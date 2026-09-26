package com.denexus.pronewoti.nexusapicore.loader;

import com.denexus.pronewoti.nexusapicore.api.exception.NexusModuleException;
import com.denexus.pronewoti.nexusapicore.diagnostics.NexusLogger;
import com.denexus.pronewoti.nexusapicore.module.ModuleInfo;
import com.denexus.pronewoti.nexusapicore.module.NexusModule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Discovers module manifests and instantiates their entrypoint classes.
 *
 * <p>Discovery scans the classpath for a well-known resource name
 * ({@value #MANIFEST_RESOURCE}); each optimization module (a separate jar/mod)
 * ships exactly one such manifest. Instantiation is reflective and requires a
 * public no-args constructor on the declared entrypoint. Any failure is wrapped
 * in {@link NexusModuleException} and isolated by the caller.</p>
 */
public final class ModuleLoader {

    public static final String MANIFEST_RESOURCE = "nexus.module.json";

    private final NexusLogger logger = NexusLogger.forComponent("loader");
    private final ManifestParser parser = new ManifestParser();

    /** Scan the given class loader for module manifests. */
    public List<ModuleInfo> discover(ClassLoader classLoader) {
        List<ModuleInfo> found = new ArrayList<>();
        try {
            Enumeration<URL> resources = classLoader.getResources(MANIFEST_RESOURCE);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                try (InputStream in = url.openStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                    ModuleInfo info = parser.parse(reader);
                    found.add(info);
                    logger.info("discovered module manifest: " + info.id() + " @ " + url);
                } catch (RuntimeException | IOException e) {
                    logger.warn("skipping unreadable manifest at " + url + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error("module discovery failed", e);
        }
        return found;
    }

    /** Reflectively instantiate the module entrypoint declared in the manifest. */
    public NexusModule instantiate(ModuleInfo info, ClassLoader classLoader) {
        try {
            Class<?> clazz = Class.forName(info.entrypoint(), true, classLoader);
            if (!NexusModule.class.isAssignableFrom(clazz)) {
                throw new NexusModuleException(info.id(),
                        "entrypoint " + info.entrypoint() + " does not implement NexusModule");
            }
            Object instance = clazz.getDeclaredConstructor().newInstance();
            return (NexusModule) instance;
        } catch (NexusModuleException e) {
            throw e;
        } catch (ReflectiveOperationException e) {
            throw new NexusModuleException(info.id(),
                    "failed to instantiate entrypoint " + info.entrypoint(), e);
        }
    }
}
