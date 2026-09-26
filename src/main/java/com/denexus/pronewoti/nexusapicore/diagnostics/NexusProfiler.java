package com.denexus.pronewoti.nexusapicore.diagnostics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * Lightweight, allocation-free-ish timing aggregator. Each named section keeps a
 * call count and cumulative nanoseconds. Designed to be cheap enough to leave on
 * in production; sampling policy is left to the caller. Holds no optimization
 * logic - it only measures.
 */
public final class NexusProfiler {

    /** Aggregated stats for one named section. */
    public record Sample(String name, long count, long totalNanos) {
        public double averageNanos() {
            return count == 0 ? 0.0 : (double) totalNanos / (double) count;
        }
    }

    private static final class Counter {
        final LongAdder count = new LongAdder();
        final LongAdder nanos = new LongAdder();
    }

    private final Map<String, Counter> sections = new ConcurrentHashMap<>();
    private volatile boolean enabled = true;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void record(String section, long nanos) {
        if (!enabled) {
            return;
        }
        Counter c = sections.computeIfAbsent(section, k -> new Counter());
        c.count.increment();
        c.nanos.add(nanos);
    }

    /** Time a block of work and record it. */
    public void time(String section, Runnable work) {
        if (!enabled) {
            work.run();
            return;
        }
        long start = System.nanoTime();
        try {
            work.run();
        } finally {
            record(section, System.nanoTime() - start);
        }
    }

    public Sample sample(String section) {
        Counter c = sections.get(section);
        if (c == null) {
            return new Sample(section, 0, 0);
        }
        return new Sample(section, c.count.sum(), c.nanos.sum());
    }

    public Map<String, Sample> snapshot() {
        Map<String, Sample> out = new ConcurrentHashMap<>();
        sections.forEach((name, c) -> out.put(name, new Sample(name, c.count.sum(), c.nanos.sum())));
        return out;
    }

    public void reset() {
        sections.clear();
    }
}
