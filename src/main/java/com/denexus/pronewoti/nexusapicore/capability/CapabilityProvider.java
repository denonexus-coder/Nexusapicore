package com.denexus.pronewoti.nexusapicore.capability;

/**
 * Marker super-interface for every capability provider. Concrete provider
 * interfaces (defined later, alongside their modules) extend this. Keeping a
 * shared marker lets the Core define the capability catalogue today without
 * committing to any module-specific method signatures.
 */
public interface CapabilityProvider {
}
