package com.denexus.pronewoti.nexusapicore.bootstrap;

import com.denexus.pronewoti.nexusapicore.capability.CapabilityRegistry;
import com.denexus.pronewoti.nexusapicore.compatibility.CompatibilityManager;
import com.denexus.pronewoti.nexusapicore.config.ConfigManager;
import com.denexus.pronewoti.nexusapicore.config.NexusConfig;
import com.denexus.pronewoti.nexusapicore.dependency.DependencyResolver;
import com.denexus.pronewoti.nexusapicore.diagnostics.NexusDiagnostics;
import com.denexus.pronewoti.nexusapicore.diagnostics.NexusLogger;
import com.denexus.pronewoti.nexusapicore.hook.HookRegistry;
import com.denexus.pronewoti.nexusapicore.lifecycle.LifecycleManager;
import com.denexus.pronewoti.nexusapicore.lifecycle.LifecyclePhase;
import com.denexus.pronewoti.nexusapicore.loader.ModuleLoader;
import com.denexus.pronewoti.nexusapicore.memory.NexusMemoryApi;
import com.denexus.pronewoti.nexusapicore.metrics.NexusMetrics;
import com.denexus.pronewoti.nexusapicore.mixin.gateway.NexusMixinGateway;
import com.denexus.pronewoti.nexusapicore.mixin.gateway.NexusMixinRegistry;
import com.denexus.pronewoti.nexusapicore.module.ModuleManager;
import com.denexus.pronewoti.nexusapicore.service.ServiceRegistry;
import com.denexus.pronewoti.nexusapicore.thread.NexusThreadApi;

import java.nio.file.Path;

/**
 * The Core object graph and its single process-wide accessor.
 *
 * <p>{@code NexusApiCore} owns every subsystem (config, threads, memory,
 * registries, module manager, diagnostics, mixin registry) and wires them
 * together. It ships NO optimization behaviour - it is pure infrastructure that
 * discovers, validates and runs third-party modules with strict error
 * isolation.</p>
 *
 * <p>Construction is cheap; {@link #initialize(Path)} performs startup and
 * {@link #shutdown()} tears everything down in reverse. A single instance is
 * held statically because the Fabric entrypoints and the Mixin gateway need a
 * common anchor.</p>
 */
public final class NexusApiCore {

    private static volatile NexusApiCore instance;

    private final NexusLogger logger = NexusLogger.forComponent("core");
    private final NexusEnvironment environment;

    // ---- subsystems --------------------------------------------------------
    private final LifecycleManager lifecycle = new LifecycleManager();
    private final ConfigManager configManager = new ConfigManager();
    private final NexusThreadApi threads = new NexusThreadApi();
    private final NexusMemoryApi memory = new NexusMemoryApi();
    private final CapabilityRegistry capabilities = new CapabilityRegistry();
    private final HookRegistry hooks = new HookRegistry();
    private final ServiceRegistry services = new ServiceRegistry();
    private final NexusDiagnostics diagnostics = new NexusDiagnostics();
    private final NexusMixinRegistry mixins = new NexusMixinRegistry();
    private final CompatibilityManager compatibility =
            new CompatibilityManager(NexusVersion.API, NexusVersion.MINECRAFT);

    private NexusConfig config;
    private ModuleManager moduleManager;
    private volatile boolean initialized;

    private NexusApiCore(NexusEnvironment environment) {
        this.environment = environment;
    }

    // ---- singleton ---------------------------------------------------------
    public static synchronized NexusApiCore create(NexusEnvironment environment) {
        if (instance == null) {
            instance = new NexusApiCore(environment);
        }
        return instance;
    }

    public static NexusApiCore get() {
        NexusApiCore c = instance;
        if (c == null) {
            throw new IllegalStateException("NexusApiCore not created yet");
        }
        return c;
    }

    public static boolean isPresent() {
        return instance != null;
    }

    // ---- lifecycle ---------------------------------------------------------

    /**
     * Bring the Core up: load config, start threads/memory, bind the mixin
     * gateway, then discover and run modules. Idempotent.
     *
     * @param configFile optional properties override file (may be null)
     */
    public synchronized void initialize(Path configFile) {
        if (initialized) {
            return;
        }
        logger.info("initializing " + NexusVersion.describe() + " [" + environment + "]");
        lifecycle.enter(LifecyclePhase.CORE_INITIALIZING);

        this.config = configManager.load(configFile);
        diagnostics.setEnabled(config.getBoolean(ConfigManager.DIAGNOSTICS_ENABLED, true));
        diagnostics.metrics().setEnabled(config.getBoolean(ConfigManager.METRICS_ENABLED, true));

        threads.start(config);
        memory.start(config);

        this.moduleManager = new ModuleManager(
                new ModuleLoader(), new DependencyResolver(), compatibility,
                capabilities, hooks, services, lifecycle, config);

        NexusMixinGateway.bind(hooks, mixins);

        lifecycle.enter(LifecyclePhase.CORE_READY);
        this.initialized = true;

        // Discover + run modules against the Core's own class loader.
        moduleManager.bootstrapModules(getClass().getClassLoader());
        lifecycle.enter(LifecyclePhase.GAME_READY);
        logger.info("Core ready: " + moduleManager.moduleCount() + " module(s), "
                + hooks.size() + " hook(s), " + capabilities.size() + " capability(ies)");
    }

    /** Reverse-order teardown. Safe to call multiple times. */
    public synchronized void shutdown() {
        if (!initialized) {
            return;
        }
        lifecycle.enter(LifecyclePhase.SHUTDOWN);
        if (moduleManager != null) {
            moduleManager.shutdownModules();
        }
        NexusMixinGateway.unbind();
        threads.shutdown();
        this.initialized = false;
        logger.info("Core shut down");
    }

    // ---- accessors ---------------------------------------------------------
    public NexusEnvironment environment() { return environment; }
    public LifecycleManager lifecycle() { return lifecycle; }
    public NexusConfig config() { return config; }
    public ConfigManager configManager() { return configManager; }
    public NexusThreadApi threads() { return threads; }
    public NexusMemoryApi memory() { return memory; }
    public CapabilityRegistry capabilities() { return capabilities; }
    public HookRegistry hooks() { return hooks; }
    public ServiceRegistry services() { return services; }
    public NexusDiagnostics diagnostics() { return diagnostics; }
    public NexusMetrics metrics() { return diagnostics.metrics(); }
    public NexusMixinRegistry mixins() { return mixins; }
    public CompatibilityManager compatibility() { return compatibility; }
    public ModuleManager moduleManager() { return moduleManager; }
    public boolean isInitialized() { return initialized; }
}
