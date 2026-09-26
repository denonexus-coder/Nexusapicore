package com.denexus.pronewoti.nexusapicore.loader;

import com.denexus.pronewoti.nexusapicore.api.exception.NexusModuleException;
import com.denexus.pronewoti.nexusapicore.module.ModuleDependency;
import com.denexus.pronewoti.nexusapicore.module.ModuleInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses a {@code nexus.module.json} manifest (schema: nexus.module.schema.json)
 * into an immutable {@link ModuleInfo}. Uses Gson, which is provided transitively
 * by the Fabric loader at runtime. Performs shape validation only - semantic
 * validation (version/dep compatibility) lives in the module + dependency layers.
 */
public final class ManifestParser {

    public ModuleInfo parse(Reader reader) {
        JsonObject root;
        try {
            JsonElement parsed = JsonParser.parseReader(reader);
            if (!parsed.isJsonObject()) {
                throw new NexusModuleException("<unknown>", "manifest root must be a JSON object");
            }
            root = parsed.getAsJsonObject();
        } catch (JsonSyntaxException e) {
            throw new NexusModuleException("<unknown>", "malformed manifest JSON", e);
        }

        String id = requireString(root, "id");
        String name = optionalString(root, "name", id);
        String version = requireString(root, "version");
        String apiVersion = requireString(root, "apiVersion");
        String minecraft = requireString(root, "minecraft");
        String entrypoint = requireString(root, "entrypoint");

        List<ModuleDependency> deps = parseDependencies(root, id);
        List<String> capabilities = parseStringArray(root, "capabilities");
        List<String> hooks = parseStringArray(root, "hooks");
        List<String> services = parseStringArray(root, "services");

        return new ModuleInfo(id, name, version, apiVersion, minecraft, entrypoint,
                deps, capabilities, hooks, services);
    }

    private List<ModuleDependency> parseDependencies(JsonObject root, String moduleId) {
        List<ModuleDependency> deps = new ArrayList<>();
        if (!root.has("dependencies") || root.get("dependencies").isJsonNull()) {
            return deps;
        }
        if (!root.get("dependencies").isJsonArray()) {
            throw new NexusModuleException(moduleId, "'dependencies' must be an array");
        }
        for (JsonElement el : root.getAsJsonArray("dependencies")) {
            if (el.isJsonPrimitive()) {
                deps.add(ModuleDependency.required(el.getAsString()));
            } else if (el.isJsonObject()) {
                JsonObject dep = el.getAsJsonObject();
                String depId = requireString(dep, "id");
                boolean optional = dep.has("optional") && dep.get("optional").getAsBoolean();
                deps.add(new ModuleDependency(depId, optional));
            } else {
                throw new NexusModuleException(moduleId, "invalid dependency entry");
            }
        }
        return deps;
    }

    private List<String> parseStringArray(JsonObject root, String key) {
        List<String> out = new ArrayList<>();
        if (root.has(key) && root.get(key).isJsonArray()) {
            JsonArray arr = root.getAsJsonArray(key);
            for (JsonElement el : arr) {
                if (el.isJsonPrimitive()) {
                    out.add(el.getAsString());
                }
            }
        }
        return out;
    }

    private String requireString(JsonObject obj, String key) {
        if (!obj.has(key) || obj.get(key).isJsonNull() || !obj.get(key).isJsonPrimitive()) {
            throw new NexusModuleException("<unknown>", "missing required manifest field: " + key);
        }
        return obj.get(key).getAsString();
    }

    private String optionalString(JsonObject obj, String key, String def) {
        if (obj.has(key) && obj.get(key).isJsonPrimitive()) {
            return obj.get(key).getAsString();
        }
        return def;
    }
}
