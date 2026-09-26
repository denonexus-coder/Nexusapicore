package com.denexus.pronewoti.nexusapicore.mixin.gateway;

import com.denexus.pronewoti.nexusapicore.hook.HookId;

import java.util.Objects;

/**
 * Declarative description of ONE Mixin adapter: which Yarn target it touches,
 * where it injects, and which {@link HookId} it dispatches to. The
 * {@code verified} flag records whether the target coordinates were confirmed
 * against the Yarn 1.21.11 mappings for this build; unverified targets are kept
 * out of the applied Mixin configs and flagged loudly.
 *
 * <p>Adapters are pure routers: Minecraft -&gt; Mixin -&gt; Hook -&gt;
 * Capability -&gt; module. They contain NO optimization logic.</p>
 *
 * @param id             stable adapter id, e.g. {@code serverworld.save}
 * @param targetClass    Yarn (named) target class, e.g.
 *                       {@code net.minecraft.server.world.ServerWorld}
 * @param intermediary   intermediary name where known, e.g. {@code class_3218}
 * @param targetMethod   Yarn method signature, e.g.
 *                       {@code save(Lnet/minecraft/util/ProgressListener;ZZ)V}
 * @param injectionPoint injection point, e.g. {@code HEAD}
 * @param hookId         the hook this adapter dispatches to
 * @param verified       true only if confirmed against Yarn 1.21.11 here
 * @param note           free-form verification note / caveat
 */
public record NexusMixinDescriptor(
        String id,
        String targetClass,
        String intermediary,
        String targetMethod,
        String injectionPoint,
        HookId hookId,
        boolean verified,
        String note
) {
    public NexusMixinDescriptor {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(targetClass, "targetClass");
        Objects.requireNonNull(targetMethod, "targetMethod");
        Objects.requireNonNull(injectionPoint, "injectionPoint");
        Objects.requireNonNull(hookId, "hookId");
        note = note == null ? "" : note;
    }

    public NexusMixinState initialState() {
        return verified ? NexusMixinState.VERIFIED : NexusMixinState.UNVERIFIED;
    }
}
