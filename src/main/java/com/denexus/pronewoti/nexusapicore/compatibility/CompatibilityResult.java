package com.denexus.pronewoti.nexusapicore.compatibility;

/**
 * Immutable outcome of a compatibility check. When {@link #compatible()} is
 * false, {@link #reason()} explains exactly why so the failure can be surfaced
 * in diagnostics.
 */
public record CompatibilityResult(boolean compatible, String reason) {

    public static CompatibilityResult ok() {
        return new CompatibilityResult(true, "compatible");
    }

    public static CompatibilityResult fail(String reason) {
        return new CompatibilityResult(false, reason);
    }
}
