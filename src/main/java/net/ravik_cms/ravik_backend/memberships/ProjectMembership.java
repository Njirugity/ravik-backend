package net.ravik_cms.ravik_backend.memberships;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.roles.Roles;
import net.ravik_cms.ravik_backend.users.Users;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMembership {
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
    private String status;
    private boolean ownership= false;
    private Double baseDailyWage;
}
