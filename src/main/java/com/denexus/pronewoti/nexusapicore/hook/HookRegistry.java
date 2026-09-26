package com.denexus.pronewoti.nexusapicore.hook;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central registry of all {@link NexusHook}s. Core subsystems (and their Mixin
 * adapters) create hooks here; modules look them up to register listeners.
 */
public final class HookRegistry {

    private final ConcurrentHashMap<HookId, NexusHook<?>> hooks = new ConcurrentHashMap<>();

    /** Gets an existing hook or creates it on demand. */
    @SuppressWarnings("unchecked")
    public <T extends HookContext> NexusHook<T> getOrCreate(HookId id) {
        return (NexusHook<T>) hooks.computeIfAbsent(id, NexusHook::new);
    }

    @SuppressWarnings("unchecked")
    public <T extends HookContext> Optional<NexusHook<T>> find(HookId id) {
        return Optional.ofNullable((NexusHook<T>) hooks.get(id));
    }

    public boolean contains(HookId id) {
        return hooks.containsKey(id);
    }

    public int size() {
        return hooks.size();
    }

    public void unregisterAllOwnedBy(String ownerModuleId) {
        for (NexusHook<?> hook : hooks.values()) {
            hook.unregisterAllOwnedBy(ownerModuleId);
        }
    }

    public java.util.Set<HookId> ids() {
        return java.util.Set.copyOf(hooks.keySet());
    }
}
