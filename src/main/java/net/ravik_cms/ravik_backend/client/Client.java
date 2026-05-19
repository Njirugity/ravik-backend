package net.ravik_cms.ravik_backend.client;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.projects.Projects;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    @Id
    private UUID id;
    private String userName;
    private String email;
    private String phoneNumber;
    @OneToMany
    @JoinColumn(name = "projects_id")
    private Set<Projects> projects;

    public void addProjects(Projects project){
        this.projects.add(project);
    }
}
