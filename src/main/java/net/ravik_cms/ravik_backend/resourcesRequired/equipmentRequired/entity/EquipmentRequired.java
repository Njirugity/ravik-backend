package net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.equipment.entity.Equipments;
import net.ravik_cms.ravik_backend.milestones.Milestones;
import net.ravik_cms.ravik_backend.projects.Projects;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentRequired extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "equipment_id")
    private Equipments equipments;
    private long plannedDuration;
    private long plannedFuelCost;
    private String notes;
    private LocalDate dateRequired;
    @ManyToOne
    @JoinColumn(name ="milestone_id")
    private Milestones milestone;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Projects project;
}