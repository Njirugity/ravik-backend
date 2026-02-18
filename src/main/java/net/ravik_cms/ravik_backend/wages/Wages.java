package net.ravik_cms.ravik_backend.wages;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.memberships.ProjectMembership;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Wages {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private double amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private int numberOfDays;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private ProjectMembership membership;

    public Wages(Double amount, LocalDate startDate, LocalDate endDate, int numberOfDays, ProjectMembership membership) {
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfDays = numberOfDays;
        this.membership = membership;
    }
}
