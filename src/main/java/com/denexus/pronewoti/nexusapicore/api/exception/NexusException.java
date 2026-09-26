package com.denexus.pronewoti.nexusapicore.api.exception;

/**
 * Root of the NexusApiCore exception hierarchy. Thrown by core subsystems and
 * caught by the error-isolation layer so that a single failing module can never
 * bring down the whole Core (and therefore never crash Minecraft).
 */
public class NexusException extends RuntimeException {
    public NexusException(String message) {
        super(message);
    }

    public NexusException(String message, Throwable cause) {
        super(message, cause);
    }
}
