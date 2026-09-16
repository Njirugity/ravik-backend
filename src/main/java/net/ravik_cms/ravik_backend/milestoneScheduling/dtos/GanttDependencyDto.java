package net.ravik_cms.ravik_backend.milestoneScheduling;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GanttDependencyDto {
    private UUID predecessorId;
    private UUID successorId;
    private String type;
    private Integer lag;
}
