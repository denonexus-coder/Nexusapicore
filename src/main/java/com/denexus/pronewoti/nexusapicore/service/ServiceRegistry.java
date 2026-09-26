package com.denexus.pronewoti.nexusapicore.service;

import com.denexus.pronewoti.nexusapicore.api.exception.NexusServiceException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe registry mapping service id -&gt; instance, plus the owning module.
 * Supports register/get/find/remove/contains as required by the API spec.
 */
public final class ServiceRegistry {

    private record Entry(NexusService service, String ownerModuleId) {
    }

    private final ConcurrentHashMap<String, Entry> services = new ConcurrentHashMap<>();

    public void register(String ownerModuleId, NexusService service) {
        if (service == null) {
            throw new NexusServiceException("null service");
        }
        Entry existing = services.putIfAbsent(service.id(), new Entry(service, ownerModuleId));
        if (existing != null) {
            throw new NexusServiceException(
                    "service '" + service.id() + "' already registered by module '" + existing.ownerModuleId() + "'");
        }
        try {
            service.start();
        } catch (Throwable t) {
            services.remove(service.id());
            throw new NexusServiceException("service '" + service.id() + "' failed to start", t);
        }
    }

    public Optional<NexusService> get(String id) {
        Entry e = services.get(id);
        return e == null ? Optional.empty() : Optional.of(e.service());
    }

    public <T extends NexusService> Optional<T> find(String id, Class<T> type) {
        return get(id).filter(type::isInstance).map(type::cast);
    }

    public <T extends NexusService> List<T> findAll(Class<T> type) {
        return services.values().stream()
                .map(Entry::service)
                .filter(type::isInstance)
                .map(type::cast)
                .collect(Collectors.toList());
    }

    public boolean contains(String id) {
        return services.containsKey(id);
    }

    public void remove(String id) {
        Entry e = services.remove(id);
        if (e != null) {
            try {
                e.service().stop();
            } catch (Throwable ignored) {
                // stop is best-effort; failures must not propagate.
            }
        }
    }

    public void removeAllOwnedBy(String ownerModuleId) {
        services.values().stream()
                .filter(e -> e.ownerModuleId().equals(ownerModuleId))
                .map(e -> e.service().id())
                .collect(Collectors.toList())
                .forEach(this::remove);
    }

    public int size() {
        return services.size();
    }
}
