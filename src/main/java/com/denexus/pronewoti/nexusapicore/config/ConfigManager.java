package com.denexus.pronewoti.nexusapicore.config;

import com.denexus.pronewoti.nexusapicore.diagnostics.NexusLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Loads and persists the Core {@link NexusConfig}. Defaults are populated in
 * code (see {@link #applyCoreDefaults}); an optional {@code nexusapicore.properties}
 * file on disk overrides them. Uses only {@link java.util.Properties} to stay
 * dependency-free and testable.
 */
public final class ConfigManager {

    // ---- core default keys -------------------------------------------------
    public static final String THREAD_WORLD_GENERATION_SIZE = "core.thread.world_generation.size";
    public static final String THREAD_REGION_IO_SIZE = "core.thread.region_io.size";
    public static final String THREAD_COMPRESSION_SIZE = "core.thread.compression.size";
    public static final String THREAD_NETWORK_SIZE = "core.thread.network.size";
    public static final String THREAD_PREDICTION_SIZE = "core.thread.prediction.size";
    public static final String THREAD_GENERAL_SIZE = "core.thread.general.size";
    public static final String MEMORY_BUDGET_BYTES = "core.memory.budget.bytes";
    public static final String DIAGNOSTICS_ENABLED = "core.diagnostics.enabled";
    public static final String METRICS_ENABLED = "core.metrics.enabled";

    private final NexusLogger logger = NexusLogger.forComponent("config");
    private final NexusConfig config = new NexusConfig();

    public NexusConfig config() {
        return config;
    }

    public NexusConfig load(Path file) {
        applyCoreDefaults();
        if (file != null && Files.isRegularFile(file)) {
            Properties props = new Properties();
            try (InputStream in = Files.newInputStream(file)) {
                props.load(in);
                props.forEach((k, v) -> config.set(String.valueOf(k), String.valueOf(v)));
                logger.info("loaded config overrides from " + file.getFileName());
            } catch (IOException e) {
                logger.warn("failed to read config file, using defaults: " + e.getMessage());
            }
        } else {
            logger.info("no config file present, using built-in defaults");
        }
        return config;
    }

    public void save(Path file) {
        Properties props = new Properties();
        config.snapshot().forEach(props::setProperty);
        try (OutputStream out = Files.newOutputStream(file)) {
            props.store(out, "NexusApiCore configuration");
        } catch (IOException e) {
            logger.warn("failed to persist config: " + e.getMessage());
        }
    }

    private void applyCoreDefaults() {
        int cpus = Math.max(1, Runtime.getRuntime().availableProcessors());
        config.setInt(THREAD_WORLD_GENERATION_SIZE, Math.max(1, cpus / 2));
        config.setInt(THREAD_REGION_IO_SIZE, 2);
        config.setInt(THREAD_COMPRESSION_SIZE, 1);
        config.setInt(THREAD_NETWORK_SIZE, 1);
        config.setInt(THREAD_PREDICTION_SIZE, 1);
        config.setInt(THREAD_GENERAL_SIZE, 2);
        config.set(MEMORY_BUDGET_BYTES, Long.toString(Runtime.getRuntime().maxMemory()));
        config.setBoolean(DIAGNOSTICS_ENABLED, true);
        config.setBoolean(METRICS_ENABLED, true);
    }
}
