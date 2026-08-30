package net.ravik_cms.ravik_backend.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.account.entity.Accounts;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.common.enums.PaymentCategory;
import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;
import net.ravik_cms.ravik_backend.projects.Projects;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private LocalDate datePaid;
    private String payee;
    private Double amount;
    @Enumerated(EnumType.STRING)
    private PaymentCategory paymentCategory;
    private Long referenceId;
    private String referenceCode;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Projects project;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Accounts account;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
}
