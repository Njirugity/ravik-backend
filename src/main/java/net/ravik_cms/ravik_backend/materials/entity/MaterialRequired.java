package net.ravik_cms.ravik_backend.materials.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.milestones.Milestones;
import net.ravik_cms.ravik_backend.projects.Projects;

import java.util.UUID;

/**
 * The estimated quantity/cost of a {@link MaterialList} type a {@link Milestones} needs to
 * complete — the baseline material_used/material_delivered are compared against for variance.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialRequired extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "material_list_id")
    private MaterialList materialList;

    @ManyToOne
    @JoinColumn(name = "milestone_id")
    private Milestones milestone;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Projects project;

    private Double quantityRequired;
    private Double unitPrice;
}
