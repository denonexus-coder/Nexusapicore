package com.denexus.pronewoti.nexusapicore.metrics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.LongSupplier;

/**
 * Minimal metrics facade: monotonic counters and pull-based gauges. Kept simple
 * and dependency-free; modules register gauges and bump counters, and a
 * diagnostics reporter can pull a snapshot on demand. No optimization logic.
 */
public final class NexusMetrics {

    private final Map<String, LongAdder> counters = new ConcurrentHashMap<>();
    private final Map<String, LongSupplier> gauges = new ConcurrentHashMap<>();
    private volatile boolean enabled = true;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void increment(String name) {
        increment(name, 1L);
    }

    public void increment(String name, long delta) {
        if (!enabled) {
            return;
        }
        counters.computeIfAbsent(name, k -> new LongAdder()).add(delta);
    }

    public long counter(String name) {
        LongAdder a = counters.get(name);
        return a == null ? 0L : a.sum();
    }

    public void registerGauge(String name, LongSupplier supplier) {
        gauges.put(name, supplier);
    }

    public void unregisterGauge(String name) {
        gauges.remove(name);
    }

    public long gauge(String name) {
        LongSupplier s = gauges.get(name);
        return s == null ? 0L : s.getAsLong();
    }

    public Map<String, Long> snapshotCounters() {
        Map<String, Long> out = new ConcurrentHashMap<>();
        counters.forEach((k, v) -> out.put(k, v.sum()));
        return out;
    }

    public Map<String, Long> snapshotGauges() {
        Map<String, Long> out = new ConcurrentHashMap<>();
        gauges.forEach((k, v) -> out.put(k, v.getAsLong()));
        return out;
    }

    public void reset() {
        counters.clear();
    }
}
