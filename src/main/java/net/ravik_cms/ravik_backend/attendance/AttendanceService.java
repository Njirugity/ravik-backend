package net.ravik_cms.ravik_backend.attendance;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.calendar.Calendar;
import net.ravik_cms.ravik_backend.calendar.CalendarService;
import net.ravik_cms.ravik_backend.common.enums.AttendanceStatus;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.memberships.ProjectMembership;
import net.ravik_cms.ravik_backend.memberships.ProjectMembershipRepository;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;
import net.ravik_cms.ravik_backend.milestones.repository.MilestonesRepository;
import net.ravik_cms.ravik_backend.milestones.service.MilestonesService;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    public Page<AttendanceSingleDayInfoDto> displayRecords(UUID projectId, LocalDate date, String jobTitle,
                                                           AttendanceStatus status, String search,
                                                           Pageable pageable){
        if(date == null){
            date = LocalDate.now();
        }

        Page<Attendance> records = attendanceRepository.findAttendanceRecords(projectId, date, jobTitle,
                status, search, pageable);
        return records.map(a->{
                    AttendanceSingleDayInfoDto dto = new AttendanceSingleDayInfoDto();
                    dto.setMemberId(a.getMembership().getId());
                    dto.setUserName(a.getMembership().getUser().getUserName());
                    dto.setJobTitle(a.getMembership().getJobTitle().getTitle());
                    dto.setAttendanceId(a.getId());
                    dto.setDate(a.getDate());
                    dto.setStatus(a.getStatus());
                    dto.setMilestoneTitle(a.getMilestone()!= null ? a.getMilestone().getTitle() : null);
                    return dto;
                });
    }
    public Page<AttendanceSingleDayInfoDto> getMemberAttendanceHistory(Long memberId, LocalDate start, LocalDate end,
                                                                       Pageable pageable){
        Page<Attendance> records = attendanceRepository.findAttendanceByMembership(memberId, start, end, pageable);
        return records.map(a->{
                    AttendanceSingleDayInfoDto dto = new AttendanceSingleDayInfoDto();
                    dto.setMemberId(a.getMembership().getId());
                    dto.setUserName(a.getMembership().getUser().getUserName());
                    dto.setJobTitle(a.getMembership().getJobTitle() != null ? a.getMembership().getJobTitle().getTitle() : null);
                    dto.setAttendanceId(a.getId());
                    dto.setDate(a.getDate());
                    dto.setStatus(a.getStatus());
                    dto.setMilestoneTitle(a.getMilestone()!= null ? a.getMilestone().getTitle() : null);
                    return dto;
                });
    }

    public Page<AttendanceSelectionDto> displayRecordsForMarking(UUID projectId, LocalDate date, String jobTitle,
                                                                 String search, Pageable pageable){
        if(date == null){
            date=LocalDate.now();
        }
        Page<ProjectMembership> memberships = attendanceRepository.findMembershipsWithoutAttendance(
                projectId, date, jobTitle, search, pageable);

        LocalDate finalDate = date;
        return memberships.map(m->{
                    AttendanceSelectionDto dto = new AttendanceSelectionDto();
                    dto.setMemberId(m.getId());
                    dto.setUserName(m.getUser().getUserName());
                    dto.setJobTitle(m.getJobTitle().getTitle());
                    dto.setDate(finalDate);
                    dto.setMilestoneId(null);
                    dto.setStatus(null);
                    return dto;
                });
    }
    public Page<AttendanceSummaryDto> summary (UUID projectId, LocalDate date, String jobTitle,
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
                jobTitle, search, pageable);
        String weekRange = getWorkingDaysString(firstDay, lastDay, calendar);
        long finalDaysOfWeek = daysOfWeek;
        return page.map(p->{
            AttendanceSummaryDto dto = new AttendanceSummaryDto();
            dto.setUserName(p.userName());
            dto.setJobTitle(p.jobTitle());
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
