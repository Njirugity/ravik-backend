package net.ravik_cms.ravik_backend.equipment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.common.enums.EquipmentCategory;
import net.ravik_cms.ravik_backend.projects.Projects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Equipments extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(value = EnumType.STRING)
    private EquipmentCategory category;
    private String title;
    private Long capacity;
    private String metric;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Projects project;
}
