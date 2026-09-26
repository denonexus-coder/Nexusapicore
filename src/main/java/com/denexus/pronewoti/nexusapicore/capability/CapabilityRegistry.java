package com.denexus.pronewoti.nexusapicore.capability;

import com.denexus.pronewoti.nexusapicore.api.exception.NexusCapabilityException;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores the provider instance bound to each capability slot, together with the
 * id of the owning module. Thread-safe. Holds NO optimization logic - only the
 * bindings between contracts and their providers.
 */
public final class CapabilityRegistry {

    private record Binding(Object provider, String ownerModuleId) {
    }

    private final ConcurrentHashMap<CapabilityId, Binding> bindings = new ConcurrentHashMap<>();

    public <T> void register(String ownerModuleId, Capability<T> capability, T provider) {
        if (provider == null) {
            throw new NexusCapabilityException("null provider for " + capability.id());
        }
        if (!capability.type().isInstance(provider)) {
            throw new NexusCapabilityException(
                    "provider does not implement " + capability.type().getName() + " for " + capability.id());
        }
        Binding existing = bindings.putIfAbsent(capability.id(), new Binding(provider, ownerModuleId));
        if (existing != null) {
            throw new NexusCapabilityException(
                    "capability " + capability.id() + " already provided by module '" + existing.ownerModuleId() + "'");
        }
    }

    public <T> Optional<T> find(Capability<T> capability) {
        Binding binding = bindings.get(capability.id());
        if (binding == null) {
            return Optional.empty();
        }
        return Optional.of(capability.type().cast(binding.provider()));
    }

    public <T> T resolve(CapabilityRequest<T> request) {
        Optional<T> found = find(request.capability());
        if (found.isEmpty() && request.required()) {
            throw new NexusCapabilityException(
                    "required capability not available: " + request.capability().id());
        }
        return found.orElse(null);
    }

    public boolean isRegistered(CapabilityId id) {
        return bindings.containsKey(id);
    }

    public void unregisterAllOwnedBy(String ownerModuleId) {
        bindings.values().removeIf(b -> b.ownerModuleId().equals(ownerModuleId));
    }

    public int size() {
        return bindings.size();
    }
}
