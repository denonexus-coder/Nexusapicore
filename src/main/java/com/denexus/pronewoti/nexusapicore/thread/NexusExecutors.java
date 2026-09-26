package com.denexus.pronewoti.nexusapicore.thread;

import com.denexus.pronewoti.nexusapicore.config.ConfigManager;

/**
 * The fixed set of logical worker pools offered by the Core. Kept intentionally
 * small - modules share these pools rather than spawning their own threads.
 * Each entry knows the config key that controls its size.
 */
public enum NexusExecutors {
    WORLD_GENERATION(ConfigManager.THREAD_WORLD_GENERATION_SIZE),
    REGION_IO(ConfigManager.THREAD_REGION_IO_SIZE),
    COMPRESSION(ConfigManager.THREAD_COMPRESSION_SIZE),
    NETWORK(ConfigManager.THREAD_NETWORK_SIZE),
    PREDICTION(ConfigManager.THREAD_PREDICTION_SIZE),
    GENERAL(ConfigManager.THREAD_GENERAL_SIZE);

    private final String sizeConfigKey;

    NexusExecutors(String sizeConfigKey) {
        this.sizeConfigKey = sizeConfigKey;
    }

    public String sizeConfigKey() {
        return sizeConfigKey;
    }
}
