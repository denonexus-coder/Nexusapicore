package com.denexus.pronewoti.nexusapicore.thread;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Convenience façade for dispatching work onto a chosen {@link NexusExecutors}
 * pool. Thin wrapper over {@link NexusThreadApi}; carries no scheduling policy
 * of its own beyond routing to the requested pool.
 */
public final class WorkScheduler {

    private final NexusThreadApi threads;

    public WorkScheduler(NexusThreadApi threads) {
        this.threads = threads;
    }

    public void submit(NexusExecutors pool, Runnable task) {
        threads.executor(pool).execute(task);
    }

    public CompletableFuture<Void> submitAsync(NexusExecutors pool, Runnable task) {
        return threads.executor(pool).runAsync(task);
    }

    public <T> CompletableFuture<T> supply(NexusExecutors pool, Supplier<T> supplier) {
        return threads.executor(pool).supplyAsync(supplier);
    }
}
