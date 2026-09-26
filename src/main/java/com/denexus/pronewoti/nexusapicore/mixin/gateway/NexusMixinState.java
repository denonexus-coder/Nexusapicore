package com.denexus.pronewoti.nexusapicore.mixin.gateway;

/**
 * Lifecycle/verification state of a single Mixin adapter registered in the
 * gateway.
 *
 * <ul>
 *   <li>{@code REGISTERED} - descriptor known, not yet confirmed active.</li>
 *   <li>{@code ACTIVE} - the Mixin class was applied and dispatched at least
 *       once (observed at runtime).</li>
 *   <li>{@code VERIFIED} - target class/method/injection point were confirmed
 *       against the Yarn 1.21.11 mappings used by this build.</li>
 *   <li>{@code UNVERIFIED} - target could NOT be confirmed here and must be
 *       validated against the real mappings before being trusted.</li>
 *   <li>{@code FAILED} - the adapter errored and was disabled (isolated).</li>
 * </ul>
 */
public enum NexusMixinState {
    REGISTERED,
    ACTIVE,
    VERIFIED,
    UNVERIFIED,
    FAILED
}
