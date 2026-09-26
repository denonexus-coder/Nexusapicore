package com.denexus.pronewoti.nexusapicore.service;

import com.denexus.pronewoti.nexusapicore.diagnostics.NexusLogger;

/**
 * Owns the {@link ServiceRegistry} and coordinates orderly shutdown of all
 * registered services when the Core stops.
 */
public final class ServiceManager {

    private final NexusLogger logger = NexusLogger.forComponent("service");
    private final ServiceRegistry registry = new ServiceRegistry();

    public ServiceRegistry registry() {
        return registry;
    }

    public void logSummary() {
        logger.info("registered services: " + registry.size());
    }
}
