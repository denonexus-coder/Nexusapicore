package com.denexus.pronewoti.nexusapicore.module;

import com.denexus.pronewoti.nexusapicore.capability.CapabilityRegistry;
import com.denexus.pronewoti.nexusapicore.config.ModuleConfig;
import com.denexus.pronewoti.nexusapicore.diagnostics.NexusLogger;
import com.denexus.pronewoti.nexusapicore.hook.HookRegistry;
import com.denexus.pronewoti.nexusapicore.lifecycle.LifecycleManager;
import com.denexus.pronewoti.nexusapicore.service.ServiceRegistry;

/**
 * The single, controlled surface a module is given to talk to the Core. A
 * module registers capabilities/hooks/services and reads its own config through
 * this context - it never reaches into Core internals directly.
 */
public interface NexusModuleContext {

    ModuleInfo moduleInfo();

    NexusLogger logger();

    CapabilityRegistry capabilities();

    HookRegistry hooks();

    ServiceRegistry services();

    LifecycleManager lifecycle();

    ModuleConfig config();
}
