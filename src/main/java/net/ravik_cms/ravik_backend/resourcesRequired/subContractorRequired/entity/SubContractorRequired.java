package net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.subContractor.entity.SubContractor;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubContractorRequired extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "sub_contractor_id")
    private SubContractor subContractor;
    @ManyToOne
    @JoinColumn(name = "milestone_id")
    private Milestones milestone;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Projects project;
    private LocalDate dateRequired;
    private Double jobAmount;
    private String jobTitle;
    private String description;
}
