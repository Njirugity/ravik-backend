package net.ravik_cms.ravik_backend.users;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.roles.RoleInfoDto;
import net.ravik_cms.ravik_backend.roles.Roles;
import net.ravik_cms.ravik_backend.salaries.Salaries;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String userName;
    private String email;
    private String password;
    private String phoneNumber;
    private String idNumber;

    @OneToMany(mappedBy = "user")
    private Set<Salaries> salary;
}
