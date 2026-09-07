package net.ravik_cms.ravik_backend.subContractorPayout.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.common.enums.PaymentCategory;
import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;
import net.ravik_cms.ravik_backend.milestones.Milestones;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.entity.SubContractorRequired;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubContractorPayout extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
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
    @Column(unique = true)
    private String referenceCode;
    private Double paidAmount;
    private LocalDate jobDate;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @Enumerated(EnumType.STRING)
    private PaymentCategory paymentCategory = PaymentCategory.SUBCONTRACTOR;
    @Version
    private Integer version;
}
