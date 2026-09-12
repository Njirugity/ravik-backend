package net.ravik_cms.ravik_backend.resourcesUsage.labourUsage.dtos;

public record LabourUsageProjection(
        Long jobTitleId,
        String jobTitleTitle,
        long labourUsed) {
}
