package com.denexus.pronewoti.nexusapicore.module;

import java.util.List;
import java.util.Objects;

/**
 * Immutable, parsed representation of a {@code nexus.module.json} manifest.
 * Produced by the loader; consumed by validation, dependency resolution and
 * registration. Carries only declarative metadata - never module logic.
 */
public record ModuleInfo(
        String id,
        String name,
        String version,
        String apiVersion,
        String minecraft,
        String entrypoint,
        List<ModuleDependency> dependencies,
        List<String> capabilities,
        List<String> hooks,
        List<String> services
) {
    public ModuleInfo {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(version, "version");
        Objects.requireNonNull(apiVersion, "apiVersion");
        Objects.requireNonNull(minecraft, "minecraft");
        Objects.requireNonNull(entrypoint, "entrypoint");
        dependencies = dependencies == null ? List.of() : List.copyOf(dependencies);
        capabilities = capabilities == null ? List.of() : List.copyOf(capabilities);
        hooks = hooks == null ? List.of() : List.copyOf(hooks);
        services = services == null ? List.of() : List.copyOf(services);
    }
}
