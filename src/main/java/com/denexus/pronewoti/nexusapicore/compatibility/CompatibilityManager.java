package com.denexus.pronewoti.nexusapicore.compatibility;

import com.denexus.pronewoti.nexusapicore.module.ModuleInfo;

/**
 * Decides whether a module is compatible with this Core. The rules are strict
 * and explicit - the project targets ONLY Minecraft 1.21.11 and never fabricates
 * compatibility with other versions.
 */
public final class CompatibilityManager {

    private final ApiVersion coreApiVersion;
    private final MinecraftVersion coreMinecraftVersion;

    public CompatibilityManager(ApiVersion coreApiVersion, MinecraftVersion coreMinecraftVersion) {
        this.coreApiVersion = coreApiVersion;
        this.coreMinecraftVersion = coreMinecraftVersion;
    }

    public CompatibilityResult check(ModuleInfo info) {
        // 1. Minecraft version must match EXACTLY.
        MinecraftVersion moduleMc = new MinecraftVersion(info.minecraft());
        if (!coreMinecraftVersion.matches(moduleMc)) {
            return CompatibilityResult.fail(
                    "module targets Minecraft " + moduleMc + " but Core targets " + coreMinecraftVersion);
        }

        // 2. API version must be semver-compatible with the Core contract.
        final ApiVersion moduleApi;
        try {
            moduleApi = ApiVersion.parse(info.apiVersion());
        } catch (IllegalArgumentException e) {
            return CompatibilityResult.fail("invalid apiVersion '" + info.apiVersion() + "': " + e.getMessage());
        }
        if (!coreApiVersion.isCompatibleWith(moduleApi)) {
            return CompatibilityResult.fail(
                    "module requires API " + moduleApi + " incompatible with Core API " + coreApiVersion);
        }

        return CompatibilityResult.ok();
    }

    public ApiVersion coreApiVersion() {
        return coreApiVersion;
    }

    public MinecraftVersion coreMinecraftVersion() {
        return coreMinecraftVersion;
    }
}
