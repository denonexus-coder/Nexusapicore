package com.denexus.pronewoti.nexusapicore.module;

/**
 * Contract implemented by every future optimization module (Region, Noise,
 * Chunk, Storage, ...). NexusApiCore itself ships NO implementations - only
 * this contract plus the machinery to discover, validate, order and run them.
 *
 * <p>Lifecycle callbacks are always invoked on the server/main thread by the
 * {@link ModuleManager} and are individually guarded by error isolation.</p>
 */
public interface NexusModule {

    /**
     * Called once after the module has been validated and its dependencies are
     * satisfied. Register capabilities/hooks/services here.
     */
    void onLoad(NexusModuleContext context);

    /** Called when the module transitions to ACTIVE. */
    default void onEnable(NexusModuleContext context) {
    }

    /** Called when the module is being disabled or the Core is shutting down. */
    default void onDisable(NexusModuleContext context) {
    }
}
