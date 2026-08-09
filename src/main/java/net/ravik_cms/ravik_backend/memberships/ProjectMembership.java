package net.ravik_cms.ravik_backend.memberships;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.common.enums.PaymentFrequency;
import net.ravik_cms.ravik_backend.common.enums.RoleCategory;
import net.ravik_cms.ravik_backend.common.enums.StaffStatus;
import net.ravik_cms.ravik_backend.jobTitles.JobTitles;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.roles.Roles;
import net.ravik_cms.ravik_backend.users.Users;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMembership extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Projects project;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Roles role;
    @Enumerated(EnumType.STRING)
    private StaffStatus status;
    private boolean ownership= false;
    private Double baseWage;
    @Enumerated(EnumType.STRING)
    private PaymentFrequency frequency;
    @ManyToOne
    @JoinColumn(name = "job_title_id")
    private JobTitles jobTitle;
    private boolean generateAttendance = true;
}
