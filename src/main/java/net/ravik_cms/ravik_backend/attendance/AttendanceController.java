package net.ravik_cms.ravik_backend.attendance;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.AttendanceStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;
    @PostMapping("/generate/{project_id}/{date}")
    public AttendanceResultDto generate(@PathVariable UUID project_id, @PathVariable LocalDate date){
        return attendanceService.generateSingleDate(project_id, date);
    }
    @GetMapping("/punch-in/{project_id}")
    public List<AttendanceSingleDayInfoDto> display(@PathVariable UUID project_id){
        return attendanceService.displayRecords(project_id);
    }
    @GetMapping("/summary/{project_id}")
    public List<AttendanceSummaryDto> summary(@PathVariable UUID project_id){
        return attendanceService.summary(project_id);
    }
    @PostMapping("/save")
    public void saveAttendance(@RequestBody List<AttendanceDayDto> dtos){
        attendanceService.updateBulkAttendance(dtos);
    }
}
