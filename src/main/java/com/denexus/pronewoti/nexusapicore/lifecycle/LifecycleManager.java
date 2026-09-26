package com.denexus.pronewoti.nexusapicore.lifecycle;

import com.denexus.pronewoti.nexusapicore.diagnostics.NexusLogger;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Owns the current {@link LifecyclePhase} and fans out phase transitions to
 * registered {@link LifecycleListener}s. A misbehaving listener is isolated:
 * its exception is logged and the remaining listeners still run.
 */
public final class LifecycleManager {

    private final NexusLogger logger = NexusLogger.forComponent("lifecycle");
    private final List<LifecycleListener> listeners = new CopyOnWriteArrayList<>();
    private final EnumSet<LifecyclePhase> reached = EnumSet.noneOf(LifecyclePhase.class);
    private volatile LifecyclePhase current = LifecyclePhase.BOOTSTRAP;

    public void addListener(LifecycleListener listener) {
        listeners.add(listener);
    }

    public void removeListener(LifecycleListener listener) {
        listeners.remove(listener);
    }

    public LifecyclePhase current() {
        return current;
    }

    public boolean hasReached(LifecyclePhase phase) {
        return reached.contains(phase);
    }

    /** Advances to the given phase and notifies all listeners (error isolated). */
    public synchronized void enter(LifecyclePhase phase) {
        this.current = phase;
        this.reached.add(phase);
        logger.info("phase -> " + phase);
        for (LifecycleListener listener : listeners) {
            try {
                listener.onPhase(phase);
            } catch (Throwable t) {
                logger.error("lifecycle listener failed during phase " + phase, t);
            }
        }
    }
}
