package net.ravik_cms.ravik_backend.milestones.dtos;

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
    private String phaseTitle;
    private String warningMessage;
    /** Why the milestone is suggested for the date; null when it is offered but not suggested. */
    private String matchReason;
    private boolean suggested;
}
