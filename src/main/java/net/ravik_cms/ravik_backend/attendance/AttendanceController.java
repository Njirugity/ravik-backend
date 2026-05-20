package net.ravik_cms.ravik_backend.attendance;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.AttendanceStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/attendances")
public class AttendanceController {
    private final AttendanceService attendanceService;

    @PostMapping("/{project_id}/{date}")
    public ResponseEntity<AttendanceResultDto> generate(@PathVariable UUID project_id, @PathVariable LocalDate date){
        AttendanceResultDto body = attendanceService.generateSingleDate(project_id, date);
        return ResponseEntity.ok(body);
    }
    @GetMapping("/{project_id}")
    public ResponseEntity<List<AttendanceSingleDayInfoDto> >display(@PathVariable UUID project_id){
        List<AttendanceSingleDayInfoDto> body = attendanceService.displayRecords(project_id);
        return ResponseEntity.ok(body);
    }
    @GetMapping("/summary/{project_id}")
    public ResponseEntity<List<AttendanceSummaryDto>> summary(@PathVariable UUID project_id){
        List<AttendanceSummaryDto> body = attendanceService.summary(project_id);
        return ResponseEntity.ok(body);
    }
    @PostMapping
    public ResponseEntity<?> saveAttendance(@RequestBody List<AttendanceDayDto> dtos){
        attendanceService.updateBulkAttendance(dtos);
        return ResponseEntity.ok().build();
    }
}
