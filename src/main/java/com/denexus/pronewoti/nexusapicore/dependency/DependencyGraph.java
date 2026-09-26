package com.denexus.pronewoti.nexusapicore.dependency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Directed dependency graph of module ids. An edge {@code a -> b} means
 * "a depends on b", so b must be ordered before a. Pure data structure; ordering
 * and cycle detection live in {@link DependencyResolver}.
 */
public final class DependencyGraph {

    private final Map<String, Set<String>> edges = new HashMap<>();

    public void addNode(String id) {
        edges.computeIfAbsent(id, k -> new LinkedHashSet<>());
    }

    /** Declare that {@code dependent} depends on {@code dependency}. */
    public void addEdge(String dependent, String dependency) {
        addNode(dependent);
        addNode(dependency);
        edges.get(dependent).add(dependency);
    }

    public Set<String> dependenciesOf(String id) {
        return edges.getOrDefault(id, Set.of());
    }

    public Set<String> nodes() {
        return edges.keySet();
    }

    public boolean contains(String id) {
        return edges.containsKey(id);
    }

    public List<String> nodeList() {
        return new ArrayList<>(edges.keySet());
    }
}
