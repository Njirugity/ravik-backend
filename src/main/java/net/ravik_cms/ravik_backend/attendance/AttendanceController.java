package net.ravik_cms.ravik_backend.attendance;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;
    @PostMapping("/generate/{project_id}")
    public AttendanceResultDto generate(@PathVariable UUID project_id, @RequestBody GenerateDatesDto dates){
        return attendanceService.generateDates(project_id, dates);
    }
    @GetMapping("/records/{project_id}")
    public List<AttendanceInfoDto> display(@PathVariable UUID project_id,
                                           @RequestParam LocalDate startDate, @RequestParam LocalDate endDate){
        return attendanceService.displayAttendanceRecords(project_id, startDate, endDate);
    }
    @PatchMapping("/{member_id}/{attendance_id}")
    public AttendanceDayDto updateAttendance(@PathVariable Long member_id,
                                 @PathVariable Long attendance_id, @RequestBody UpdateAttendanceDto request ){
        return attendanceService.updateAttendance(attendance_id, member_id, request);
    }
}
