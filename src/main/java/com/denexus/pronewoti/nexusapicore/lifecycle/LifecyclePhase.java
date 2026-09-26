package com.denexus.pronewoti.nexusapicore.lifecycle;

/**
 * The ordered set of phases the Core moves through, from process bootstrap to
 * shutdown. Modules observe these phases through {@link LifecycleListener}.
 */
public enum LifecyclePhase {
    BOOTSTRAP,
    CORE_INITIALIZING,
    CORE_READY,
    MODULE_DISCOVERY,
    MODULE_VALIDATION,
    MODULE_REGISTRATION,
    CAPABILITY_REGISTRATION,
    HOOK_REGISTRATION,
    SERVICE_REGISTRATION,
    GAME_READY,
    SERVER_START,
    WORLD_START,
    WORLD_READY,
    WORLD_STOP,
    SERVER_STOP,
    SHUTDOWN
}
