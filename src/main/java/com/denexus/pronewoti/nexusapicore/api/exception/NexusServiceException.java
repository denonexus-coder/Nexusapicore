package com.denexus.pronewoti.nexusapicore.api.exception;

/** Raised on invalid service registration / resolution. */
public class NexusServiceException extends NexusException {
    public NexusServiceException(String message) {
        super(message);
    }

    public NexusServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
