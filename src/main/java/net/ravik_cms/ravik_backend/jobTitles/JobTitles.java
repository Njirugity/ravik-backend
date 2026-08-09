package net.ravik_cms.ravik_backend.jobTitles;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.enums.PaymentFrequency;
import net.ravik_cms.ravik_backend.projects.Projects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobTitles {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private Double baseWage;
    private PaymentFrequency frequency;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Projects project;

}
