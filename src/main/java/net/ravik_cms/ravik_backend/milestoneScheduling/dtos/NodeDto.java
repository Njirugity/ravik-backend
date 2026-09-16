package net.ravik_cms.ravik_backend.milestoneScheduling.dtos;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeDto {
    private UUID id;
    private String title;
    private boolean isMilestone;
    private int x;
    private int y;
}
