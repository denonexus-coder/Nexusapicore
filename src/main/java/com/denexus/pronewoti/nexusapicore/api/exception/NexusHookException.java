package com.denexus.pronewoti.nexusapicore.api.exception;

/** Raised when a hook listener throws while being dispatched. */
public class NexusHookException extends NexusException {
    public NexusHookException(String message) {
        super(message);
    }

    public NexusHookException(String message, Throwable cause) {
        super(message, cause);
    }
}
