package net.ravik_cms.ravik_backend.wages;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.memberships.ProjectMembership;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Wages extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Double baseWage;
    private Double grossPay;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long workedDays;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private ProjectMembership membership;
}
