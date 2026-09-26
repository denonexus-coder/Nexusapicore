package com.denexus.pronewoti.nexusapicore.service;

/**
 * A long-lived service published into the Core's {@link ServiceRegistry}.
 * Services expose behaviour (unlike capabilities, which are typed slots) and
 * have their own start/stop lifecycle. The Core ships no concrete services.
 */
public interface NexusService {

    /** Unique service id, e.g. {@code region.io}. */
    String id();

    /** Semantic version of this service implementation. */
    default String version() {
        return "1.0.0";
    }

    /** Called when the service is registered/activated. */
    default void start() {
    }

    /** Called when the service is removed or the Core shuts down. */
    default void stop() {
    }
}
