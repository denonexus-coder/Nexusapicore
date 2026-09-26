package com.denexus.pronewoti.nexusapicore.bootstrap;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

/**
 * Fabric {@code main} entrypoint (common/server side). Delegates to
 * {@link NexusBootstrap}; contains no logic beyond wiring the Fabric config
 * directory into the Core.
 */
public final class NexusFabricEntrypoint implements ModInitializer {

    @Override
    public void onInitialize() {
        Path configFile = FabricLoader.getInstance()
                .getConfigDir()
                .resolve("nexusapicore.properties");
        NexusBootstrap.start(NexusEnvironment.SERVER, configFile);
    }
}
