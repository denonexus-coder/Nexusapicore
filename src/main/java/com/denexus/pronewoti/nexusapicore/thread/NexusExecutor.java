package com.denexus.pronewoti.nexusapicore.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * A named, bounded worker pool. Threads are daemon threads named for easy
 * profiling, e.g. {@code nexus-WORLD_GENERATION-1}. Wraps a fixed thread pool
 * and offers clean shutdown.
 */
public final class NexusExecutor {

    private final NexusExecutors kind;
    private final ExecutorService delegate;

    public NexusExecutor(NexusExecutors kind, int size) {
        this.kind = kind;
        int poolSize = Math.max(1, size);
        this.delegate = Executors.newFixedThreadPool(poolSize, namedDaemonFactory(kind.name()));
    }

    private static ThreadFactory namedDaemonFactory(String poolName) {
        AtomicInteger counter = new AtomicInteger(1);
        return r -> {
            Thread t = new Thread(r, "nexus-" + poolName + "-" + counter.getAndIncrement());
            t.setDaemon(true);
            return t;
        };
    }

    public NexusExecutors kind() {
        return kind;
    }

    public void execute(Runnable task) {
        delegate.execute(task);
    }

    public <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, delegate);
    }

    public CompletableFuture<Void> runAsync(Runnable task) {
        return CompletableFuture.runAsync(task, delegate);
    }

    /** Orderly shutdown: stop accepting work, then wait briefly for drain. */
    public void shutdown() {
        delegate.shutdown();
        try {
            if (!delegate.awaitTermination(5, TimeUnit.SECONDS)) {
                delegate.shutdownNow();
            }
        } catch (InterruptedException e) {
            delegate.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public boolean isShutdown() {
        return delegate.isShutdown();
    }
}
