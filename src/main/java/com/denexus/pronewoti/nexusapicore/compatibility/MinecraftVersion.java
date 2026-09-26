package com.denexus.pronewoti.nexusapicore.compatibility;

import java.util.Objects;

/**
 * Represents a Minecraft version string. This project targets EXACTLY one
 * version: {@code 1.21.11}. No fuzzy / cross-version matching is offered on
 * purpose - see {@link #TARGET}.
 */
public final class MinecraftVersion {

    /** The one and only supported Minecraft version of PRONEWOTI. */
    public static final MinecraftVersion TARGET = new MinecraftVersion("1.21.11");

    private final String id;

    public MinecraftVersion(String id) {
        this.id = Objects.requireNonNull(id, "id").trim();
    }

    public String id() {
        return id;
    }

    /** Exact-match only. A module declaring any other version is rejected. */
    public boolean matches(MinecraftVersion other) {
        return this.id.equals(other.id);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MinecraftVersion v && id.equals(v.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id;
    }
}
