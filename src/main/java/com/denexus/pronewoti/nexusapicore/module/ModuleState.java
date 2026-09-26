package com.denexus.pronewoti.nexusapicore.module;

/**
 * Lifecycle state of a single module inside the Core. Transitions are driven by
 * {@link ModuleManager}. FAILED is terminal for a module but never propagates
 * to the Core (error isolation).
 */
public enum ModuleState {
    DISCOVERED,
    VALIDATING,
    WAITING_DEPENDENCY,
    REGISTERING,
    INITIALIZING,
    ACTIVE,
    DISABLED,
    FAILED,
    STOPPING,
    STOPPED
}
