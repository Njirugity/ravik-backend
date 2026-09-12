package net.ravik_cms.ravik_backend.materials.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;
import net.ravik_cms.ravik_backend.projects.Projects;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Materials actually used on site for a milestone. References {@link MaterialDelivered} rather
 * than {@link MaterialList} directly, so usage is always deducted from stock that was actually
 * delivered — not just planned/estimated.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialUsed extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "material_delivered_id")
    private MaterialDelivered materialDelivered;

    @ManyToOne
    @JoinColumn(name = "milestone_id")
    private Milestones milestone;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Projects project;

    private Double quantityUsed;
    private LocalDate dateUsed;
}
