package com.denexus.pronewoti.nexusapicore.hook;

/**
 * A single listener bound to a hook.
 *
 * @param <T> the hook payload / context type
 */
@FunctionalInterface
public interface HookListener<T extends HookContext> {
    void handle(T context);
}
