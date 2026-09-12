package net.ravik_cms.ravik_backend.expense.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.common.enums.PaymentCategory;
import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;
import net.ravik_cms.ravik_backend.expenseCategory.entity.ExpenseCategory;
import net.ravik_cms.ravik_backend.projects.Projects;

import java.time.LocalDate;
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
    private LocalDate date;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private ExpenseCategory category;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Projects project;
    @Column(unique = true)
    private String referenceCode;
    private Double paidAmount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @Enumerated(EnumType.STRING)
    private PaymentCategory paymentCategory = PaymentCategory.EXPENSES;
}
