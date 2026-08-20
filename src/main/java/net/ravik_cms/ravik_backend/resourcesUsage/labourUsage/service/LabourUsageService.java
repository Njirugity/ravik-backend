package net.ravik_cms.ravik_backend.resourcesUsage.labourUsage.service;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.attendance.AttendanceRepository;
import net.ravik_cms.ravik_backend.resourcesUsage.labourUsage.dtos.LabourComparisonDto;
import net.ravik_cms.ravik_backend.resourcesUsage.labourUsage.dtos.LabourUsageProjection;
import net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.dtos.LaborRequiredInfoProjection;
import net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.repository.LaborRequiredRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LabourUsageService {
    private final AttendanceRepository attendanceRepository;
    private final LaborRequiredRepository laborRequiredRepository;

    public List<LabourComparisonDto> getLabourComparison(UUID milestoneId) {
        List<LaborRequiredInfoProjection> required = laborRequiredRepository.findAllByMilestoneId(milestoneId);
        List<LabourUsageProjection> used = attendanceRepository.findLabourUsedByMilestone(milestoneId);

        Map<Long, LabourUsageProjection> usedByJobTitle = used.stream()
                .collect(Collectors.toMap(LabourUsageProjection::jobTitleId, Function.identity()));

        List<LabourComparisonDto> comparisons = new ArrayList<>();
        Set<Long> seenJobTitles = new HashSet<>();

        for (LaborRequiredInfoProjection r : required) {
            LabourUsageProjection u = usedByJobTitle.get(r.jobTitleId());
            comparisons.add(new LabourComparisonDto(
                    r.jobTitleId(),
                    r.jobTitleTitle(),
                    r.workersRequired(),
                    u != null ? u.labourUsed() : 0));
            seenJobTitles.add(r.jobTitleId());
        }

        for (LabourUsageProjection u : used) {
            if (!seenJobTitles.contains(u.jobTitleId())) {
                comparisons.add(new LabourComparisonDto(u.jobTitleId(), u.jobTitleTitle(), 0, u.labourUsed()));
            }
        }

        return comparisons;
    }
}
