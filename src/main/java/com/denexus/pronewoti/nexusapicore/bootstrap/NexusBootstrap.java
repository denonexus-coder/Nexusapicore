package com.denexus.pronewoti.nexusapicore.bootstrap;

import com.denexus.pronewoti.nexusapicore.diagnostics.NexusLogger;
import com.denexus.pronewoti.nexusapicore.hook.HookId;
import com.denexus.pronewoti.nexusapicore.mixin.gateway.NexusMixinDescriptor;

import java.nio.file.Path;

/**
 * Shared startup routine invoked by both Fabric entrypoints. Creates the Core
 * singleton for the detected {@link NexusEnvironment}, registers the KNOWN Mixin
 * adapter descriptors (marking verification status honestly) and initializes
 * the Core. Kept free of Fabric types so it is unit-testable.
 */
public final class NexusBootstrap {

    private static final NexusLogger LOGGER = NexusLogger.forComponent("bootstrap");

    /** Standard hook ids dispatched by the shipped Mixin adapters. */
    public static final HookId HOOK_WORLD_SAVE = HookId.of("server.world.save");

    private NexusBootstrap() {
    }

    public static synchronized NexusApiCore start(NexusEnvironment environment, Path configFile) {
        NexusApiCore core = NexusApiCore.create(environment);
        if (core.isInitialized()) {
            return core;
        }
        registerMixinDescriptors(core);
        core.initialize(configFile);
        LOGGER.info("bootstrap complete for " + environment);
        return core;
    }

    private static void registerMixinDescriptors(NexusApiCore core) {
        // VERIFIED against Yarn 1.21.11+build.6: ServerWorld#save(ProgressListener,boolean,boolean)V
        // (intermediary class_3218) is unambiguous.
        core.mixins().register(new NexusMixinDescriptor(
                "serverworld.save",
                "net.minecraft.server.world.ServerWorld",
                "class_3218",
                "save(Lnet/minecraft/util/ProgressListener;ZZ)V",
                "HEAD",
                HOOK_WORLD_SAVE,
                true,
                "VERIFIED via Yarn javadoc build.6; adapter only dispatches the save hook, no logic"));
    }
}
