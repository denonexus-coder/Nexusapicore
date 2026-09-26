package com.denexus.pronewoti.nexusapicore.capability;

/**
 * The catalogue of base capability slots defined by NexusApiCore. These are
 * ONLY contracts (names); the Core provides no implementation for any of them.
 * Future modules bind providers to these ids.
 *
 * <p>The generic type of each slot is intentionally the marker interface
 * {@link CapabilityProvider} for now: the concrete provider interfaces will be
 * introduced together with the modules that own them, without changing these
 * ids.</p>
 */
public final class Capabilities {

    private Capabilities() {
    }

    private static Capability<CapabilityProvider> slot(String id) {
        return Capability.of(id, CapabilityProvider.class);
    }

    // generation.*
    public static final Capability<CapabilityProvider> GENERATION_CHUNK = slot("generation.chunk");
    public static final Capability<CapabilityProvider> GENERATION_REGION = slot("generation.region");
    public static final Capability<CapabilityProvider> GENERATION_NOISE = slot("generation.noise");

    // chunk.*
    public static final Capability<CapabilityProvider> CHUNK_LOAD = slot("chunk.load");
    public static final Capability<CapabilityProvider> CHUNK_UNLOAD = slot("chunk.unload");
    public static final Capability<CapabilityProvider> CHUNK_GENERATE = slot("chunk.generate");
    public static final Capability<CapabilityProvider> CHUNK_SAVE = slot("chunk.save");

    // world.*
    public static final Capability<CapabilityProvider> WORLD_LOAD = slot("world.load");
    public static final Capability<CapabilityProvider> WORLD_UNLOAD = slot("world.unload");
    public static final Capability<CapabilityProvider> WORLD_TICK = slot("world.tick");

    // storage.*
    public static final Capability<CapabilityProvider> STORAGE_CHUNK = slot("storage.chunk");
    public static final Capability<CapabilityProvider> STORAGE_REGION = slot("storage.region");
    public static final Capability<CapabilityProvider> STORAGE_CONTINENT = slot("storage.continent");

    // network.*
    public static final Capability<CapabilityProvider> NETWORK_CHUNK = slot("network.chunk");
    public static final Capability<CapabilityProvider> NETWORK_REGION = slot("network.region");

    // player.*
    public static final Capability<CapabilityProvider> PLAYER_POSITION = slot("player.position");
    public static final Capability<CapabilityProvider> PLAYER_MOVEMENT = slot("player.movement");
    public static final Capability<CapabilityProvider> PLAYER_VIEW_DISTANCE = slot("player.view_distance");

    // render.*
    public static final Capability<CapabilityProvider> RENDER_CHUNK = slot("render.chunk");
    public static final Capability<CapabilityProvider> RENDER_WORLD = slot("render.world");

    // prediction.*
    public static final Capability<CapabilityProvider> PREDICTION_REGION = slot("prediction.region");

    // memory.*
    public static final Capability<CapabilityProvider> MEMORY_CACHE = slot("memory.cache");
    public static final Capability<CapabilityProvider> MEMORY_BUDGET = slot("memory.budget");

    // thread.*
    public static final Capability<CapabilityProvider> THREAD_GENERATION = slot("thread.generation");
    public static final Capability<CapabilityProvider> THREAD_IO = slot("thread.io");
}
