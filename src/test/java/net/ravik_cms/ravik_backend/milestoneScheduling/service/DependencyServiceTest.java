package net.ravik_cms.ravik_backend.milestoneScheduling.service;

import net.ravik_cms.ravik_backend.common.exception.CircularDependencyException;
import net.ravik_cms.ravik_backend.common.exception.InvalidDependencyException;
import net.ravik_cms.ravik_backend.milestoneScheduling.entity.MilestoneDependency;
import net.ravik_cms.ravik_backend.milestoneScheduling.repository.ScheduleRepository;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;
import net.ravik_cms.ravik_backend.milestones.repository.MilestonesRepository;
import net.ravik_cms.ravik_backend.projects.entity.Projects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DependencyServiceTest {

    private ScheduleRepository scheduleRepository;
    private DependencyService dependencyService;

    /** predecessorId -> successors, i.e. the edges already saved. */
    private final Map<UUID, List<Milestones>> successors = new HashMap<>();
    private final Projects project = new Projects();
    private Milestones a, b, c;

    @BeforeEach
    void setUp() {
        scheduleRepository = mock(ScheduleRepository.class);
        MilestonesRepository milestonesRepository = mock(MilestonesRepository.class);
        dependencyService = new DependencyService(scheduleRepository, milestonesRepository, mock(ScheduleService.class));

        project.setId(UUID.randomUUID());
        a = milestone("A");
        b = milestone("B");
        c = milestone("C");
        for (Milestones m : List.of(a, b, c)) {
            when(milestonesRepository.findById(m.getId())).thenReturn(Optional.of(m));
        }
        when(scheduleRepository.findSuccessorByMilestoneId(any()))
                .thenAnswer(inv -> successors.getOrDefault(inv.getArgument(0), List.of()));
    }

    private Milestones milestone(String title) {
        Milestones m = new Milestones();
        m.setId(UUID.randomUUID());
        m.setTitle(title);
        m.setProject(project);
        return m;
    }

    /** Records that {@code predecessor} must finish before {@code milestone}. */
    private void edge(Milestones predecessor, Milestones milestone) {
        successors.computeIfAbsent(predecessor.getId(), k -> new ArrayList<>()).add(milestone);
        when(scheduleRepository.existsByMilestoneIdAndPredecessorId(milestone.getId(), predecessor.getId()))
                .thenReturn(true);
    }

    @Test
    void rejectsDirectCycle() {
        edge(a, b);
        assertThrows(CircularDependencyException.class,
                () -> dependencyService.addPredecessor(a.getId(), b.getId()));
        verify(scheduleRepository, never()).save(any(MilestoneDependency.class));
    }

    @Test
    void rejectsTransitiveCycle() {
        edge(a, b);
        edge(b, c);
        assertThrows(CircularDependencyException.class,
                () -> dependencyService.addPredecessor(a.getId(), c.getId()));
    }

    @Test
    void allowsRedundantForwardEdge() {
        edge(a, b);
        edge(b, c);
        assertDoesNotThrow(() -> dependencyService.addPredecessor(c.getId(), a.getId()));
        verify(scheduleRepository).save(any(MilestoneDependency.class));
    }

    @Test
    void rejectsDuplicateAndSelfDependency() {
        edge(a, b);
        assertThrows(InvalidDependencyException.class,
                () -> dependencyService.addPredecessor(b.getId(), a.getId()));
        assertThrows(InvalidDependencyException.class,
                () -> dependencyService.addPredecessor(a.getId(), a.getId()));
    }
}
