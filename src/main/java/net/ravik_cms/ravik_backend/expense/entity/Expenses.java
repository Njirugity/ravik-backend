package net.ravik_cms.ravik_backend.expense.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.expenseCategory.entity.ExpenseCategory;
import net.ravik_cms.ravik_backend.projects.Projects;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Expenses extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    private String description;
    private Double amount;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private ExpenseCategory category;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Projects project;



}
