package net.ravik_cms.ravik_backend.expenseCategory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;
import net.ravik_cms.ravik_backend.projects.Projects;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseCategory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    @Enumerated(EnumType.STRING)
    private BudgetCategory budgetCategory = BudgetCategory.OTHERS;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Projects projects;
}
