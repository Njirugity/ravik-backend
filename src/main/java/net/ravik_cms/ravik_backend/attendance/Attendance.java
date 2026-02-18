package net.ravik_cms.ravik_backend.attendance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.memberships.ProjectMembership;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDate date;
    private boolean present;
    @ManyToOne
    @JoinColumn(name = "membership_id")
    private ProjectMembership membership;
    private boolean locked = false;
}
