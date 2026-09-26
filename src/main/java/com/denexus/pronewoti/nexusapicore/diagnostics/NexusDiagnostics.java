package com.denexus.pronewoti.nexusapicore.diagnostics;

import com.denexus.pronewoti.nexusapicore.metrics.NexusMetrics;

/**
 * Central diagnostics hub. Bundles the {@link NexusProfiler} and
 * {@link NexusMetrics} and can render a human-readable report for logging or a
 * debug command. Purely observational.
 */
public final class NexusDiagnostics {

    private final NexusLogger logger = NexusLogger.forComponent("diagnostics");
    private final NexusProfiler profiler = new NexusProfiler();
    private final NexusMetrics metrics = new NexusMetrics();
    private volatile boolean enabled = true;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        profiler.setEnabled(enabled);
        metrics.setEnabled(enabled);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public NexusProfiler profiler() {
        return profiler;
    }

    public NexusMetrics metrics() {
        return metrics;
    }

    /** Build a plain-text snapshot of counters, gauges and profiled sections. */
    public String renderReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== NexusApiCore Diagnostics ===\n");
        sb.append("[counters]\n");
        metrics.snapshotCounters().forEach((k, v) -> sb.append("  ").append(k).append(" = ").append(v).append('\n'));
        sb.append("[gauges]\n");
        metrics.snapshotGauges().forEach((k, v) -> sb.append("  ").append(k).append(" = ").append(v).append('\n'));
        sb.append("[profiler] (avg ns / calls)\n");
        profiler.snapshot().forEach((k, s) ->
                sb.append("  ").append(k).append(" = ")
                        .append(String.format("%.1f", s.averageNanos()))
                        .append(" / ").append(s.count()).append('\n'));
        return sb.toString();
    }

    public void logReport() {
        logger.info("\n" + renderReport());
    }
}
