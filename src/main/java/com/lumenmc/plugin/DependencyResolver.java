package com.lumenmc.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

public class DependencyResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(DependencyResolver.class);
    private static final String prefix = "[LumenServer] ";

    public static List<String> getLoadOrder(Map<String, PluginDescriptionFile> plugins) {
        Map<String, List<String>> graph = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();
        Map<String, Set<String>> softDependencyMap = new HashMap<>();
        Map<String, Set<String>> expandedSoftDependencies = new HashMap<>();

        // Initialize graphs and maps
        for (String pluginName : plugins.keySet()) {
            graph.put(pluginName, new ArrayList<>());
            inDegree.put(pluginName, 0);
            softDependencyMap.put(pluginName, new HashSet<>());
            expandedSoftDependencies.put(pluginName, new HashSet<>());
        }

        // Build graph for hard dependencies and compute in-degrees
        for (PluginDescriptionFile plugin : plugins.values()) {
            String pluginName = plugin.getName();

            for (String dep : plugin.getDepend()) {
                if (!plugins.containsKey(dep)) {
                    LOGGER.error(prefix+"Missing required dependency: {}", dep);
                }
                graph.get(dep).add(pluginName);
                inDegree.put(pluginName, inDegree.get(pluginName) + 1);
            }

            // Store direct soft dependencies
            for (String softDep : plugin.getSoftDepend()) {
                if (plugins.containsKey(softDep)) {
                    softDependencyMap.get(pluginName).add(softDep);
                }
            }
        }

        // Expand transitive soft dependencies efficiently
        for (String plugin : plugins.keySet()) {
            expandSoftDependencies(plugin, softDependencyMap, expandedSoftDependencies);
        }

        // Custom comparator that updates dynamically
        Comparator<String> dependencyComparator = (p1, p2) -> {
            boolean p1DependsOnP2 = expandedSoftDependencies.get(p1).contains(p2);
            boolean p2DependsOnP1 = expandedSoftDependencies.get(p2).contains(p1);

            if (p1DependsOnP2) return 1;  // p2 should be processed first
            if (p2DependsOnP1) return -1; // p1 should be processed first

            // Enforce that plugins with soft dependencies are processed first
            boolean p1HasSoftDep = !expandedSoftDependencies.get(p1).isEmpty();
            boolean p2HasSoftDep = !expandedSoftDependencies.get(p2).isEmpty();

            if (p1HasSoftDep && !p2HasSoftDep) return -1;
            if (!p1HasSoftDep && p2HasSoftDep) return 1;

            return p1.compareTo(p2); // Default alphabetical order
        };

        // Priority queue dynamically maintaining correct order
        PriorityQueue<String> queue = new PriorityQueue<>(dependencyComparator);

        // Enqueue all zero in-degree plugins
        for (String plugin : inDegree.keySet()) {
            if (inDegree.get(plugin) == 0) {
                queue.offer(plugin);
            }
        }

        List<String> loadOrder = new ArrayList<>();

        while (!queue.isEmpty()) {
            List<String> batch = new ArrayList<>();

            // Collect all elements with zero in-degree
            while (!queue.isEmpty()) {
                batch.add(queue.poll());
            }

            // Dynamically re-sort based on current state
            batch.sort(dependencyComparator);

            for (String current : batch) {
                loadOrder.add(current);

                // Process all direct dependents
                for (String neighbor : graph.get(current)) {
                    inDegree.put(neighbor, inDegree.get(neighbor) - 1);

                    if (inDegree.get(neighbor) == 0) {
                        queue.offer(neighbor); // Directly insert into queue to maintain order dynamically
                    }
                }
            }
        }

        // Detect cycles
        if (loadOrder.size() != plugins.size()) {
            LOGGER.error(prefix+"Cycle detected in dependencies, cannot resolve load order.");
        }

        return loadOrder;
    }

    /**
     * Expands soft dependencies recursively to include transitive dependencies.
     */
    private static void expandSoftDependencies(String plugin,
                                               Map<String, Set<String>> softDependencyMap,
                                               Map<String, Set<String>> expandedSoftDependencies) {
        if (!expandedSoftDependencies.get(plugin).isEmpty()) {
            return; // Already processed
        }

        Set<String> expanded = new HashSet<>(softDependencyMap.get(plugin));

        for (String dep : softDependencyMap.get(plugin)) {
            expandSoftDependencies(dep, softDependencyMap, expandedSoftDependencies);
            expanded.addAll(expandedSoftDependencies.get(dep));
        }

        expandedSoftDependencies.put(plugin, expanded);
    }
}
