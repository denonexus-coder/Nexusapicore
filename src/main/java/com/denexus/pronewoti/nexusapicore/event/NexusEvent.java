package com.denexus.pronewoti.nexusapicore.event;

/**
 * Base type for events posted on the {@link NexusEventBus}. Events are Core-side
 * notifications; unlike hooks (which are wired to Mixin adapters) events are a
 * lighter, in-process publish/subscribe mechanism used between Core subsystems
 * and modules.
 */
public abstract class NexusEvent {

    private final String name;

    protected NexusEvent(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }
}
