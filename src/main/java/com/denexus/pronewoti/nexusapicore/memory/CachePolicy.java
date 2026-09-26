package com.denexus.pronewoti.nexusapicore.memory;

/**
 * Describes how a registered cache should be trimmed under memory pressure.
 * {@code trim(level)} returns the fraction of entries the cache should evict
 * at the given pressure level (0.0 = keep all, 1.0 = clear all).
 */
@FunctionalInterface
public interface CachePolicy {

    double trimFraction(MemoryPressure level);

    /** Default policy: evict progressively as pressure rises. */
    static CachePolicy progressive() {
        return level -> switch (level) {
            case LOW -> 0.0;
            case MODERATE -> 0.10;
            case HIGH -> 0.40;
            case CRITICAL -> 0.90;
        };
    }
}
