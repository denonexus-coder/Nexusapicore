package com.denexus.pronewoti.nexusapicore.dependency;

import com.denexus.pronewoti.nexusapicore.api.exception.NexusModuleException;
import com.denexus.pronewoti.nexusapicore.diagnostics.NexusLogger;
import com.denexus.pronewoti.nexusapicore.module.ModuleDependency;
import com.denexus.pronewoti.nexusapicore.module.ModuleInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Builds a {@link DependencyGraph} from discovered {@link ModuleInfo}s and
 * produces a load order via topological sort (DFS with cycle detection).
 *
 * <p>Missing required dependencies fail the dependent module (isolated by the
 * caller); missing optional dependencies are simply dropped from ordering.
 * A dependency cycle raises {@link NexusModuleException} naming the cycle.</p>
 */
public final class DependencyResolver {

    private final NexusLogger logger = NexusLogger.forComponent("dependency");

    /**
     * @return module ids in a valid load order (dependencies first)
     */
    public List<String> resolveOrder(List<ModuleInfo> modules) {
        Map<String, ModuleInfo> byId = new HashMap<>();
        for (ModuleInfo m : modules) {
            byId.put(m.id(), m);
        }

        DependencyGraph graph = new DependencyGraph();
        for (ModuleInfo m : modules) {
            graph.addNode(m.id());
            for (ModuleDependency dep : m.dependencies()) {
                if (byId.containsKey(dep.id())) {
                    graph.addEdge(m.id(), dep.id());
                } else if (dep.optional()) {
                    logger.debug("optional dependency absent, ignoring: " + m.id() + " -> " + dep.id());
                } else {
                    throw new NexusModuleException(m.id(),
                            "missing required dependency: " + dep.id());
                }
            }
        }
        return topologicalSort(graph);
    }

    private List<String> topologicalSort(DependencyGraph graph) {
        List<String> ordered = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> onStack = new LinkedHashSet<>();
        for (String node : graph.nodeList()) {
            visit(node, graph, visited, onStack, ordered);
        }
        return ordered;
    }

    private void visit(String node, DependencyGraph graph, Set<String> visited,
                       Set<String> onStack, List<String> ordered) {
        if (visited.contains(node)) {
            return;
        }
        if (onStack.contains(node)) {
            throw new NexusModuleException(node,
                    "dependency cycle detected: " + String.join(" -> ", onStack) + " -> " + node);
        }
        onStack.add(node);
        for (String dep : graph.dependenciesOf(node)) {
            visit(dep, graph, visited, onStack, ordered);
        }
        onStack.remove(node);
        visited.add(node);
        ordered.add(node); // dependency added before dependent
    }
}
