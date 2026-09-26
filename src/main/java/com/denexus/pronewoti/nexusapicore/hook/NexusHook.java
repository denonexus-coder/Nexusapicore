package com.denexus.pronewoti.nexusapicore.hook;

import com.denexus.pronewoti.nexusapicore.diagnostics.NexusLogger;
import com.denexus.pronewoti.nexusapicore.event.EventPriority;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A named, typed interception point. The Core owns the hooks and dispatches
 * them from its Mixin adapters; modules only register listeners. A hook carries
 * no optimization logic - it merely forwards a context to its listeners in
 * priority order, isolating any listener failure.
 *
 * @param <T> the payload / context type dispatched through this hook
 */
public final class NexusHook<T extends HookContext> {

    /** One registered listener with its priority, owner and enabled flag. */
    public static final class Registration<T extends HookContext> {
        final HookListener<T> listener;
        final EventPriority priority;
        final String ownerModuleId;
        volatile boolean enabled = true;

        Registration(HookListener<T> listener, EventPriority priority, String ownerModuleId) {
            this.listener = listener;
            this.priority = priority;
            this.ownerModuleId = ownerModuleId;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public String ownerModuleId() {
            return ownerModuleId;
        }
    }

    private final NexusLogger logger = NexusLogger.forComponent("hook");
    private final HookId id;
    private final List<Registration<T>> registrations = new CopyOnWriteArrayList<>();
    private volatile boolean enabled = true;

    public NexusHook(HookId id) {
        this.id = id;
    }

    public HookId id() {
        return id;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Registration<T> register(String ownerModuleId, EventPriority priority, HookListener<T> listener) {
        Registration<T> reg = new Registration<>(listener, priority, ownerModuleId);
        registrations.add(reg);
        registrations.sort(Comparator.comparingInt(r -> r.priority.ordinal()));
        return reg;
    }

    public void unregister(Registration<T> registration) {
        registrations.remove(registration);
    }

    public void unregisterAllOwnedBy(String ownerModuleId) {
        registrations.removeIf(r -> r.ownerModuleId.equals(ownerModuleId));
    }

    public int listenerCount() {
        return registrations.size();
    }

    /**
     * Dispatches the context to every enabled listener in priority order. A
     * listener throwing does not stop the remaining listeners (error isolation).
     */
    public void dispatch(T context) {
        if (!enabled) {
            return;
        }
        for (Registration<T> reg : registrations) {
            if (!reg.enabled) {
                continue;
            }
            try {
                reg.listener.handle(context);
            } catch (Throwable t) {
                logger.error("hook '" + id + "' listener from module '" + reg.ownerModuleId + "' failed", t);
            }
        }
    }
}
