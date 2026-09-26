package com.denexus.pronewoti.nexusapicore.module;

import com.denexus.pronewoti.nexusapicore.capability.CapabilityRegistry;
import com.denexus.pronewoti.nexusapicore.config.ModuleConfig;
import com.denexus.pronewoti.nexusapicore.config.NexusConfig;
import com.denexus.pronewoti.nexusapicore.diagnostics.NexusLogger;
import com.denexus.pronewoti.nexusapicore.hook.HookRegistry;
import com.denexus.pronewoti.nexusapicore.lifecycle.LifecycleManager;
import com.denexus.pronewoti.nexusapicore.service.ServiceRegistry;

/**
 * Default {@link NexusModuleContext} implementation. It exposes the shared Core
 * registries but scopes config to the module's namespace and tags a logger with
 * the module id. Created by {@link ModuleManager}; one instance per module.
 */
public final class ModuleContextImpl implements NexusModuleContext {

    private final ModuleInfo info;
    private final NexusLogger logger;
    private final CapabilityRegistry capabilities;
    private final HookRegistry hooks;
    private final ServiceRegistry services;
    private final LifecycleManager lifecycle;
    private final ModuleConfig config;

    public ModuleContextImpl(ModuleInfo info,
                             CapabilityRegistry capabilities,
                             HookRegistry hooks,
                             ServiceRegistry services,
                             LifecycleManager lifecycle,
                             NexusConfig backingConfig) {
        this.info = info;
        this.logger = NexusLogger.forComponent("module/" + info.id());
        this.capabilities = capabilities;
        this.hooks = hooks;
        this.services = services;
        this.lifecycle = lifecycle;
        this.config = new ModuleConfig(backingConfig, info.id());
    }

    @Override
    public ModuleInfo moduleInfo() {
        return info;
    }

    @Override
    public NexusLogger logger() {
        return logger;
    }

    @Override
    public CapabilityRegistry capabilities() {
        return capabilities;
    }

    @Override
    public HookRegistry hooks() {
        return hooks;
    }

    @Override
    public ServiceRegistry services() {
        return services;
    }

    @Override
    public LifecycleManager lifecycle() {
        return lifecycle;
    }

    @Override
    public ModuleConfig config() {
        return config;
    }
}
