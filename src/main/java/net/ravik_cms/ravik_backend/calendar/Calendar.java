package net.ravik_cms.ravik_backend.calendar;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.projects.Projects;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Calendar {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private boolean monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    @ElementCollection
    @CollectionTable(
            name = "calendar_holidays",
            joinColumns = @JoinColumn(name = "calendar_id")
    )
    @Column(name = "holiday_date")
    private Set<LocalDate> holidays =  new HashSet<>();
    @OneToOne
    @JoinColumn(name = "project_id")
    private Projects project;

}
