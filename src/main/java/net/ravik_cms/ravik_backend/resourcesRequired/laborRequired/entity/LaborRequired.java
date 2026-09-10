package net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.jobTitles.JobTitles;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;
import net.ravik_cms.ravik_backend.projects.Projects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LaborRequired extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "job_title_id")
    private JobTitles jobTitle;
    @ManyToOne
    @JoinColumn(name = "milestone_id")
    private Milestones milestone;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Projects project;
    private long workersRequired;
}
