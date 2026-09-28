package net.ravik_cms.ravik_backend.milestoneScheduling.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * A milestone on the critical path, as shown in the schedule summary. Deliberately narrow：
 * {@link net.ravik_cms.ravik_backend.milestones.entity.Milestones} carries a {@code project}
 * reference, and {@code Projects}/{@code Client} reference each other back (client.projects),
 * so serialising the raw entity here recurses through that cycle indefinitely.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CriticalPathMilestoneDto {
    private UUID id;
    private String title;
    private int duration;
}
