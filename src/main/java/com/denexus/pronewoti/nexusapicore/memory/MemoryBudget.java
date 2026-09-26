package com.denexus.pronewoti.nexusapicore.memory;

/**
 * Tracks the Core memory budget and maps live heap usage onto a
 * {@link MemoryPressure} level. The budget is a soft target (typically the JVM
 * max heap); the pressure signal lets caches shrink before the JVM is forced
 * into aggressive GC.
 */
public final class MemoryBudget {

    private volatile long budgetBytes;

    public MemoryBudget(long budgetBytes) {
        this.budgetBytes = Math.max(1L, budgetBytes);
    }

    public long budgetBytes() {
        return budgetBytes;
    }

    public void setBudgetBytes(long budgetBytes) {
        this.budgetBytes = Math.max(1L, budgetBytes);
    }

    /** Fraction of the budget currently used, in [0.0, 1.0+]. */
    public double usedFraction() {
        Runtime rt = Runtime.getRuntime();
        long used = rt.totalMemory() - rt.freeMemory();
        return (double) used / (double) budgetBytes;
    }

    public MemoryPressure pressure() {
        double f = usedFraction();
        if (f >= 0.95) return MemoryPressure.CRITICAL;
        if (f >= 0.85) return MemoryPressure.HIGH;
        if (f >= 0.65) return MemoryPressure.MODERATE;
        return MemoryPressure.LOW;
    }
}
