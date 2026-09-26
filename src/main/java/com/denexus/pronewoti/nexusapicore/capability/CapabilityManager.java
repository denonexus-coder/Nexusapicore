package com.denexus.pronewoti.nexusapicore.capability;

import com.denexus.pronewoti.nexusapicore.diagnostics.NexusLogger;

/**
 * Owns the {@link CapabilityRegistry} and exposes it to the rest of the Core.
 * The base capability catalogue ({@link Capabilities}) needs no runtime
 * pre-registration - the slots exist as constants and are bound lazily by
 * modules, so this manager stays intentionally thin.
 */
public final class CapabilityManager {

    private final NexusLogger logger = NexusLogger.forComponent("capability");
    private final CapabilityRegistry registry = new CapabilityRegistry();

    public CapabilityRegistry registry() {
        return registry;
    }

    public void logSummary() {
        logger.info("bound capabilities: " + registry.size());
    }
}
