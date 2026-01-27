package net.ravik_cms.ravik_backend.projects;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.users.Users;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Projects {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    private String location;
    private String plotNo;
    private String address;
    private String NCAregNumber;
    private String NEMAregNumber;
    private String CountyRegNumber;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Users client;
    @ManyToMany
    @JoinTable(
            name = "project_staff",
            joinColumns = @JoinColumn(name ="project_id"),
            inverseJoinColumns = @JoinColumn(name = "staff_id")
    )
    private Set<Users> staff;
}
