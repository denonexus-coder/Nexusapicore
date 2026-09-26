package com.denexus.pronewoti.nexusapicore.api;

/**
 * Base contract for every context object dispatched through a hook.
 *
 * <p>A context is an immutable (or defensively read-only) carrier of the data
 * relevant to a single interception point. Contexts NEVER carry optimization
 * logic - they only expose data plus the thread on which they are valid.</p>
 */
public interface NexusContext {

    /**
     * @return the thread on which the fields of this context may be read safely.
     */
    ThreadRequirement threadRequirement();
}
