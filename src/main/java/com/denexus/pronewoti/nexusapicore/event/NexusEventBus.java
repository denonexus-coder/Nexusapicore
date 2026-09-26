package com.denexus.pronewoti.nexusapicore.event;

import com.denexus.pronewoti.nexusapicore.diagnostics.NexusLogger;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Synchronous, priority-ordered publish/subscribe bus. Listeners run from
 * LOWEST to MONITOR. For {@link CancellableEvent}s, once cancelled, listeners
 * that opted out of cancelled delivery are skipped, but MONITOR listeners always
 * observe the final state. Listener failures are isolated.
 */
public final class NexusEventBus {

    private record Sub<E extends NexusEvent>(EventPriority priority, boolean receiveCancelled,
                                             String ownerModuleId, EventListener<E> listener) {
    }

    private final NexusLogger logger = NexusLogger.forComponent("eventbus");
    private final Map<Class<?>, List<Sub<?>>> subscribers = new ConcurrentHashMap<>();

    public <E extends NexusEvent> void register(Class<E> type, EventListener<E> listener) {
        register(type, EventPriority.NORMAL, true, "core", listener);
    }

    public <E extends NexusEvent> void register(Class<E> type, EventPriority priority,
                                                boolean receiveCancelled, String ownerModuleId,
                                                EventListener<E> listener) {
        List<Sub<?>> list = subscribers.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>());
        list.add(new Sub<>(priority, receiveCancelled, ownerModuleId, listener));
        list.sort(Comparator.comparingInt(s -> s.priority().ordinal()));
    }

    public void unregisterAllOwnedBy(String ownerModuleId) {
        for (List<Sub<?>> list : subscribers.values()) {
            list.removeIf(s -> s.ownerModuleId().equals(ownerModuleId));
        }
    }

    @SuppressWarnings("unchecked")
    public <E extends NexusEvent> E post(E event) {
        List<Sub<?>> list = subscribers.get(event.getClass());
        if (list == null) {
            return event;
        }
        boolean cancellable = event instanceof CancellableEvent;
        for (Sub<?> raw : list) {
            Sub<E> sub = (Sub<E>) raw;
            boolean cancelled = cancellable && ((CancellableEvent) event).isCancelled();
            boolean monitor = sub.priority() == EventPriority.MONITOR;
            if (cancelled && !sub.receiveCancelled() && !monitor) {
                continue;
            }
            try {
                sub.listener().onEvent(event);
            } catch (Throwable t) {
                logger.error("event listener for " + event.name() + " from module '"
                        + sub.ownerModuleId() + "' failed", t);
            }
        }
        return event;
    }

    public int subscriberCount(Class<?> type) {
        List<Sub<?>> list = subscribers.get(type);
        return list == null ? 0 : list.size();
    }
}
