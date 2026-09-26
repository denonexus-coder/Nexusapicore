package com.denexus.pronewoti.nexusapicore.api.exception;

/** Raised when a module fails to load, validate, initialize or run. */
public class NexusModuleException extends NexusException {
    private final String moduleId;

    public NexusModuleException(String moduleId, String message) {
        super("[module:" + moduleId + "] " + message);
        this.moduleId = moduleId;
    }

    public NexusModuleException(String moduleId, String message, Throwable cause) {
        super("[module:" + moduleId + "] " + message, cause);
        this.moduleId = moduleId;
    }

    public String moduleId() {
        return moduleId;
    }
}
