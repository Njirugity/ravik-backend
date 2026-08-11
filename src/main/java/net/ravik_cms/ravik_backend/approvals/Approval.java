package net.ravik_cms.ravik_backend.approvals;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.common.enums.ApprovalStatus;
import net.ravik_cms.ravik_backend.projects.Projects;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Approval extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private ApprovalStatus approvalStatus;
    private LocalDate approvalDate;
    private LocalDate approvalExpiry;
    private Double approvalAmount;
    private String registrationCode;
    private String governingBody;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Projects project;
}
