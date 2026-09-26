package com.denexus.pronewoti.nexusapicore.memory;

import com.denexus.pronewoti.nexusapicore.diagnostics.NexusLogger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.DoubleConsumer;

/**
 * Registry of trimmable caches. Modules register an eviction callback plus a
 * {@link CachePolicy}; on {@link #applyPressure(MemoryPressure)} the manager
 * asks each cache to evict the policy-dictated fraction of its entries.
 * The Core itself owns no cache data - it only coordinates trimming.
 */
public final class CacheManager {

    /** A module-supplied cache that can shed a fraction of its contents. */
    private record Entry(CachePolicy policy, DoubleConsumer evictor) {
    }

    private final NexusLogger logger = NexusLogger.forComponent("memory");
    private final Map<String, Entry> caches = new ConcurrentHashMap<>();

    /**
     * @param name    unique cache id (usually {@code moduleId:cacheName})
     * @param policy  how aggressively to trim per pressure level
     * @param evictor called with the fraction [0..1] of entries to drop
     */
    public void register(String name, CachePolicy policy, DoubleConsumer evictor) {
        caches.put(name, new Entry(policy, evictor));
        logger.debug("registered cache: " + name);
    }

    public void unregister(String name) {
        caches.remove(name);
    }

    public void applyPressure(MemoryPressure level) {
        for (Map.Entry<String, Entry> e : caches.entrySet()) {
            double fraction = e.getValue().policy().trimFraction(level);
            if (fraction > 0.0) {
                try {
                    e.getValue().evictor().accept(fraction);
                } catch (RuntimeException ex) {
                    logger.warn("cache '" + e.getKey() + "' eviction failed: " + ex.getMessage());
                }
            }
        }
    }

    public int registeredCount() {
        return caches.size();
    }
}
