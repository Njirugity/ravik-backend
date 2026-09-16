package net.ravik_cms.ravik_backend.milestoneScheduling.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AONDiagramDto {
    private List<NodeDto> nodes;
    private List<LinkDto> links;
    private UUID projectId;
    private String projectTitle;
    private LocalDate projectStart;
    private LocalDate projectEnd;
}
