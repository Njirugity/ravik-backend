package net.ravik_cms.ravik_backend.materials.service;

import net.ravik_cms.ravik_backend.materials.dto.MaterialsReportDto;

import java.util.UUID;

public interface MaterialsReportService {
    MaterialsReportDto getProjectReport(UUID projectId);
}
