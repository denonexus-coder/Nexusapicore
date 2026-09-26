package com.denexus.pronewoti.nexusapicore.capability;

import java.util.Objects;

/**
 * A capability contract: a typed slot named by a {@link CapabilityId}. The Core
 * defines the slots; modules register a provider instance of type {@code T}.
 *
 * @param <T> the provider interface a module must implement to fulfil the slot
 */
public final class Capability<T> {

    private final CapabilityId id;
    private final Class<T> type;

    public Capability(CapabilityId id, Class<T> type) {
        this.id = Objects.requireNonNull(id, "id");
        this.type = Objects.requireNonNull(type, "type");
    }

    public static <T> Capability<T> of(String id, Class<T> type) {
        return new Capability<>(CapabilityId.of(id), type);
    }

    public CapabilityId id() {
        return id;
    }

    public Class<T> type() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Capability<?> c && id.equals(c.id) && type.equals(c.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }

    @Override
    public String toString() {
        return "Capability[" + id + " : " + type.getSimpleName() + "]";
    }
}
