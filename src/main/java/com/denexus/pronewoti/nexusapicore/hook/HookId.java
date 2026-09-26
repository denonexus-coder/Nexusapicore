package com.denexus.pronewoti.nexusapicore.hook;

import java.util.Objects;

/** Namespaced identifier of a hook, e.g. {@code chunk.save}. */
public record HookId(String value) {

    public HookId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("hook id must not be blank");
        }
    }

    public static HookId of(String value) {
        return new HookId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
