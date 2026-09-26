package com.denexus.pronewoti.nexusapicore.client;

import com.denexus.pronewoti.nexusapicore.bootstrap.NexusApiCore;
import com.denexus.pronewoti.nexusapicore.bootstrap.NexusEnvironment;
import com.denexus.pronewoti.nexusapicore.diagnostics.NexusLogger;
import net.fabricmc.api.ClientModInitializer;

/**
 * Fabric {@code client} entrypoint. On a physical client the common entrypoint
 * has already started the Core; this only records the client side for
 * diagnostics. It never starts a second Core instance.
 */
public final class NexusFabricClientEntrypoint implements ClientModInitializer {

    private static final NexusLogger LOGGER = NexusLogger.forComponent("client");

    @Override
    public void onInitializeClient() {
        if (NexusApiCore.isPresent()) {
            LOGGER.info("client entrypoint: Core already present ("
                    + NexusApiCore.get().environment() + ")");
        } else {
            // Defensive: should not happen, common entrypoint runs first.
            LOGGER.warn("client entrypoint fired before common Core init; environment=" + NexusEnvironment.CLIENT);
        }
    }
}
