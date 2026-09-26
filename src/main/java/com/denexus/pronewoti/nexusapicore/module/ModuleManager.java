package com.denexus.pronewoti.nexusapicore.module;

import com.denexus.pronewoti.nexusapicore.capability.CapabilityRegistry;
import com.denexus.pronewoti.nexusapicore.compatibility.CompatibilityManager;
import com.denexus.pronewoti.nexusapicore.compatibility.CompatibilityResult;
import com.denexus.pronewoti.nexusapicore.config.NexusConfig;
import com.denexus.pronewoti.nexusapicore.dependency.DependencyResolver;
import com.denexus.pronewoti.nexusapicore.diagnostics.NexusLogger;
import com.denexus.pronewoti.nexusapicore.hook.HookRegistry;
import com.denexus.pronewoti.nexusapicore.lifecycle.LifecycleManager;
import com.denexus.pronewoti.nexusapicore.lifecycle.LifecyclePhase;
import com.denexus.pronewoti.nexusapicore.loader.ModuleLoader;
import com.denexus.pronewoti.nexusapicore.service.ServiceRegistry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Orchestrates the full module life cycle: discover -&gt; validate compatibility
 * -&gt; resolve dependency order -&gt; instantiate -&gt; load -&gt; enable, and
 * the reverse on shutdown.
 *
 * <p><b>Error isolation is the central guarantee:</b> a failure in any single
 * module marks only that module {@link ModuleState#FAILED}; it never aborts the
 * Core or other modules. NexusApiCore ships no modules of its own - this
 * machinery only manages third-party optimization modules.</p>
 */
public final class ModuleManager {

    private final NexusLogger logger = NexusLogger.forComponent("module");

    private final ModuleLoader loader;
    private final DependencyResolver resolver;
    private final CompatibilityManager compatibility;
    private final CapabilityRegistry capabilities;
    private final HookRegistry hooks;
    private final ServiceRegistry services;
    private final LifecycleManager lifecycle;
    private final NexusConfig config;

    private final Map<String, ModuleContainer> containers = new LinkedHashMap<>();
    private final List<String> loadOrder = new ArrayList<>();

    public ModuleManager(ModuleLoader loader,
                         DependencyResolver resolver,
                         CompatibilityManager compatibility,
                         CapabilityRegistry capabilities,
                         HookRegistry hooks,
                         ServiceRegistry services,
                         LifecycleManager lifecycle,
                         NexusConfig config) {
        this.loader = loader;
        this.resolver = resolver;
        this.compatibility = compatibility;
        this.capabilities = capabilities;
        this.hooks = hooks;
        this.services = services;
        this.lifecycle = lifecycle;
        this.config = config;
    }

    /** Full startup pipeline. Safe to call once after the Core is ready. */
    public synchronized void bootstrapModules(ClassLoader classLoader) {
        lifecycle.enter(LifecyclePhase.MODULE_DISCOVERY);
        List<ModuleInfo> discovered = loader.discover(classLoader);

        lifecycle.enter(LifecyclePhase.MODULE_VALIDATION);
        List<ModuleInfo> compatible = validate(discovered);

        List<String> order;
        try {
            order = resolver.resolveOrder(compatible);
        } catch (RuntimeException e) {
            logger.error("dependency resolution failed; no modules will load", e);
            return;
        }

        Map<String, ModuleInfo> byId = new LinkedHashMap<>();
        for (ModuleInfo m : compatible) {
            byId.put(m.id(), m);
        }

        lifecycle.enter(LifecyclePhase.MODULE_REGISTRATION);
        for (String id : order) {
            ModuleInfo info = byId.get(id);
            if (info == null) {
                continue;
            }
            loadOne(info, classLoader);
        }
        enableAll();
    }

    private List<ModuleInfo> validate(List<ModuleInfo> discovered) {
        List<ModuleInfo> ok = new ArrayList<>();
        for (ModuleInfo info : discovered) {
            try {
                CompatibilityResult result = compatibility.check(info);
                if (result.compatible()) {
                    ok.add(info);
                } else {
                    logger.warn("module '" + info.id() + "' incompatible: " + result.reason());
                }
            } catch (RuntimeException e) {
                logger.error("validation error for module '" + info.id() + "' (isolated)", e);
            }
        }
        return ok;
    }

    private void loadOne(ModuleInfo info, ClassLoader classLoader) {
        ModuleContainer container;
        try {
            NexusModule instance = loader.instantiate(info, classLoader);
            container = new ModuleContainer(info, instance);
            containers.put(info.id(), container);
            loadOrder.add(info.id());
        } catch (Throwable t) {
            logger.error("instantiation failed for module '" + info.id() + "' (isolated)", t);
            return;
        }

        container.setState(ModuleState.INITIALIZING);
        NexusModuleContext ctx = new ModuleContextImpl(info, capabilities, hooks, services, lifecycle, config);
        try {
            container.instance().onLoad(ctx);
            container.setState(ModuleState.DISABLED); // loaded but not yet enabled
            logger.info("loaded module '" + info.id() + "'");
        } catch (Throwable t) {
            container.fail(t);
            rollback(info.id());
            logger.error("onLoad failed for module '" + info.id() + "' (isolated, rolled back)", t);
        }
    }

    private void enableAll() {
        for (String id : loadOrder) {
            ModuleContainer c = containers.get(id);
            if (c == null || c.isFailed()) {
                continue;
            }
            NexusModuleContext ctx = new ModuleContextImpl(c.info(), capabilities, hooks, services, lifecycle, config);
            try {
                c.instance().onEnable(ctx);
                c.setState(ModuleState.ACTIVE);
            } catch (Throwable t) {
                c.fail(t);
                rollback(id);
                logger.error("onEnable failed for module '" + id + "' (isolated, rolled back)", t);
            }
        }
    }

    /** Remove any Core registrations owned by a failed module. */
    private void rollback(String moduleId) {
        capabilities.unregisterAllOwnedBy(moduleId);
        hooks.unregisterAllOwnedBy(moduleId);
        services.removeAllOwnedBy(moduleId);
    }

    /** Reverse-order disable + rollback of every module. */
    public synchronized void shutdownModules() {
        for (int i = loadOrder.size() - 1; i >= 0; i--) {
            String id = loadOrder.get(i);
            ModuleContainer c = containers.get(id);
            if (c == null) {
                continue;
            }
            c.setState(ModuleState.STOPPING);
            NexusModuleContext ctx = new ModuleContextImpl(c.info(), capabilities, hooks, services, lifecycle, config);
            try {
                c.instance().onDisable(ctx);
            } catch (Throwable t) {
                logger.error("onDisable failed for module '" + id + "' (isolated)", t);
            } finally {
                rollback(id);
                c.setState(ModuleState.STOPPED);
            }
        }
    }

    public ModuleContainer container(String id) {
        return containers.get(id);
    }

    public List<String> loadOrder() {
        return List.copyOf(loadOrder);
    }

    public int moduleCount() {
        return containers.size();
    }
}
