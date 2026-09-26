package com.denexus.pronewoti.nexusapicore.hook;

import com.denexus.pronewoti.nexusapicore.api.NexusContext;

/**
 * Marker for payloads dispatched through a {@link NexusHook}. Extends
 * {@link NexusContext} so every hook payload declares its thread requirement.
 * All Minecraft-domain {@code *Context} types implement this interface.
 */
public interface HookContext extends NexusContext {
}
