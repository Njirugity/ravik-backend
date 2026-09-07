package net.ravik_cms.ravik_backend.equipmentsPayout.entity;

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
import net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.entity.EquipmentRequired;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentsPayout extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "equipment_required_id")
    private EquipmentRequired equipmentRequired;
    @ManyToOne
    @JoinColumn(name = "milestone_id")
    private Milestones milestone;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Projects project;
    private long workedDuration;
    private long rentalCost;
    private long fuelCost;
    private long operatorCost;
    private LocalDate dateUsed;
    private String notes;
    @Column(unique = true)
    private String referenceCode;
    private Double paidAmount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @Enumerated(EnumType.STRING)
    private PaymentCategory paymentCategory = PaymentCategory.EQUIPMENT;
    @Version
    private Integer version;
}
