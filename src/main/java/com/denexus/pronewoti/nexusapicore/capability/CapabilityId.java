package com.denexus.pronewoti.nexusapicore.capability;

import java.util.Objects;

/**
 * A namespaced capability identifier, e.g. {@code chunk.load}. Capability ids
 * are pure contracts - they name a slot that a future module can fill.
 */
public record CapabilityId(String value) {

    public CapabilityId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("capability id must not be blank");
        }
    }

    public static CapabilityId of(String value) {
        return new CapabilityId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
