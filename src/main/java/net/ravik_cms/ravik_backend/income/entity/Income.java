package net.ravik_cms.ravik_backend.income.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.account.entity.Accounts;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.projects.Projects;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Income extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String source;
    private String description;
    private Double amount;
    private LocalDate date;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Accounts account;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Projects project;
}
