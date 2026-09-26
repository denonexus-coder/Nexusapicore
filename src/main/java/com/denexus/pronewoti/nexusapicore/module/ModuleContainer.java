package com.denexus.pronewoti.nexusapicore.module;

/**
 * Runtime wrapper around a single module: its manifest, its instance, its
 * current {@link ModuleState} and (if it failed) the isolated failure cause.
 * Mutable state is confined to the {@link ModuleManager} which owns it.
 */
public final class ModuleContainer {

    private final ModuleInfo info;
    private final NexusModule instance;
    private volatile ModuleState state = ModuleState.DISCOVERED;
    private volatile Throwable failure;

    public ModuleContainer(ModuleInfo info, NexusModule instance) {
        this.info = info;
        this.instance = instance;
    }

    public ModuleInfo info() {
        return info;
    }

    public NexusModule instance() {
        return instance;
    }

    public ModuleState state() {
        return state;
    }

    public void setState(ModuleState state) {
        this.state = state;
    }

    public Throwable failure() {
        return failure;
    }

    public void fail(Throwable failure) {
        this.failure = failure;
        this.state = ModuleState.FAILED;
    }

    public boolean isFailed() {
        return state == ModuleState.FAILED;
    }
}
