package com.denexus.pronewoti.nexusapicore.api.exception;

/** Raised on invalid capability registration / resolution. */
public class NexusCapabilityException extends NexusException {
    public NexusCapabilityException(String message) {
        super(message);
    }

    public NexusCapabilityException(String message, Throwable cause) {
        super(message, cause);
    }
}
