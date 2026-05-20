package net.ravik_cms.ravik_backend.milestones;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;
import net.ravik_cms.ravik_backend.phase.Phases;
import net.ravik_cms.ravik_backend.projects.Projects;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor

@Entity
public class Milestones extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    private String description;
    private LocalDate plannedStartDate;
    private LocalDate plannedEndDate;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;
    private Double budget;
    @Enumerated(EnumType.STRING)
    private ProgressStatus status;
    @ManyToOne
    @JoinColumn(name = "phase_id")
    private Phases phase;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Projects project;

}
