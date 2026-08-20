package net.ravik_cms.ravik_backend.subContractorPayout.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.milestones.Milestones;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.entity.SubContractorRequired;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubContractorPayout extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "sub_contractor_required_id")
    private SubContractorRequired subContractorRequired;
    @ManyToOne
    @JoinColumn(name = "milestone_id")
    private Milestones milestone;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Projects project;
    private Double actualJobCost;
    private Double paidAmount;
    private LocalDate jobDate;
}
