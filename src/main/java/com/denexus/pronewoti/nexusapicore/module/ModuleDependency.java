package com.denexus.pronewoti.nexusapicore.module;

import java.util.Objects;

/**
 * A dependency edge declared by a module manifest. {@code optional} marks a
 * soft dependency that only influences ordering when present.
 */
public record ModuleDependency(String id, boolean optional) {

    public ModuleDependency {
        Objects.requireNonNull(id, "id");
    }

    public static ModuleDependency required(String id) {
        return new ModuleDependency(id, false);
    }

    public static ModuleDependency optional(String id) {
        return new ModuleDependency(id, true);
    }
}
