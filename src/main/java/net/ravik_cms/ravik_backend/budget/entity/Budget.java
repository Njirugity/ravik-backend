package net.ravik_cms.ravik_backend.budget.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;
import net.ravik_cms.ravik_backend.common.enums.BudgetSource;
import net.ravik_cms.ravik_backend.projects.Projects;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Budget extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Double amount;
    @Enumerated(EnumType.STRING)
    private BudgetCategory category;
    @Enumerated(EnumType.STRING)
    private BudgetSource source;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Projects projects;
}
