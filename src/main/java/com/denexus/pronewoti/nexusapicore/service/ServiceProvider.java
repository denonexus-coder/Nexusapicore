package com.denexus.pronewoti.nexusapicore.service;

/**
 * Lazily supplies a {@link NexusService} instance. Lets a module defer service
 * construction until first resolution.
 *
 * @param <T> concrete service type
 */
@FunctionalInterface
public interface ServiceProvider<T extends NexusService> {
    T create();
}
