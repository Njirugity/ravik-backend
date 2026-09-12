package net.ravik_cms.ravik_backend.phase.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneAssignmentDto {
    private UUID milestoneId;
}
