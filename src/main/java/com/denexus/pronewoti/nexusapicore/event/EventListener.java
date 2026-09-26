package com.denexus.pronewoti.nexusapicore.event;

/**
 * A subscriber to a specific event type.
 *
 * @param <E> the event type handled
 */
@FunctionalInterface
public interface EventListener<E extends NexusEvent> {
    void onEvent(E event);
}
