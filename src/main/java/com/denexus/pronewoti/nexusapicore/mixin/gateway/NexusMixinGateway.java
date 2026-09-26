package com.denexus.pronewoti.nexusapicore.mixin.gateway;

import com.denexus.pronewoti.nexusapicore.diagnostics.NexusLogger;
import com.denexus.pronewoti.nexusapicore.hook.HookContext;
import com.denexus.pronewoti.nexusapicore.hook.HookId;
import com.denexus.pronewoti.nexusapicore.hook.HookRegistry;
import com.denexus.pronewoti.nexusapicore.hook.NexusHook;

/**
 * The ONLY bridge between generated Mixin classes and the rest of the Core.
 * Mixin adapters must not touch Core internals directly; they call
 * {@link #dispatch(String, HookId, HookContext)} to forward a context to the
 * matching {@link NexusHook}. This keeps the dependency direction strict:
 *
 * <pre>Minecraft -&gt; Mixin -&gt; Gateway -&gt; Hook -&gt; Capability -&gt; module</pre>
 *
 * <p>The gateway is a process-wide singleton accessed statically because Mixin
 * classes are instantiated by the Mixin subsystem and cannot be dependency-
 * injected. It is null-safe before {@link #bind} is called (mixins that fire
 * early simply no-op), and every dispatch is error-isolated.</p>
 */
public final class NexusMixinGateway {

    private static final NexusLogger LOGGER = NexusLogger.forComponent("mixin-gateway");
    private static volatile NexusMixinGateway INSTANCE;

    private final HookRegistry hooks;
    private final NexusMixinRegistry mixins;

    private NexusMixinGateway(HookRegistry hooks, NexusMixinRegistry mixins) {
        this.hooks = hooks;
        this.mixins = mixins;
    }

    /** Install the live gateway. Called once by the bootstrap after Core init. */
    public static synchronized void bind(HookRegistry hooks, NexusMixinRegistry mixins) {
        INSTANCE = new NexusMixinGateway(hooks, mixins);
        LOGGER.info("mixin gateway bound");
    }

    /** Tear down the gateway on shutdown so late-firing mixins no-op safely. */
    public static synchronized void unbind() {
        INSTANCE = null;
    }

    public static boolean isBound() {
        return INSTANCE != null;
    }

    /**
     * Route a Mixin-produced context to its hook. Safe to call before the Core
     * is ready (no-op) and never throws into the caller (error isolated).
     *
     * @param mixinId  the descriptor id of the calling adapter (for state/log)
     * @param hookId   the target hook
     * @param context  the payload built by the adapter from Yarn-typed args
     */
    public static <T extends HookContext> void dispatch(String mixinId, HookId hookId, T context) {
        NexusMixinGateway gw = INSTANCE;
        if (gw == null) {
            return; // Core not ready yet - adapters must tolerate this.
        }
        try {
            gw.mixins.markActive(mixinId);
            NexusHook<T> hook = gw.hooks.getOrCreate(hookId);
            hook.dispatch(context);
        } catch (Throwable t) {
            gw.mixins.markFailed(mixinId);
            LOGGER.error("mixin adapter '" + mixinId + "' dispatch to hook '" + hookId + "' failed (isolated)", t);
        }
    }
}
