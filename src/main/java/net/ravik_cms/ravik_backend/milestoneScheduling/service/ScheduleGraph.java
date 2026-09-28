package net.ravik_cms.ravik_backend.milestoneScheduling.service;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.exception.CircularDependencyException;
import net.ravik_cms.ravik_backend.milestoneScheduling.entity.MilestoneDependency;
import net.ravik_cms.ravik_backend.milestoneScheduling.repository.ScheduleRepository;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Shared graph construction + topological ordering.
 *
 * Extracted out of ScheduleService so that both the BASELINE pass
 * (ScheduleService.calculateSchedule) and the FORECAST pass
 * (ForecastService.recalculateForecast) run over the exact same network.
 * If these two ever drift apart, variance numbers become meaningless.
 */
@Component
@RequiredArgsConstructor
public class ScheduleGraph {

    private final ScheduleRepository scheduleRepository;

    /**
     * Adjacency + indegree for one project's milestone network.
     */
    public static class GraphData {
        public final Map<UUID, List<Milestones>> successorMap;
        public final Map<UUID, List<Milestones>> predecessorMap;
        public final Map<UUID, Integer> indegree;
        /** lag in working days, keyed by "predecessorId->milestoneId" */
        public final Map<String, Integer> lagMap;

        public GraphData(Map<UUID, List<Milestones>> successorMap,
                         Map<UUID, List<Milestones>> predecessorMap,
                         Map<UUID, Integer> indegree,
                         Map<String, Integer> lagMap) {
            this.successorMap = successorMap;
            this.predecessorMap = predecessorMap;
            this.indegree = indegree;
            this.lagMap = lagMap;
        }

        public List<Milestones> successorsOf(UUID id) {
            return successorMap.getOrDefault(id, Collections.emptyList());
        }

        public List<Milestones> predecessorsOf(UUID id) {
            return predecessorMap.getOrDefault(id, Collections.emptyList());
        }

        public boolean isTerminal(UUID id) {
            return successorsOf(id).isEmpty();
        }

        public boolean isStartNode(UUID id) {
            return predecessorsOf(id).isEmpty();
        }

        public int lagBetween(UUID predecessorId, UUID milestoneId) {
            return lagMap.getOrDefault(key(predecessorId, milestoneId), ScheduleGraph.DEFAULT_LAG_DAYS);
        }

        static String key(UUID predecessorId, UUID milestoneId) {
            return predecessorId + "->" + milestoneId;
        }
    }

    /**
     * Gap inserted between a predecessor finishing and its successor starting,
     * in working days. Every edge uses this value until a per-edge lag column
     * is added to MilestoneDependency.
     */
    public static final int DEFAULT_LAG_DAYS = 2;

    public GraphData build(List<Milestones> milestones, UUID projectId) {
        Map<UUID, List<Milestones>> successorMap = new HashMap<>();
        Map<UUID, List<Milestones>> predecessorMap = new HashMap<>();
        Map<UUID, Integer> indegree = new HashMap<>();
        Map<String, Integer> lagMap = new HashMap<>();

        for (Milestones m : milestones) {
            successorMap.put(m.getId(), new ArrayList<>());
            predecessorMap.put(m.getId(), new ArrayList<>());
            indegree.put(m.getId(), 0);
        }

        List<MilestoneDependency> allDependencies = scheduleRepository.findByProjectId(projectId);

        for (MilestoneDependency d : allDependencies) {
            UUID milestoneId = d.getMilestone().getId();
            UUID predecessorId = d.getPredecessor().getId();

            // Guard against dependencies pointing at milestones outside this project
            if (!successorMap.containsKey(predecessorId) || !predecessorMap.containsKey(milestoneId)) {
                continue;
            }

            successorMap.get(predecessorId).add(d.getMilestone());
            predecessorMap.get(milestoneId).add(d.getPredecessor());
            indegree.merge(milestoneId, 1, Integer::sum);
            lagMap.put(GraphData.key(predecessorId, milestoneId), DEFAULT_LAG_DAYS);
        }

        return new GraphData(successorMap, predecessorMap, indegree, lagMap);
    }

    /**
     * Kahn's algorithm. Takes a DEFENSIVE COPY of indegree so the graph data
     * stays reusable — a second sort over the same GraphData must not silently
     * produce garbage. That matters now that two services share one graph.
     */
    public List<Milestones> topologicalSort(List<Milestones> milestones, GraphData data) {
        Map<UUID, Integer> indegree = new HashMap<>(data.indegree);

        List<Milestones> sorted = new ArrayList<>();
        Map<UUID, Milestones> byId = milestones.stream()
                .collect(Collectors.toMap(Milestones::getId, m -> m));

        Queue<Milestones> queue = new LinkedList<>();
        for (Milestones m : milestones) {
            if (indegree.get(m.getId()) == 0) {
                queue.offer(m);
            }
        }

        if (queue.isEmpty()) {
            throw new CircularDependencyException(
                    "No start milestone found. Every milestone has a predecessor, possible cycle.");
        }

        while (!queue.isEmpty()) {
            Milestones current = queue.poll();
            sorted.add(current);
            for (Milestones successor : data.successorsOf(current.getId())) {
                int newIndegree = indegree.merge(successor.getId(), -1, Integer::sum);
                if (newIndegree == 0) {
                    queue.offer(byId.get(successor.getId()));
                }
            }
        }

        if (sorted.size() != milestones.size()) {
            Set<UUID> sortedIds = sorted.stream().map(Milestones::getId).collect(Collectors.toSet());
            List<String> unsorted = milestones.stream()
                    .filter(m -> !sortedIds.contains(m.getId()))
                    .map(Milestones::getTitle)
                    .toList();
            throw new CircularDependencyException(
                    "Circular dependency detected involving milestones: " + unsorted);
        }

        return sorted;
    }

    /**
     * Longest-path depth for every node in ONE traversal.
     *
     * Replaces the old ScheduleService.calculateNodeLevel(), which ran a full BFS of
     * the entire graph separately for each node — O(V * (V+E)) — and discarded all
     * but one entry each time. This is O(V+E) total.
     */
    public Map<UUID, Integer> computeLevels(List<Milestones> topologicallySorted, GraphData data) {
        Map<UUID, Integer> levels = new HashMap<>();
        for (Milestones m : topologicallySorted) {
            levels.putIfAbsent(m.getId(), 0);
            int level = levels.get(m.getId());
            for (Milestones successor : data.successorsOf(m.getId())) {
                levels.merge(successor.getId(), level + 1, Math::max);
            }
        }
        return levels;
    }
}
