package com.denexus.pronewoti.nexusapicore.diagnostics;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thin logging facade used across the Core. Backed by {@link java.util.logging}
 * so the infrastructure stays dependency-free and unit-testable without a
 * running Minecraft/Fabric environment. Every message is prefixed with a
 * component tag for readable diagnostics.
 */
public final class NexusLogger {

    private final Logger delegate;
    private final String component;

    private NexusLogger(String component) {
        this.component = component;
        this.delegate = Logger.getLogger("NexusApiCore/" + component);
    }

    public static NexusLogger forComponent(String component) {
        return new NexusLogger(component);
    }

    public void info(String message) {
        delegate.log(Level.INFO, message);
    }

    public void warn(String message) {
        delegate.log(Level.WARNING, message);
    }

    public void error(String message) {
        delegate.log(Level.SEVERE, message);
    }

    public void error(String message, Throwable t) {
        delegate.log(Level.SEVERE, message, t);
    }

    public void debug(String message) {
        delegate.log(Level.FINE, message);
    }

    public String component() {
        return component;
    }
}
