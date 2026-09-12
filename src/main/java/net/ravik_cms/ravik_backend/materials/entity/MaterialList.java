package net.ravik_cms.ravik_backend.materials.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.common.enums.DeletionStatus;
import net.ravik_cms.ravik_backend.projects.Projects;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialList extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String metric;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Projects project;

    @Enumerated(EnumType.STRING)
    private DeletionStatus status = DeletionStatus.NOT_DELETED;
}
