package com.denexus.pronewoti.nexusapicore.lifecycle;

/**
 * Callback invoked when the Core enters a {@link LifecyclePhase}. Registered by
 * modules (or Core subsystems) through {@link LifecycleManager}.
 */
@FunctionalInterface
public interface LifecycleListener {
    void onPhase(LifecyclePhase phase);
}
