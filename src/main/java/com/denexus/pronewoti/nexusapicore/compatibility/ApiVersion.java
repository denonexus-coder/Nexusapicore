package com.denexus.pronewoti.nexusapicore.compatibility;

import java.util.Objects;

/**
 * A minimal, strict semantic version (MAJOR.MINOR.PATCH) used for the
 * NexusApiCore API contract version and for module {@code apiVersion} fields.
 */
public final class ApiVersion implements Comparable<ApiVersion> {

    private final int major;
    private final int minor;
    private final int patch;

    public ApiVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public static ApiVersion parse(String raw) {
        Objects.requireNonNull(raw, "version");
        String[] parts = raw.trim().split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Expected MAJOR.MINOR.PATCH, got: " + raw);
        }
        try {
            return new ApiVersion(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Non-numeric version component in: " + raw, e);
        }
    }

    public int major() { return major; }
    public int minor() { return minor; }
    public int patch() { return patch; }

    /**
     * Semantic-versioning compatibility: same MAJOR, and this (the Core) is at
     * least as new as the required version.
     */
    public boolean isCompatibleWith(ApiVersion required) {
        if (this.major != required.major) {
            return false;
        }
        return this.compareTo(required) >= 0;
    }

    @Override
    public int compareTo(ApiVersion o) {
        if (major != o.major) return Integer.compare(major, o.major);
        if (minor != o.minor) return Integer.compare(minor, o.minor);
        return Integer.compare(patch, o.patch);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApiVersion other)) return false;
        return major == other.major && minor == other.minor && patch == other.patch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch);
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}
