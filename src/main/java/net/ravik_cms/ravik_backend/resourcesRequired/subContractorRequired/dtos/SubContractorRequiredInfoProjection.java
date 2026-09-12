package net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.dtos;

import java.time.LocalDate;
import java.util.UUID;

public record SubContractorRequiredInfoProjection(
        Long id,
        Long subContractorId,
        String subContractorTitle,
        UUID milestoneId,
        LocalDate dateRequired,
        Double jobAmount,
        String jobTitle,
        String description) {
}
