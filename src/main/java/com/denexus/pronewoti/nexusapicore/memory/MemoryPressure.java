package com.denexus.pronewoti.nexusapicore.memory;

/**
 * Coarse memory-pressure levels derived from heap usage. Modules react to
 * {@link #HIGH}/{@link #CRITICAL} by trimming their caches. Thresholds are
 * evaluated by {@link MemoryBudget}.
 */
public enum MemoryPressure {
    LOW,
    MODERATE,
    HIGH,
    CRITICAL
}
