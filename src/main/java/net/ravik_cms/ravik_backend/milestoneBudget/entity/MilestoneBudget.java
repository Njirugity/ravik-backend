package net.ravik_cms.ravik_backend.milestoneBudget.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;
import net.ravik_cms.ravik_backend.common.enums.BudgetSource;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MilestoneBudget extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "milestone_id")
    private Milestones milestone;
    @Enumerated(EnumType.STRING)
    private BudgetCategory category;
    private Double amount;
    @Enumerated(EnumType.STRING)
    private BudgetSource source = BudgetSource.DERIVED;
}
