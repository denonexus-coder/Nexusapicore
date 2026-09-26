package com.denexus.pronewoti.nexusapicore.bootstrap;

/**
 * The physical side the Core is running on. Set once during bootstrap from the
 * Fabric entrypoint that fired. Kept independent of Fabric types so the Core is
 * unit-testable without a Fabric runtime.
 */
public enum NexusEnvironment {
    /** Dedicated or integrated server side (common entrypoint). */
    SERVER,
    /** Physical client side (client entrypoint). */
    CLIENT,
    /** Not yet determined. */
    UNKNOWN
}
