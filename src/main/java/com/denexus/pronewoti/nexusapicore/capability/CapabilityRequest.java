package com.denexus.pronewoti.nexusapicore.capability;

import java.util.Objects;

/**
 * A request issued by a module to obtain the provider bound to a capability.
 * {@code required} controls whether an unfulfilled request is an error.
 */
public record CapabilityRequest<T>(Capability<T> capability, boolean required) {

    public CapabilityRequest {
        Objects.requireNonNull(capability, "capability");
    }

    public static <T> CapabilityRequest<T> required(Capability<T> capability) {
        return new CapabilityRequest<>(capability, true);
    }

    public static <T> CapabilityRequest<T> optional(Capability<T> capability) {
        return new CapabilityRequest<>(capability, false);
    }
}
