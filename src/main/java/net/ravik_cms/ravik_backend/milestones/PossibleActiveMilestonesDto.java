package net.ravik_cms.ravik_backend.milestones;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PossibleActiveMilestonesDto {
    private UUID id;
    private String title;
    private String warningMessage;
    private String matchReason;
}
