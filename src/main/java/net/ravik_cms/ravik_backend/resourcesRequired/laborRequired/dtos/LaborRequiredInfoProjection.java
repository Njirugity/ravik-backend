package net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.dtos;

public record LaborRequiredInfoProjection(
        Long id,
        Long jobTitleId,
        String jobTitleTitle,
        long workersRequired) {
}
