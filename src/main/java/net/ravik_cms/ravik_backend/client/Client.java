package net.ravik_cms.ravik_backend.client;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.users.Users;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Client {
    @Id
    private UUID id;
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Users user;
    @OneToMany(mappedBy = "client")
    private Set<Projects> projects;
}
