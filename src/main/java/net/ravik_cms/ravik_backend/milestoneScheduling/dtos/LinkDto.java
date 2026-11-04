package net.ravik_cms.ravik_backend.milestoneScheduling.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkDto {
    private UUID id;
    private UUID sourceId;
    private UUID targetId;
    private String dependencyType;
    private Integer lag;
    private boolean isCritical;
    private String label;
}
