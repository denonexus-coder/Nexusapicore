package com.denexus.pronewoti.nexusapicore.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A flat, thread-safe, typed key/value configuration store. Kept dependency-free
 * (no JSON/TOML library) so the Core config layer is unit-testable without a
 * Minecraft runtime. Keys are dotted, e.g. {@code core.thread.generation.size}.
 */
public class NexusConfig {

    private final Map<String, String> values = new ConcurrentHashMap<>();

    public void set(String key, String value) {
        values.put(key, value);
    }

    public void setInt(String key, int value) {
        values.put(key, Integer.toString(value));
    }

    public void setBoolean(String key, boolean value) {
        values.put(key, Boolean.toString(value));
    }

    public String getString(String key, String def) {
        return values.getOrDefault(key, def);
    }

    public int getInt(String key, int def) {
        String v = values.get(key);
        if (v == null) return def;
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public long getLong(String key, long def) {
        String v = values.get(key);
        if (v == null) return def;
        try {
            return Long.parseLong(v.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public boolean getBoolean(String key, boolean def) {
        String v = values.get(key);
        return v == null ? def : Boolean.parseBoolean(v.trim());
    }

    public boolean contains(String key) {
        return values.containsKey(key);
    }

    public Map<String, String> snapshot() {
        return Map.copyOf(values);
    }
}
