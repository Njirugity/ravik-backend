package net.ravik_cms.ravik_backend.labourPayout.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.common.enums.PaymentCategory;
import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LabourPayout extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Double totalAmount;
    @Column(unique = true)
    private String referenceCode;
    private Double paidAmount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private UUID projectId;
    @Enumerated(EnumType.STRING)
    private PaymentCategory paymentCategory = PaymentCategory.LABOUR;
    @Version
    private Integer version;
}
