package net.ravik_cms.ravik_backend.attendance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.common.enums.AttendanceStatus;
import net.ravik_cms.ravik_backend.memberships.ProjectMembership;
import net.ravik_cms.ravik_backend.milestones.Milestones;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Attendance extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;
    @ManyToOne
    @JoinColumn(name = "membership_id")
    private ProjectMembership membership;
    @ManyToOne
    @JoinColumn(name = "milestone_id")
    private Milestones milestone;
    private boolean locked = false;
}
