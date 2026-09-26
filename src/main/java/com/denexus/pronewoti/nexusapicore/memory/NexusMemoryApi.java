package com.denexus.pronewoti.nexusapicore.memory;

import com.denexus.pronewoti.nexusapicore.config.ConfigManager;
import com.denexus.pronewoti.nexusapicore.config.NexusConfig;
import com.denexus.pronewoti.nexusapicore.diagnostics.NexusLogger;

/**
 * Public memory-management façade. Owns the {@link MemoryBudget} and
 * {@link CacheManager}; a single {@link #poll()} call samples pressure and, when
 * it rises above {@link MemoryPressure#LOW}, asks registered caches to trim.
 * Polling is driven externally (e.g. from a server-tick hook) - the Core starts
 * no timer thread of its own here.
 */
public final class NexusMemoryApi {

    private final NexusLogger logger = NexusLogger.forComponent("memory");
    private final CacheManager cacheManager = new CacheManager();
    private MemoryBudget budget;
    private volatile MemoryPressure lastPressure = MemoryPressure.LOW;

    public void start(NexusConfig config) {
        long budgetBytes = config.getLong(ConfigManager.MEMORY_BUDGET_BYTES, Runtime.getRuntime().maxMemory());
        this.budget = new MemoryBudget(budgetBytes);
        logger.info("memory budget set to " + budgetBytes + " bytes");
    }

    public CacheManager caches() {
        return cacheManager;
    }

    public MemoryBudget budget() {
        return budget;
    }

    public MemoryPressure lastPressure() {
        return lastPressure;
    }

    /** Sample current pressure and trigger cache trimming if needed. */
    public MemoryPressure poll() {
        if (budget == null) {
            return MemoryPressure.LOW;
        }
        MemoryPressure level = budget.pressure();
        this.lastPressure = level;
        if (level != MemoryPressure.LOW) {
            cacheManager.applyPressure(level);
        }
        return level;
    }
}
