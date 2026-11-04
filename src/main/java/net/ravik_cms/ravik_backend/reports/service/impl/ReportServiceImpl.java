package net.ravik_cms.ravik_backend.reports.service.impl;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.accountInsights.dtos.ProjectAccountInsightDto;
import net.ravik_cms.ravik_backend.accountInsights.service.AccountInsightsService;
import net.ravik_cms.ravik_backend.approvals.Approval;
import net.ravik_cms.ravik_backend.approvals.ApprovalRepository;
import net.ravik_cms.ravik_backend.attendance.AttendanceRepository;
import net.ravik_cms.ravik_backend.authorization.AuthorizationService;
import net.ravik_cms.ravik_backend.budgetSummary.dtos.BudgetCategoryLineDto;
import net.ravik_cms.ravik_backend.budgetSummary.service.BudgetSummaryService;
import net.ravik_cms.ravik_backend.cashSummary.dtos.CashSummaryDto;
import net.ravik_cms.ravik_backend.cashSummary.service.CashSummaryService;
import net.ravik_cms.ravik_backend.common.enums.ApprovalStatus;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.costSummary.dtos.CostPaymentSummaryDto;
import net.ravik_cms.ravik_backend.costSummary.service.CostSummaryService;
import net.ravik_cms.ravik_backend.equipmentsPayout.repository.EquipmentsPayoutRepository;
import net.ravik_cms.ravik_backend.expense.repository.ExpenseRepository;
import net.ravik_cms.ravik_backend.common.enums.PaymentCategory;
import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;
import net.ravik_cms.ravik_backend.labourPayout.repository.LabourPayoutRepository;
import net.ravik_cms.ravik_backend.milestoneScheduling.ScheduleService;
import net.ravik_cms.ravik_backend.milestoneScheduling.ScheduleSummary;
import net.ravik_cms.ravik_backend.milestones.dtos.MilestoneSummaryDto;
import net.ravik_cms.ravik_backend.milestones.service.MilestonesService;
import net.ravik_cms.ravik_backend.payment.dtos.PayoutSummaryProjection;
import net.ravik_cms.ravik_backend.reports.dto.ApprovalComplianceReportDto;
import net.ravik_cms.ravik_backend.reports.dto.ApprovalReportLineDto;
import net.ravik_cms.ravik_backend.reports.dto.EquipmentActualAggregateProjection;
import net.ravik_cms.ravik_backend.reports.dto.EquipmentPayoutComparisonLineDto;
import net.ravik_cms.ravik_backend.reports.dto.EquipmentSubContractorPayoutReportDto;
import net.ravik_cms.ravik_backend.reports.dto.ExecutiveDashboardDto;
import net.ravik_cms.ravik_backend.reports.dto.JobTitleAttendanceProjection;
import net.ravik_cms.ravik_backend.reports.dto.JobTitleLabourLineDto;
import net.ravik_cms.ravik_backend.reports.dto.JobTitleWagesProjection;
import net.ravik_cms.ravik_backend.reports.dto.LabourAttendanceCostReportDto;
import net.ravik_cms.ravik_backend.reports.dto.OutstandingPayableLineDto;
import net.ravik_cms.ravik_backend.reports.dto.OutstandingPayablesReportDto;
import net.ravik_cms.ravik_backend.reports.dto.PayableCategoryLineDto;
import net.ravik_cms.ravik_backend.reports.dto.SubContractorActualAggregateProjection;
import net.ravik_cms.ravik_backend.reports.dto.SubContractorPayoutComparisonLineDto;
import net.ravik_cms.ravik_backend.reports.service.ReportService;
import net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.dtos.EquipmentRequiredInfoProjection;
import net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.repository.EquipmentRequiredRepository;
import net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.dtos.SubContractorRequiredInfoProjection;
import net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.repository.SubContractorRequiredRepository;
import net.ravik_cms.ravik_backend.subContractorPayout.repository.SubContractorPayoutRepository;
import net.ravik_cms.ravik_backend.wages.WagesRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Default {@link ReportService} implementation.
 * <p>
 * Composes the existing cash/cost/account-insight summary services in-process so every
 * per-project report is reachable from one place, without duplicating any aggregation logic
 * already owned by those services.
 */
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private static final int EXPIRING_SOON_WINDOW_DAYS = 30;

    private final CashSummaryService cashSummaryService;
    private final CostSummaryService costSummaryService;
    private final AccountInsightsService accountInsightsService;
    private final ApprovalRepository approvalRepository;
    private final LabourPayoutRepository labourPayoutRepository;
    private final EquipmentsPayoutRepository equipmentsPayoutRepository;
    private final SubContractorPayoutRepository subContractorPayoutRepository;
    private final ExpenseRepository expenseRepository;
    private final AttendanceRepository attendanceRepository;
    private final WagesRepository wagesRepository;
    private final EquipmentRequiredRepository equipmentRequiredRepository;
    private final SubContractorRequiredRepository subContractorRequiredRepository;
    private final BudgetSummaryService budgetSummaryService;
    private final MilestonesService milestonesService;
    private final ScheduleService scheduleService;
    private final AuthorizationService authorizationService;

    /**
     * Returns the project's cash position: income vs payments, net cash, outstanding cost, and
     * funding gap.
     *
     * @param projectId the project to report on
     * @return the project's cash summary
     */
    @Override
    public CashSummaryDto getCashSummary(UUID projectId) {
        authorizationService.authorize("READ_CASH_SUMMARY");
        return cashSummaryService.getCashSummary(projectId);
    }

    /**
     * Returns the project's cost/payment summary: actual cost vs paid amount by budget category,
     * with outstanding balance and payment progress.
     *
     * @param projectId the project to report on
     * @return the project's cost/payment summary, one line per budget category plus a totals line
     */
    @Override
    public CostPaymentSummaryDto getCostSummary(UUID projectId) {
        authorizationService.authorize("READ_COST_SUMMARY");
        return costSummaryService.getCostPaymentSummary(projectId);
    }

    /**
     * Returns per-account income/payment flows scoped to this project.
     *
     * @param projectId the project to report on
     * @return one entry per account used on this project, with its income/payment flows
     */
    @Override
    public List<ProjectAccountInsightDto> getAccountInsights(UUID projectId) {
        authorizationService.authorize("READ_ACCOUNT_INSIGHTS");
        return accountInsightsService.getProjectAccountFlows(projectId);
    }

    /**
     * Returns every approval on the project with its compliance status, plus roll-up counts.
     * <p>
     * An approval is treated as expired if its stored status is {@code EXPIRED}, or if its
     * {@code approvalExpiry} date has already passed regardless of stored status (the status
     * field is set manually and can go stale) — rejected approvals are excluded from expiry
     * tracking since they were never active. "Expiring soon" means within the next
     * {@value #EXPIRING_SOON_WINDOW_DAYS} days and not already expired.
     *
     * @param projectId the project to report on
     * @return every approval on the project plus status roll-up counts and total approval amount
     */
    @Override
    public ApprovalComplianceReportDto getApprovalsReport(UUID projectId) {
        authorizationService.authorize("READ_APPROVALS_REPORT");
        return buildApprovalsReport(projectId);
    }

    /**
     * Builds the approvals & compliance report without checking permissions — shared by
     * {@link #getApprovalsReport} and {@link #getExecutiveDashboard}, which each authorize
     * against their own permission before calling in.
     *
     * @param projectId the project to report on
     * @return every approval on the project plus status roll-up counts and total approval amount
     */
    private ApprovalComplianceReportDto buildApprovalsReport(UUID projectId) {
        LocalDate today = LocalDate.now();
        LocalDate expiringSoonCutoff = today.plusDays(EXPIRING_SOON_WINDOW_DAYS);

        List<Approval> approvalEntities = approvalRepository.findAllByProject_IdOrderByApprovalExpiryAsc(projectId);

        long approvedCount = 0;
        long pendingCount = 0;
        long rejectedCount = 0;
        long expiredCount = 0;
        long expiringSoonCount = 0;
        double totalApprovalAmount = 0;
        List<ApprovalReportLineDto> lines = new ArrayList<>();

        for (Approval approval : approvalEntities) {
            boolean rejected = approval.getApprovalStatus() == ApprovalStatus.REJECTED;
            boolean pastExpiry = approval.getApprovalExpiry() != null && approval.getApprovalExpiry().isBefore(today);
            boolean expired = !rejected && (approval.getApprovalStatus() == ApprovalStatus.EXPIRED || pastExpiry);
            boolean expiringSoon = !rejected && !expired && approval.getApprovalExpiry() != null
                    && !approval.getApprovalExpiry().isAfter(expiringSoonCutoff);

            switch (approval.getApprovalStatus()) {
                case APPROVED -> approvedCount++;
                case PENDING -> pendingCount++;
                case REJECTED -> rejectedCount++;
                case EXPIRED -> expiredCount++;
            }
            if (expired && approval.getApprovalStatus() != ApprovalStatus.EXPIRED) {
                expiredCount++;
            }
            if (expiringSoon) {
                expiringSoonCount++;
            }
            totalApprovalAmount += approval.getApprovalAmount() != null ? approval.getApprovalAmount() : 0;

            lines.add(ApprovalReportLineDto.builder()
                    .id(approval.getId())
                    .name(approval.getName())
                    .approvalStatus(approval.getApprovalStatus())
                    .approvalDate(approval.getApprovalDate())
                    .approvalExpiry(approval.getApprovalExpiry())
                    .approvalAmount(approval.getApprovalAmount())
                    .registrationCode(approval.getRegistrationCode())
                    .governingBody(approval.getGoverningBody())
                    .expiringSoon(expiringSoon)
                    .build());
        }

        return ApprovalComplianceReportDto.builder()
                .approvals(lines)
                .totalCount(approvalEntities.size())
                .approvedCount(approvedCount)
                .pendingCount(pendingCount)
                .rejectedCount(rejectedCount)
                .expiredCount(expiredCount)
                .expiringSoonCount(expiringSoonCount)
                .totalApprovalAmount(totalApprovalAmount)
                .build();
    }

    /**
     * Returns every not-fully-paid labour, equipment, subcontractor, or expense payout on the
     * project, ranked by outstanding amount (highest first), plus a per-category roll-up.
     * <p>
     * "Outstanding" means {@link PaymentStatus#PENDING} or {@link PaymentStatus#PARTIALLY_PAID};
     * {@link PaymentStatus#PAID} and {@link PaymentStatus#OVERPAID} are excluded since nothing is
     * owed in either case.
     *
     * @param projectId the project to report on
     * @return every outstanding payable on the project, ranked by amount owed, plus a
     *         per-{@link PaymentCategory} roll-up
     */
    @Override
    public OutstandingPayablesReportDto getOutstandingPayablesReport(UUID projectId) {
        authorizationService.authorize("READ_OUTSTANDING_PAYABLES_REPORT");
        return buildOutstandingPayablesReport(projectId);
    }

    /**
     * Builds the outstanding payables report without checking permissions — shared by
     * {@link #getOutstandingPayablesReport} and {@link #getExecutiveDashboard}, which each
     * authorize against their own permission before calling in.
     *
     * @param projectId the project to report on
     * @return every outstanding payable on the project, ranked by amount owed, plus a
     *         per-{@link PaymentCategory} roll-up
     */
    private OutstandingPayablesReportDto buildOutstandingPayablesReport(UUID projectId) {
        List<PayoutSummaryProjection> allPayouts = Stream.of(
                        labourPayoutRepository.findPayoutSummaries(projectId, null),
                        equipmentsPayoutRepository.findPayoutSummaries(projectId, null),
                        subContractorPayoutRepository.findPayoutSummaries(projectId, null),
                        expenseRepository.findPayoutSummaries(projectId, null)
                )
                .flatMap(List::stream)
                .filter(p -> p.paymentStatus() == PaymentStatus.PENDING || p.paymentStatus() == PaymentStatus.PARTIALLY_PAID)
                .toList();

        List<OutstandingPayableLineDto> lines = allPayouts.stream()
                .map(this::toOutstandingLine)
                .sorted(Comparator.comparingDouble(OutstandingPayableLineDto::outstandingAmount).reversed())
                .toList();

        Map<PaymentCategory, List<OutstandingPayableLineDto>> byCategoryMap = new EnumMap<>(PaymentCategory.class);
        for (OutstandingPayableLineDto line : lines) {
            byCategoryMap.computeIfAbsent(line.paymentCategory(), k -> new ArrayList<>()).add(line);
        }
        List<PayableCategoryLineDto> byCategory = byCategoryMap.entrySet().stream()
                .map(entry -> PayableCategoryLineDto.builder()
                        .category(entry.getKey())
                        .count(entry.getValue().size())
                        .totalOutstanding(entry.getValue().stream().mapToDouble(OutstandingPayableLineDto::outstandingAmount).sum())
                        .build())
                .toList();

        double totalOutstandingAmount = lines.stream().mapToDouble(OutstandingPayableLineDto::outstandingAmount).sum();

        return OutstandingPayablesReportDto.builder()
                .payables(lines)
                .byCategory(byCategory)
                .totalCount(lines.size())
                .totalOutstandingAmount(totalOutstandingAmount)
                .build();
    }

    /**
     * Maps a raw cross-entity payout projection into the report's line shape, computing the
     * outstanding amount ({@code totalCost - paidAmount}) along the way.
     *
     * @param payout the raw payout summary projection (labour, equipment, subcontractor, or
     *               expense)
     * @return the corresponding outstanding-payable line
     */
    private OutstandingPayableLineDto toOutstandingLine(PayoutSummaryProjection payout) {
        double totalCost = payout.totalCost() != null ? payout.totalCost() : 0;
        double paidAmount = payout.paidAmount() != null ? payout.paidAmount() : 0;
        return OutstandingPayableLineDto.builder()
                .id(payout.id())
                .paymentCategory(payout.paymentCategory())
                .referenceCode(payout.referenceCode())
                .title(payout.title())
                .totalCost(totalCost)
                .paidAmount(paidAmount)
                .outstandingAmount(totalCost - paidAmount)
                .paymentStatus(payout.paymentStatus())
                .date(payout.date())
                .build();
    }

    /**
     * Returns a per-job-title labour rollup for the project — headcount, days present/absent
     * (from attendance), and gross pay earned (from wages) — plus the project's labour payout
     * totals (invoiced, paid, outstanding).
     * <p>
     * {@code totalGrossPay} is the cost <i>earned</i> by staff via attendance, while
     * {@code totalLabourPayoutAmount} is the amount actually <i>batched into a payout</i> — they
     * can differ when earned wages haven't been rolled into a labour payout yet.
     *
     * @param projectId the project to report on
     * @return the per-job-title labour rollup plus project-wide labour payout totals
     */
    @Override
    public LabourAttendanceCostReportDto getLabourAttendanceCostReport(UUID projectId) {
        authorizationService.authorize("READ_LABOUR_ATTENDANCE_REPORT");
        return buildLabourAttendanceCostReport(projectId);
    }

    /**
     * Builds the labour & attendance cost rollup without checking permissions — shared by
     * {@link #getLabourAttendanceCostReport} and {@link #getExecutiveDashboard}, which each
     * authorize against their own permission before calling in.
     *
     * @param projectId the project to report on
     * @return the per-job-title labour rollup plus project-wide labour payout totals
     */
    private LabourAttendanceCostReportDto buildLabourAttendanceCostReport(UUID projectId) {
        List<JobTitleAttendanceProjection> attendanceByJobTitle = attendanceRepository.aggregateAttendanceByJobTitleForProject(projectId);
        List<JobTitleWagesProjection> wagesByJobTitle = wagesRepository.aggregateGrossPayByJobTitleForProject(projectId);

        Map<Long, JobTitleAttendanceProjection> attendanceMap = new HashMap<>();
        for (JobTitleAttendanceProjection p : attendanceByJobTitle) {
            attendanceMap.put(p.jobTitleId(), p);
        }
        Map<Long, Double> grossPayMap = new HashMap<>();
        for (JobTitleWagesProjection p : wagesByJobTitle) {
            grossPayMap.put(p.jobTitleId(), p.totalGrossPay() != null ? p.totalGrossPay() : 0.0);
        }

        Set<Long> jobTitleIds = new HashSet<>();
        jobTitleIds.addAll(attendanceMap.keySet());
        jobTitleIds.addAll(grossPayMap.keySet());

        List<JobTitleLabourLineDto> lines = new ArrayList<>();
        for (Long jobTitleId : jobTitleIds) {
            JobTitleAttendanceProjection attendance = attendanceMap.get(jobTitleId);
            lines.add(JobTitleLabourLineDto.builder()
                    .jobTitleId(jobTitleId)
                    .jobTitleTitle(attendance != null && attendance.jobTitleTitle() != null ? attendance.jobTitleTitle() : "Unassigned")
                    .headcount(attendance != null && attendance.headcount() != null ? attendance.headcount() : 0)
                    .daysPresent(attendance != null && attendance.daysPresent() != null ? attendance.daysPresent() : 0)
                    .daysAbsent(attendance != null && attendance.daysAbsent() != null ? attendance.daysAbsent() : 0)
                    .totalGrossPay(grossPayMap.getOrDefault(jobTitleId, 0.0))
                    .build());
        }
        lines.sort(Comparator.comparing(JobTitleLabourLineDto::jobTitleTitle, Comparator.nullsLast(Comparator.naturalOrder())));

        long totalHeadcount = lines.stream().mapToLong(JobTitleLabourLineDto::headcount).sum();
        long totalDaysPresent = lines.stream().mapToLong(JobTitleLabourLineDto::daysPresent).sum();
        long totalDaysAbsent = lines.stream().mapToLong(JobTitleLabourLineDto::daysAbsent).sum();
        double totalGrossPay = lines.stream().mapToDouble(JobTitleLabourLineDto::totalGrossPay).sum();

        double totalLabourPayoutAmount = nz(labourPayoutRepository.sumTotalAmountByProjectId(projectId));
        double totalLabourPaid = nz(labourPayoutRepository.sumPaidAmountByProjectId(projectId));

        return LabourAttendanceCostReportDto.builder()
                .byJobTitle(lines)
                .totalHeadcount(totalHeadcount)
                .totalDaysPresent(totalDaysPresent)
                .totalDaysAbsent(totalDaysAbsent)
                .totalGrossPay(totalGrossPay)
                .totalLabourPayoutAmount(totalLabourPayoutAmount)
                .totalLabourPaid(totalLabourPaid)
                .totalLabourOutstanding(totalLabourPayoutAmount - totalLabourPaid)
                .build();
    }

    /**
     * Null-safe unboxing helper — treats a {@code null} monetary/aggregate value as zero.
     *
     * @param value a possibly-null value, typically from a {@code SUM(...)} query with no rows
     * @return {@code value}, or {@code 0.0} if {@code value} is {@code null}
     */
    private double nz(Double value) {
        return value != null ? value : 0.0;
    }

    /**
     * Returns planned-vs-actual cost for every equipment and subcontractor requirement on the
     * project, matched by their respective requirement id.
     * <p>
     * Equipment's "planned cost" is only {@code plannedFuelCost} — {@code EquipmentRequired} has
     * no planned figure for operator or rental cost, so the variance there is a partial estimate,
     * not a full planned-vs-actual comparison the way subcontractor's {@code jobAmount} vs
     * {@code actualJobCost} is.
     *
     * @param projectId the project to report on
     * @return planned-vs-actual comparison lines for equipment and subcontractors, plus totals
     *         for each
     */
    @Override
    public EquipmentSubContractorPayoutReportDto getEquipmentSubContractorPayoutReport(UUID projectId) {
        authorizationService.authorize("READ_EQUIPMENT_SUBCONTRACTOR_REPORT");

        List<EquipmentPayoutComparisonLineDto> equipmentLines = buildEquipmentComparison(projectId);
        List<SubContractorPayoutComparisonLineDto> subContractorLines = buildSubContractorComparison(projectId);

        double totalPlannedEquipmentFuelCost = equipmentLines.stream().mapToDouble(EquipmentPayoutComparisonLineDto::plannedFuelCost).sum();
        double totalActualEquipmentCost = equipmentLines.stream().mapToDouble(EquipmentPayoutComparisonLineDto::actualCost).sum();
        double totalEquipmentPaid = equipmentLines.stream().mapToDouble(EquipmentPayoutComparisonLineDto::paidAmount).sum();

        double totalPlannedSubContractorAmount = subContractorLines.stream().mapToDouble(SubContractorPayoutComparisonLineDto::plannedAmount).sum();
        double totalActualSubContractorCost = subContractorLines.stream().mapToDouble(SubContractorPayoutComparisonLineDto::actualCost).sum();
        double totalSubContractorPaid = subContractorLines.stream().mapToDouble(SubContractorPayoutComparisonLineDto::paidAmount).sum();

        return EquipmentSubContractorPayoutReportDto.builder()
                .equipment(equipmentLines)
                .subContractors(subContractorLines)
                .totalPlannedEquipmentFuelCost(totalPlannedEquipmentFuelCost)
                .totalActualEquipmentCost(totalActualEquipmentCost)
                .totalEquipmentPaid(totalEquipmentPaid)
                .totalPlannedSubContractorAmount(totalPlannedSubContractorAmount)
                .totalActualSubContractorCost(totalActualSubContractorCost)
                .totalSubContractorPaid(totalSubContractorPaid)
                .build();
    }

    /**
     * Matches each equipment requirement on the project to its actual payout totals (worked
     * duration, cost, paid amount), falling back to a zero-planned line for any payout whose
     * requirement record no longer exists.
     *
     * @param projectId the project to report on
     * @return one comparison line per equipment requirement (or orphaned payout)
     */
    private List<EquipmentPayoutComparisonLineDto> buildEquipmentComparison(UUID projectId) {
        List<EquipmentRequiredInfoProjection> required = equipmentRequiredRepository.findAllByProjectId(projectId);
        List<EquipmentActualAggregateProjection> actuals = equipmentsPayoutRepository.aggregateActualsByProjectGroupedByRequirement(projectId);

        Map<Long, EquipmentActualAggregateProjection> actualsByRequirementId = new HashMap<>();
        for (EquipmentActualAggregateProjection a : actuals) {
            actualsByRequirementId.put(a.equipmentRequiredId(), a);
        }

        List<EquipmentPayoutComparisonLineDto> lines = new ArrayList<>();
        Set<Long> seenRequirementIds = new HashSet<>();

        for (EquipmentRequiredInfoProjection r : required) {
            EquipmentActualAggregateProjection actual = actualsByRequirementId.get(r.id());
            double actualCost = actual != null ? nz(actual.totalActualCost()) : 0;
            lines.add(EquipmentPayoutComparisonLineDto.builder()
                    .equipmentRequiredId(r.id())
                    .equipmentId(r.equipmentId())
                    .equipmentTitle(r.equipmentTitle())
                    .equipmentCategory(r.category())
                    .plannedDuration(r.plannedDuration())
                    .plannedFuelCost(r.plannedFuelCost())
                    .actualDuration(actual != null && actual.totalWorkedDuration() != null ? actual.totalWorkedDuration() : 0)
                    .actualCost(actualCost)
                    .paidAmount(actual != null ? nz(actual.totalPaidAmount()) : 0)
                    .costVariance(actualCost - r.plannedFuelCost())
                    .build());
            seenRequirementIds.add(r.id());
        }

        for (EquipmentActualAggregateProjection a : actuals) {
            if (!seenRequirementIds.contains(a.equipmentRequiredId())) {
                double actualCost = nz(a.totalActualCost());
                lines.add(EquipmentPayoutComparisonLineDto.builder()
                        .equipmentRequiredId(a.equipmentRequiredId())
                        .plannedDuration(0)
                        .plannedFuelCost(0)
                        .actualDuration(a.totalWorkedDuration() != null ? a.totalWorkedDuration() : 0)
                        .actualCost(actualCost)
                        .paidAmount(nz(a.totalPaidAmount()))
                        .costVariance(actualCost)
                        .build());
            }
        }
        return lines;
    }

    /**
     * Matches each subcontractor requirement on the project to its actual payout totals (cost,
     * paid amount), falling back to a zero-planned line for any payout whose requirement record
     * no longer exists.
     *
     * @param projectId the project to report on
     * @return one comparison line per subcontractor requirement (or orphaned payout)
     */
    private List<SubContractorPayoutComparisonLineDto> buildSubContractorComparison(UUID projectId) {
        List<SubContractorRequiredInfoProjection> required = subContractorRequiredRepository.findAllByProjectId(projectId);
        List<SubContractorActualAggregateProjection> actuals = subContractorPayoutRepository.aggregateActualsByProjectGroupedByRequirement(projectId);

        Map<Long, SubContractorActualAggregateProjection> actualsByRequirementId = new HashMap<>();
        for (SubContractorActualAggregateProjection a : actuals) {
            actualsByRequirementId.put(a.subContractorRequiredId(), a);
        }

        List<SubContractorPayoutComparisonLineDto> lines = new ArrayList<>();
        Set<Long> seenRequirementIds = new HashSet<>();

        for (SubContractorRequiredInfoProjection r : required) {
            SubContractorActualAggregateProjection actual = actualsByRequirementId.get(r.id());
            double plannedAmount = nz(r.jobAmount());
            double actualCost = actual != null ? nz(actual.totalActualCost()) : 0;
            lines.add(SubContractorPayoutComparisonLineDto.builder()
                    .subContractorRequiredId(r.id())
                    .subContractorId(r.subContractorId())
                    .subContractorTitle(r.subContractorTitle())
                    .plannedAmount(plannedAmount)
                    .actualCost(actualCost)
                    .paidAmount(actual != null ? nz(actual.totalPaidAmount()) : 0)
                    .costVariance(actualCost - plannedAmount)
                    .build());
            seenRequirementIds.add(r.id());
        }

        for (SubContractorActualAggregateProjection a : actuals) {
            if (!seenRequirementIds.contains(a.subContractorRequiredId())) {
                double actualCost = nz(a.totalActualCost());
                lines.add(SubContractorPayoutComparisonLineDto.builder()
                        .subContractorRequiredId(a.subContractorRequiredId())
                        .plannedAmount(0)
                        .actualCost(actualCost)
                        .paidAmount(nz(a.totalPaidAmount()))
                        .costVariance(actualCost)
                        .build());
            }
        }
        return lines;
    }

    /**
     * Returns the project's top-line health check: budget utilization, cash position, schedule
     * status, milestone progress, and outstanding risk (unpaid bills, expiring approvals, unpaid
     * labour) — composed from the other reports/summaries rather than recomputed here.
     * <p>
     * Only requires {@code READ_EXECUTIVE_DASHBOARD} — it does not require the individual
     * permission for each underlying report, since the point of a dashboard is to not need every
     * granular permission just to see a summary. The schedule block is {@code null}/zeroed when
     * the project hasn't had scheduling run yet ({@link ScheduleService#getScheduleSummary}
     * throws {@link ResourceNotFoundException} in that case, which is an expected project state
     * here, not an error).
     *
     * @param projectId the project to report on
     * @return the project's top-line dashboard, combining budget, cash, schedule/milestone, and
     *         outstanding-risk figures
     */
    @Override
    public ExecutiveDashboardDto getExecutiveDashboard(UUID projectId) {
        authorizationService.authorize("READ_EXECUTIVE_DASHBOARD");

        BudgetCategoryLineDto budgetTotals = budgetSummaryService.getProjectBudgetSummary(projectId).getTotals();
        CashSummaryDto cash = cashSummaryService.getCashSummary(projectId);
        MilestoneSummaryDto milestoneSummary = milestonesService.getMilestoneSummary(projectId);
        OutstandingPayablesReportDto payables = buildOutstandingPayablesReport(projectId);
        ApprovalComplianceReportDto approvals = buildApprovalsReport(projectId);
        LabourAttendanceCostReportDto labour = buildLabourAttendanceCostReport(projectId);

        ScheduleSummary schedule = null;
        try {
            schedule = scheduleService.getScheduleSummary(projectId);
        } catch (ResourceNotFoundException e) {
            // Project hasn't had scheduling run yet — a normal state, not a failure.
        }

        Double approvedBudget = budgetTotals.getApprovedBudget();
        double totalActualCost = nz(budgetTotals.getTotalActualCost());
        Double budgetUtilizationPct = approvedBudget != null && approvedBudget != 0 ? totalActualCost / approvedBudget : null;

        return ExecutiveDashboardDto.builder()
                .projectId(projectId)
                .approvedBudget(approvedBudget)
                .totalActualCost(totalActualCost)
                .budgetUtilizationPct(budgetUtilizationPct)
                .budgetVariance(nz(budgetTotals.getHeadlineVariance()))
                .totalIncome(nz(cash.getTotalIncome()))
                .totalPayments(nz(cash.getTotalPayments()))
                .netCashPosition(nz(cash.getNetCashPosition()))
                .fundingGap(nz(cash.getFundingGap()))
                .scheduled(schedule != null)
                .scheduleStartDate(schedule != null ? schedule.getStartDate() : null)
                .scheduleEndDate(schedule != null ? schedule.getEndDate() : null)
                .criticalPathDuration(schedule != null ? schedule.getCriticalPathDuration() : null)
                .criticalMilestoneCount(schedule != null ? schedule.getCriticalMilestoneCount() : null)
                .totalMilestoneCount(schedule != null ? schedule.getTotalMilestoneCount() : null)
                .milestonesCompleted(milestoneSummary.getCompleted())
                .milestonesInProgress(milestoneSummary.getInProgress())
                .milestonesPending(milestoneSummary.getPending())
                .milestonesOverdue(milestoneSummary.getOverdue())
                .totalOutstandingPayables(payables.totalOutstandingAmount())
                .outstandingPayablesCount(payables.totalCount())
                .approvalsExpiringSoon(approvals.expiringSoonCount())
                .approvalsExpired(approvals.expiredCount())
                .totalLabourOutstanding(labour.totalLabourOutstanding())
                .build();
    }
}
