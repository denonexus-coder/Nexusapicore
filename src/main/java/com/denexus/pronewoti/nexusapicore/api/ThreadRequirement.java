package com.denexus.pronewoti.nexusapicore.api;

/**
 * Declares on which thread a piece of API state may be safely accessed.
 *
 * <p>Every {@code *Context} object handed to a module MUST document its
 * {@link ThreadRequirement}. Modules MUST NOT touch Minecraft state from a
 * thread that does not match the declared requirement.</p>
 */
public enum ThreadRequirement {
    /** Client render / main thread only. */
    MAIN_THREAD,
    /** Logical server thread only (integrated or dedicated). */
    SERVER_THREAD,
    /** Any Nexus worker pool thread. */
    WORKER_THREAD,
    /** Dedicated IO thread. */
    IO_THREAD,
    /** Thread-agnostic; the value is immutable or internally synchronized. */
    ANY
}
