package net.ravik_cms.ravik_backend.attendance;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.AttendanceStatus;
import net.ravik_cms.ravik_backend.common.enums.StaffStatus;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.memberships.ProjectMembership;
import net.ravik_cms.ravik_backend.memberships.ProjectMembershipRepository;
import net.ravik_cms.ravik_backend.milestones.Milestones;
import net.ravik_cms.ravik_backend.milestones.MilestonesMapper;
import net.ravik_cms.ravik_backend.milestones.MilestonesService;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final ProjectsRepository projectsRepository;
    private final MilestonesService milestonesService;
    private final MilestonesMapper milestonesMapper;

    @Scheduled(cron = "0 0 0 * * *")
    public void generateCurrentDay(){
        LocalDate today = LocalDate.now();
        List<Projects> projects = projectsRepository.findAll();
        for (Projects project: projects){
            try{
                generateSingleDate(project.getId(), today);
            }catch (Exception e) {
                // Log error for specific project so the whole job doesn't fail
                System.out.println(e);
            }
        }

    }
    @Transactional
    public AttendanceResultDto generateSingleDate(UUID projectId, LocalDate date){
        List<ProjectMembership> memberships = attendanceRepository.findMembershipsWithoutAttendance(
                projectId, date);
        Milestones milestone = milestonesMapper.toEntityFromInfoDto(milestonesService.getActiveMilestone(projectId));
        milestonesService.setMilestoneActualStartDate(milestone);
        List<Attendance> newRecords = memberships.stream()
                .map(m-> {
                    Attendance a = new Attendance();
                    a.setMembership(m);
                    a.setDate(date);
                    a.setStatus(AttendanceStatus.ABSENT);
                    a.setMilestone(milestone);
                    return a;
                }).collect(Collectors.toList());
        attendanceRepository.saveAll(newRecords);
        return new AttendanceResultDto(0, newRecords.size());
    }

    public List<AttendanceSingleDayInfoDto> displayRecords(UUID projectId){
        LocalDate today = LocalDate.now();
        List<Attendance> records = attendanceRepository.findByProjectIdAndDate(projectId, today);
        return records.stream()
                .map(a->{
                    AttendanceSingleDayInfoDto dto = new AttendanceSingleDayInfoDto();
                    dto.setMemberId(a.getMembership().getId());
                    dto.setUserName(a.getMembership().getUser().getUserName());
                    dto.setRole(a.getMembership().getRole().getName());
                    dto.setAttendanceId(a.getId());
                    dto.setDate(a.getDate());
                    dto.setStatus(a.getStatus());
                    dto.setMilestoneTitle(a.getMilestone().getTitle());
                    return dto;
                }).collect(Collectors.toList());
    }

    public List<AttendanceSummaryDto> summary (UUID projectId){
        LocalDate today = LocalDate.now();
        LocalDate firstDay = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate lastDay = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        Long daysOfWeek = ChronoUnit.DAYS.between(firstDay, lastDay);

        List<Attendance> records = attendanceRepository.findAttendanceForSummary(projectId, firstDay, lastDay);
        String weekRange = firstDay.getMonth().name() + " " + firstDay.getDayOfMonth() + " - " + lastDay.getDayOfMonth();
        return records.stream()
                .collect(Collectors.groupingBy(Attendance::getMembership)) // Group by member
                .entrySet().stream()
                .map(entry -> {
                    ProjectMembership m = entry.getKey();
                    List<Attendance> userRecords = entry.getValue();

                    long presentCount = userRecords.stream()
                            .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                            .count();

                    AttendanceSummaryDto dto = new AttendanceSummaryDto();
                    dto.setUserName(m.getUser().getUserName());
                    dto.setRole(m.getRole().getName());
                    dto.setDaysPresent(presentCount);
                    dto.setDaysOfWeek(daysOfWeek);
                    dto.setWeekOf(weekRange);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateBulkAttendance(List<AttendanceDayDto> dtos){
        List<Attendance> toUpdate = dtos.stream()
                .map(d->{
                    Attendance a = attendanceRepository.getReferenceById(d.getId());
                    a.setStatus(d.getStatus());
                    return a;
                }).toList();
        attendanceRepository.saveAll(toUpdate);
    }
}
