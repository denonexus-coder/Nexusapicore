package com.denexus.pronewoti.nexusapicore.mixin.gateway;

import com.denexus.pronewoti.nexusapicore.diagnostics.NexusLogger;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Catalogue of every {@link NexusMixinDescriptor} the Core knows about, with its
 * mutable {@link NexusMixinState}. This is the single source of truth for the
 * NEXUS_MIXIN_MAP documentation and for runtime introspection of which adapters
 * are verified vs unverified.
 */
public final class NexusMixinRegistry {

    private final NexusLogger logger = NexusLogger.forComponent("mixin");
    private final Map<String, NexusMixinDescriptor> descriptors = new ConcurrentHashMap<>();
    private final Map<String, NexusMixinState> states = new ConcurrentHashMap<>();

    public void register(NexusMixinDescriptor descriptor) {
        descriptors.put(descriptor.id(), descriptor);
        states.put(descriptor.id(), descriptor.initialState());
        if (!descriptor.verified()) {
            logger.warn("UNVERIFIED mixin adapter registered: " + descriptor.id()
                    + " -> " + descriptor.targetClass() + "#" + descriptor.targetMethod()
                    + " (" + descriptor.note() + ")");
        } else {
            logger.info("verified mixin adapter registered: " + descriptor.id());
        }
    }

    public void markActive(String id) {
        states.computeIfPresent(id, (k, v) -> v == NexusMixinState.FAILED ? v : NexusMixinState.ACTIVE);
    }

    public void markFailed(String id) {
        states.put(id, NexusMixinState.FAILED);
    }

    public NexusMixinState state(String id) {
        return states.get(id);
    }

    public NexusMixinDescriptor descriptor(String id) {
        return descriptors.get(id);
    }

    public Collection<NexusMixinDescriptor> all() {
        return descriptors.values();
    }

    public boolean isVerified(String id) {
        NexusMixinDescriptor d = descriptors.get(id);
        return d != null && d.verified();
    }

    public int size() {
        return descriptors.size();
    }
}
