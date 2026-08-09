package net.ravik_cms.ravik_backend.wages;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.attendance.Attendance;
import net.ravik_cms.ravik_backend.attendance.AttendanceRepository;
import net.ravik_cms.ravik_backend.common.enums.PaymentFrequency;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.memberships.ProjectMembership;
import net.ravik_cms.ravik_backend.memberships.ProjectMembershipRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WagesService {
    private final WagesRepository wagesRepository;
    private final AttendanceRepository attendanceRepository;
    private final ProjectMembershipRepository membershipRepository;

//    public List<WagePreviewDto> displayWages(UUID projectId, LocalDate startDate, LocalDate endDate) {
//        List<ProjectMembership> memberships = membershipRepository.findAllByProjectId(projectId);
//        List<Wages> wages = wagesRepository.findAllByMembershipInAndStartDateAndEndDate(memberships, startDate, endDate);
//        Map<ProjectMembership, List<Wages>> grouped = wages.stream()
//                .collect(Collectors.groupingBy(Wages::getMembership));
//        List<WagePreviewDto> wagesRows = new ArrayList<>();
//        for (Wages wage : wages) {
//            WagePreviewDto record = getWageInfoDto(wage);
//            wagesRows.add(record);
//        }
//        return wagesRows;
//    }
//
//    private static @NonNull WagePreviewDto getWageInfoDto(Wages wage) {
//        WagePreviewDto record = new WagePreviewDto();
//        record.setUserName(wage.getMembership().getUser().getUserName());
//        record.setStartDate(wage.getStartDate());
//        record.setEndDate(wage.getEndDate());
//        record.setBaseWage(wage.getMembership().getBaseDailyWage());
//        record.setWorkingDays(wage.getNumberOfDays());
//        record.setTotalAmount(wage.getAmount());
//        record.setMembershipId(wage.getMembership().getId());
//        record.setRole(wage.getMembership().getRole().getName());
//        return record;
//    }
    public Page<WagePreviewDto> previewDailyWages(UUID projectId, LocalDate startDate, LocalDate endDate,
                                             String jobTitle, String search, Pageable pageable){
        if(startDate == null && endDate == null){
            LocalDate date = LocalDate.now();
            startDate = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            endDate = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        }
        Page<WageCalculationDataProjection> page = wagesRepository.findWageDataForDailyFrequency(
                projectId, startDate, endDate,jobTitle, search, pageable);

        LocalDate finalStartDate = startDate;
        LocalDate finalEndDate = endDate;
        return page.map(p->{
            Double grossPay = p.baseWage() * p.workedDays();
            WagePreviewDto dto = new WagePreviewDto();
            dto.setMembershipId(p.membershipId());
            dto.setUserName(p.userName());
            dto.setJobTitle(p.jobTitle());
            dto.setStartDate(finalStartDate);
            dto.setEndDate(finalEndDate);
            dto.setBaseWage(p.baseWage());
            dto.setGrossPay(grossPay);
            dto.setWorkedDays(p.workedDays());
            dto.setFrequency(p.frequency());
            return dto;
        });
    }

    Page<WagePreviewDto> previewMonthlyWages(UUID projectId, int year, int month,
                                             String jobTitle, String search, Pageable pageable){
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        Page<ProjectMembership> page = wagesRepository.findWageDataForMonthlyFrequency(
                projectId, startDate, endDate, jobTitle, search, pageable
        );

        return page.map(p->{
            WagePreviewDto dto = new WagePreviewDto();
            dto.setMembershipId(p.getId());
            dto.setUserName(p.getUser().getUserName());
            dto.setJobTitle(p.getJobTitle().getTitle());
            dto.setStartDate(startDate);
            dto.setEndDate(endDate);
            dto.setBaseWage(p.getBaseWage());
            dto.setGrossPay(p.getBaseWage());
            dto.setFrequency(p.getFrequency());
            return dto;
        });
    }
    @Transactional
    public void confirmDailyWage(List<CreateWageRecordDto> request){
        if (request == null || request.isEmpty()) {
            return;
        }
       List<Long> memberIds = request.stream()
               .map(CreateWageRecordDto::getMemberId).distinct().toList();
       LocalDate start =  request.getFirst().getStartDate();
       LocalDate end =  request.getFirst().getEndDate();
       Map<Long, ProjectMembership> membershipMap = membershipRepository.findAllById(memberIds).stream()
               .collect(Collectors.toMap(ProjectMembership::getId, m->m));
        List<Attendance> attendances = attendanceRepository.findAttendanceForGeneratingWage(memberIds,start, end);
        List<Wages> wagesToSave = new ArrayList<>();
        Map<Long, Wages> wageByMembership = new HashMap<>();
        for(CreateWageRecordDto dto : request){
            ProjectMembership membership = membershipMap.get(dto.getMemberId());
            if (membership == null) {
                throw new ResourceNotFoundException("Membership not found");
            }

            Wages wage = new Wages();
            wage.setMembership(membership);
            wage.setStartDate(dto.getStartDate());
            wage.setEndDate(dto.getEndDate());
            wage.setBaseWage(dto.getBaseWage());
            wage.setGrossPay(dto.getGrossPay());
            wage.setWorkedDays(dto.getWorkedDays());

            wagesToSave.add(wage);
            wageByMembership.put(dto.getMemberId(), wage);
        }
        wagesRepository.saveAll(wagesToSave);
        for(Attendance a :  attendances){
            Wages wage = wageByMembership.get(a.getMembership().getId());
            a.setWage(wage);
        }
    }
    @Transactional
    public void confirmMonthlyWage(List<CreateWageRecordDto> request){
        if (request == null || request.isEmpty()) {
            return;
        }
        List<Long> memberIds = request.stream()
                .map(CreateWageRecordDto::getMemberId).distinct().toList();
        Map<Long, ProjectMembership> membershipMap = membershipRepository.findAllById(memberIds).stream()
                .collect(Collectors.toMap(ProjectMembership::getId, m->m));

        List<Wages> records = request.stream()
                .map(r->{
                    Wages wage = new Wages();
                    ProjectMembership membership = membershipMap.get(r.getMemberId());
                    wage.setMembership(membership);
                    wage.setStartDate(r.getStartDate());
                    wage.setEndDate(r.getEndDate());
                    wage.setGrossPay(r.getGrossPay());
                    wage.setBaseWage(r.getBaseWage());
                    return wage;
                }).toList();
        wagesRepository.saveAll(records);
    }

    public Page<WageHistoryProjection> getWageHistory(UUID projectId,String jobTitle, String search,
                                                      Pageable pageable){
        return wagesRepository.findWageHistory(projectId, jobTitle, search, pageable);
    }
    @Transactional
    public void deleteWage(Long id){
        Wages wage = wagesRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Wage record not found"));
        List<Attendance> attendances = attendanceRepository.findByWage(wage);
        attendances.forEach(a->a.setWage(null));
        wagesRepository.delete(wage);
    }

}
