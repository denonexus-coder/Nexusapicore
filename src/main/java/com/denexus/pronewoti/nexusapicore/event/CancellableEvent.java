package com.denexus.pronewoti.nexusapicore.event;

/**
 * An event whose default action can be vetoed by a listener. Cancellation is
 * only meaningful where it is semantically valid - the Core does not create
 * cancellable events for observations that cannot actually be prevented.
 */
public abstract class CancellableEvent extends NexusEvent {

    private boolean cancelled;

    protected CancellableEvent(String name) {
        super(name);
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
