package com.denexus.pronewoti.nexusapicore.event;

/**
 * Dispatch ordering for event listeners and hook listeners. Listeners run from
 * {@link #LOWEST} to {@link #MONITOR}; {@code MONITOR} listeners run last and
 * MUST NOT mutate or cancel - they observe the final outcome only.
 */
public enum EventPriority {
    LOWEST,
    LOW,
    NORMAL,
    HIGH,
    HIGHEST,
    MONITOR
}
