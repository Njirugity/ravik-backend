package net.ravik_cms.ravik_backend.resourcesUsage.labourUsage.dtos;

public record LabourComparisonDto(
        Long jobTitleId,
        String jobTitleTitle,
        long workersRequired,
        long labourUsed) {
}
