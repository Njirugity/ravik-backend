package net.ravik_cms.ravik_backend.attendance;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.calendar.Calendar;
import net.ravik_cms.ravik_backend.calendar.CalendarService;
import net.ravik_cms.ravik_backend.common.enums.AttendanceStatus;
import net.ravik_cms.ravik_backend.common.enums.StaffStatus;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.memberships.ProjectMembership;
import net.ravik_cms.ravik_backend.memberships.ProjectMembershipRepository;
import net.ravik_cms.ravik_backend.milestones.Milestones;
import net.ravik_cms.ravik_backend.milestones.MilestonesMapper;
import net.ravik_cms.ravik_backend.milestones.MilestonesRepository;
import net.ravik_cms.ravik_backend.milestones.MilestonesService;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import net.ravik_cms.ravik_backend.wages.CreateWageRecordDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private final ProjectMembershipRepository membershipRepository;
    private final MilestonesRepository milestonesRepository;
    private final MilestonesService milestonesService;
    private final CalendarService calenderService;

    public Page<AttendanceSingleDayInfoDto> displayRecords(UUID projectId, LocalDate date, String role,
                                                           AttendanceStatus status, String search,
                                                           Pageable pageable){
        if(date == null){
            date = LocalDate.now();
        }

        Page<Attendance> records = attendanceRepository.findAttendanceRecords(projectId, date, role,
                status, search, pageable);
        return records.map(a->{
                    AttendanceSingleDayInfoDto dto = new AttendanceSingleDayInfoDto();
                    dto.setMemberId(a.getMembership().getId());
                    dto.setUserName(a.getMembership().getUser().getUserName());
                    dto.setRole(a.getMembership().getRole().getName());
                    dto.setAttendanceId(a.getId());
                    dto.setDate(a.getDate());
                    dto.setStatus(a.getStatus());
                    dto.setMilestoneTitle(a.getMilestone()!= null ? a.getMilestone().getTitle() : null);
                    return dto;
                });
    }
    public Page<AttendanceSelectionDto> displayRecordsForMarking(UUID projectId, LocalDate date, String role,
                                                                 String search, Pageable pageable){
        if(date == null){
            date=LocalDate.now();
        }
        Page<ProjectMembership> memberships = attendanceRepository.findMembershipsWithoutAttendance(
                projectId, date, role, search, pageable);

        LocalDate finalDate = date;
        return memberships.map(m->{
                    AttendanceSelectionDto dto = new AttendanceSelectionDto();
                    dto.setMemberId(m.getId());
                    dto.setUserName(m.getUser().getUserName());
                    dto.setRole(m.getRole().getName());
                    dto.setDate(finalDate);
                    dto.setMilestoneId(null);
                    dto.setStatus(null);
                    return dto;
                });
    }
    public Page<AttendanceSummaryDto> summary (UUID projectId, LocalDate date, String role,
                                               String search, Pageable pageable){
        LocalDate workingDate =  date == null ? LocalDate.now() : date;
        Calendar calendar = calenderService.getCalenderEntity(projectId);

        LocalDate firstDay = workingDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate lastDay = workingDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        long daysOfWeek = 0;
        LocalDate current = firstDay;

        while(!current.isAfter(lastDay)){
            if(calenderService.isWorkingDay(calendar, current)){
                daysOfWeek++;
            }
            current = current.plusDays(1);
        }

        Page<AttendanceSummaryProjection> page = attendanceRepository.findAttendanceForSummary(projectId, firstDay, lastDay,
                role, search, pageable);
        String weekRange = getWorkingDaysString(firstDay, lastDay, calendar);
        long finalDaysOfWeek = daysOfWeek;
        return page.map(p->{
            AttendanceSummaryDto dto = new AttendanceSummaryDto();
            dto.setUserName(p.userName());
            dto.setRole(p.role());
            dto.setDaysPresent(p.daysPresent());
            dto.setDaysOfWeek(finalDaysOfWeek);
            dto.setWeekOf(weekRange);
            return dto;
        });
    }

    @Transactional
    public void createAttendanceRecord(List<CreateAttendanceRecordDto> request){
        List<Long> memberIds = request.stream()
                .map(CreateAttendanceRecordDto::getMemberId).distinct().toList();
        Map<Long, ProjectMembership> membershipMap = membershipRepository.findAllById(memberIds).stream()
                .collect(Collectors.toMap(ProjectMembership::getId, m->m));
        List<UUID> milestoneIds  = request.stream()
                .map(CreateAttendanceRecordDto::getMilestoneId).distinct().toList();
        Map<UUID, Milestones> milestoneMap = milestonesRepository.findAllById(milestoneIds).stream()
                .collect(Collectors.toMap(Milestones::getId, m->m));
        List<Attendance> records = request.stream()
                .map(r->{
                    Attendance a = new Attendance();
                    a.setDate(r.getDate());
                    a.setStatus(r.getStatus());
                    ProjectMembership membership = membershipMap.get(r.getMemberId());
                    a.setMembership(membership);
                    Milestones milestone = milestoneMap.get(r.getMilestoneId());
                    a.setMilestone(milestone);
                    milestonesService.setMilestoneActualStartDate(milestone);
                    return a;
                }).collect(Collectors.toList());
        attendanceRepository.saveAll(records);
    }
    private String getWorkingDaysString(LocalDate firstDay, LocalDate lastDay, Calendar calendar){
        LocalDate firstWorkingDay = firstDay;
        while (!calenderService.isWorkingDay(calendar, firstWorkingDay)) {
            firstWorkingDay = firstWorkingDay.plusDays(1);
        }

        LocalDate lastWorkingDay = lastDay;
        while (!calenderService.isWorkingDay(calendar, lastWorkingDay)) {
            lastWorkingDay = lastWorkingDay.minusDays(1);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM");

        return firstWorkingDay.format(formatter) + " - " + lastWorkingDay.format(formatter);
    }
    @Transactional
    public void updateAttendance(UpdateAttendanceDto request){
        Attendance a = attendanceRepository.findById(request.getId())
                .orElseThrow(()-> new ResourceNotFoundException("Attendance record not found"));
        if (request.getStatus() != null) {
            a.setStatus(request.getStatus());
        }

        if (request.getMilestoneId() != null) {
            Milestones milestone = milestonesRepository.findById(request.getMilestoneId())
                    .orElseThrow(() -> new ResourceNotFoundException("Milestone record not found"));

            a.setMilestone(milestone);
        }
    }

    @Transactional
    public void deleteAttendance(Long id){
        Attendance a = attendanceRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Attendance record not found"));
        attendanceRepository.delete(a);
    }
}
