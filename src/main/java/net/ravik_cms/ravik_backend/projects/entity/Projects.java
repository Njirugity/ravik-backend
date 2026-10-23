package net.ravik_cms.ravik_backend.projects.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.client.Client;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;
import net.ravik_cms.ravik_backend.locations.entity.Ward;
import net.ravik_cms.ravik_backend.users.Users;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Projects extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    private String plotNo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id")
    private Ward ward;
    private String street;
    private String type;
    private Double sizeSquareMeters;
    private ProgressStatus progress;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    @ManyToMany
    @JoinTable(
            name = "project_staff",
            joinColumns = @JoinColumn(name ="project_id"),
            inverseJoinColumns = @JoinColumn(name = "staff_id")
    )
    private Set<Users> staff;
    private LocalDate plannedStart;
    private LocalDate plannedEnd;
    LocalDate dataDate;
    LocalDate forecastEnd;
    LocalDate contractFinish;
    private boolean scheduled = false;
}
