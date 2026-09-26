package com.denexus.pronewoti.nexusapicore.config;

/**
 * A namespaced view over the shared {@link NexusConfig} scoped to a single
 * module. Every key is automatically prefixed with {@code module.<id>.} so
 * modules cannot collide with each other or with core keys.
 */
public final class ModuleConfig {

    private final NexusConfig backing;
    private final String prefix;

    public ModuleConfig(NexusConfig backing, String moduleId) {
        this.backing = backing;
        this.prefix = "module." + moduleId + ".";
    }

    private String key(String k) {
        return prefix + k;
    }

    public String getString(String key, String def) {
        return backing.getString(key(key), def);
    }

    public int getInt(String key, int def) {
        return backing.getInt(key(key), def);
    }

    public boolean getBoolean(String key, boolean def) {
        return backing.getBoolean(key(key), def);
    }

    public void set(String key, String value) {
        backing.set(key(key), value);
    }
}
