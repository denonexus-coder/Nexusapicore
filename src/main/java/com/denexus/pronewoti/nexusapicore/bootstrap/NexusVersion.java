package com.denexus.pronewoti.nexusapicore.bootstrap;

import com.denexus.pronewoti.nexusapicore.compatibility.ApiVersion;
import com.denexus.pronewoti.nexusapicore.compatibility.MinecraftVersion;

/**
 * Compile-time constants describing exactly what this Core targets. These values
 * are VERIFIED against meta.fabricmc.net and the Yarn javadoc for build.6 and
 * must never be fabricated or loosened to other versions.
 */
public final class NexusVersion {

    /** NexusApiCore API contract version (semver). */
    public static final ApiVersion API = new ApiVersion(1, 0, 0);

    /** The ONLY supported Minecraft version. */
    public static final MinecraftVersion MINECRAFT = new MinecraftVersion("1.21.11");

    /** Yarn mappings this build was written against. */
    public static final String YARN_MAPPINGS = "1.21.11+build.6";

    /** Fabric loader baseline (see fabric.mod.json depends). */
    public static final String FABRIC_LOADER = "0.16.9+";

    /** Java language/runtime level. */
    public static final int JAVA = 21;

    private NexusVersion() {
    }

    public static String describe() {
        return "NexusApiCore API " + API + " / Minecraft " + MINECRAFT
                + " / Yarn " + YARN_MAPPINGS + " / Java " + JAVA;
    }
}
