package com.denexus.pronewoti.nexusapicore.thread;

import com.denexus.pronewoti.nexusapicore.config.NexusConfig;
import com.denexus.pronewoti.nexusapicore.diagnostics.NexusLogger;

import java.util.EnumMap;
import java.util.Map;

/**
 * Creates and owns the logical worker pools, sized from {@link NexusConfig}.
 * Provides the single entry point modules use to run background work, and
 * guarantees a clean shutdown of every pool.
 */
public final class NexusThreadApi {

    private final NexusLogger logger = NexusLogger.forComponent("thread");
    private final Map<NexusExecutors, NexusExecutor> pools = new EnumMap<>(NexusExecutors.class);
    private volatile boolean started;

    public synchronized void start(NexusConfig config) {
        if (started) {
            return;
        }
        for (NexusExecutors kind : NexusExecutors.values()) {
            int size = config.getInt(kind.sizeConfigKey(), 1);
            pools.put(kind, new NexusExecutor(kind, size));
        }
        started = true;
        logger.info("started " + pools.size() + " worker pools");
    }

    public NexusExecutor executor(NexusExecutors kind) {
        NexusExecutor e = pools.get(kind);
        if (e == null) {
            throw new IllegalStateException("thread pools not started or unknown pool: " + kind);
        }
        return e;
    }

    public synchronized void shutdown() {
        for (NexusExecutor e : pools.values()) {
            e.shutdown();
        }
        pools.clear();
        started = false;
        logger.info("all worker pools shut down");
    }

    public boolean isStarted() {
        return started;
    }

    public int poolCount() {
        return pools.size();
    }
}
