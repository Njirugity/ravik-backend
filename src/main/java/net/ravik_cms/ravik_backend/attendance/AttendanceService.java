package net.ravik_cms.ravik_backend.attendance;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.memberships.ProjectMembership;
import net.ravik_cms.ravik_backend.memberships.ProjectMembershipRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final AttendanceMapper attendanceMapper;
    private final ProjectMembershipRepository membershipRepository;


    public AttendanceResultDto generateDates(UUID projectId, GenerateDatesDto dates){
        List<ProjectMembership> memberships = membershipRepository.findAllByProjectIdAndStatus(projectId, "ACTIVE");
        List<LocalDate> attendanceDates = new ArrayList<LocalDate>();
        for(long i = 0L; i < dates.getRange(); i++){
            attendanceDates.add(dates.getStartDate().plusDays(i));
        }
        List<Attendance> attendances = new ArrayList<>();
        int skipped = 0;
        int generated = 0;
        for (ProjectMembership membership : memberships) {
            for(LocalDate attendanceDate : attendanceDates){
                boolean exists = attendanceRepository.existsByMembershipAndDate(membership, attendanceDate);
                if (exists){
                    skipped++;
                } else{
                    Attendance attendance = new Attendance();
                    attendance.setMembership(membership);
                    attendance.setDate(attendanceDate);
                    attendance.setPresent(false);
                    attendances.add(attendance);
                    generated++;
                }
            }
        }
        attendanceRepository.saveAll(attendances);
        return new AttendanceResultDto(skipped, generated);
    }

    @Transactional
    public List<AttendanceInfoDto> displayAttendanceRecords(UUID projectId, LocalDate startDate, LocalDate endDate){
        List<ProjectMembership> memberships = membershipRepository.findAllByProjectIdAndStatus(projectId, "ACTIVE");
        List<Attendance> attendanceList = attendanceRepository.findByMembershipInAndDateBetween(memberships, startDate, endDate);

        Map<ProjectMembership, List<Attendance>> grouped=
                attendanceList.stream()
                        .collect(Collectors.groupingBy(Attendance::getMembership));
        List<AttendanceInfoDto> rows = new ArrayList<>();
        for (ProjectMembership membership : memberships) {
            List<Attendance> records =
                    grouped.getOrDefault(membership, List.of());
            List<AttendanceDayDto> days = records.stream()
                    .sorted(Comparator.comparing(Attendance::getDate))
                    .map(a ->{
                        AttendanceDayDto dto = new AttendanceDayDto();
                        dto.setDate(a.getDate());
                        dto.setPresent(a.isPresent());
                        return dto;
                    })
                    .toList();
            AttendanceInfoDto row = new AttendanceInfoDto();
            row.setDays(days);
            row.setUserName(membership.getUser().getUserName());
            row.setRole(membership.getRole().getName());
            row.setMemberId(membership.getId());
            rows.add(row);
        }
        return rows;
    }
    @Transactional
    public AttendanceDayDto updateAttendance(Long attendanceId, Long membershipId, UpdateAttendanceDto request){
        ProjectMembership member = membershipRepository.findById(membershipId)
                .orElseThrow(()-> new ResourceNotFoundException("Member not found"));
        Attendance attendance = attendanceRepository.findByMembershipAndId(member, attendanceId)
                .orElseThrow(()-> new ResourceNotFoundException("Attendance not found"));
        if(attendance.isLocked()){
            throw new IllegalStateException(
                    "Attendance is locked and cannot be updated!"
            );
        }
        attendance.setPresent(request.isPresent());
        attendanceRepository.save(attendance);
        return attendanceMapper.toAttendanceDayDto(attendance);
    }
}
