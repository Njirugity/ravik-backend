package net.ravik_cms.ravik_backend.materials.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.common.enums.DeletionStatus;
import net.ravik_cms.ravik_backend.common.enums.PaymentCategory;
import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;
import net.ravik_cms.ravik_backend.projects.entity.Projects;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Materials delivered to the site. Belongs to a project, not a milestone — a user might bulk-order
 * a project's materials in one delivery. {@link MaterialUsed} tracks which delivered stock a
 * milestone actually consumed.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialDelivered extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "material_list_id")
    private MaterialList materialList;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Projects project;

    private Double quantityDelivered;
    private Double unitPrice;
    private LocalDate dateDelivered;

    private Double totalCost;
    private Double paidAmount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @Enumerated(EnumType.STRING)
    private PaymentCategory paymentCategory = PaymentCategory.MATERIALS;
    @Column(unique = true)
    private String referenceCode;

    // Not on MVP — receipts/photos upload plumbing isn't built yet, field is a placeholder.
    private String supportingDocuments;

    @Enumerated(EnumType.STRING)
    private DeletionStatus status = DeletionStatus.NOT_DELETED;
}
