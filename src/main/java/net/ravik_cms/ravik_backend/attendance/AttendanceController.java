package net.ravik_cms.ravik_backend.attendance;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
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

    @GetMapping("/{project_id}")
    public ResponseEntity<Page<AttendanceSingleDayInfoDto>>display(
            @PathVariable UUID project_id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String jobTitle,
            @RequestParam(required = false) AttendanceStatus status,
            @RequestParam(required = false) String search,
            @PageableDefault(size =20) Pageable pageable){
        Page<AttendanceSingleDayInfoDto> body = attendanceService.displayRecords(project_id, date, jobTitle, status,
        search, pageable);
        return ResponseEntity.ok(body);
    }
    @GetMapping("/member/{member_id}")
    public ResponseEntity<Page<AttendanceSingleDayInfoDto>> memberHistory(
            @PathVariable Long member_id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @PageableDefault(size = 20) Pageable pageable){
        Page<AttendanceSingleDayInfoDto> body = attendanceService.getMemberAttendanceHistory(member_id, start, end, pageable);
        return ResponseEntity.ok(body);
    }
    @GetMapping("/mark/{project_id}")
    public ResponseEntity<Page<AttendanceSelectionDto>> displayForMarking(
            @PathVariable UUID project_id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String jobTitle,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable
    ){
        Page<AttendanceSelectionDto> body = attendanceService.displayRecordsForMarking(project_id, date, jobTitle,
                search, pageable);
        return ResponseEntity.ok(body);
    }
    @GetMapping("/summary/{project_id}")
    public ResponseEntity<Page<AttendanceSummaryDto>> summary(
            @PathVariable UUID project_id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String jobTitle,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable){
        Page<AttendanceSummaryDto> body = attendanceService.summary(project_id, date, jobTitle, search, pageable);
        return ResponseEntity.ok(body);
    }
    @PostMapping
    public ResponseEntity<?> saveAttendance(@RequestBody List<CreateAttendanceRecordDto> request){
        attendanceService.createAttendanceRecord(request);
        return ResponseEntity.ok().build();
    }
    @PutMapping
    public ResponseEntity<?> updateRecord(@RequestBody UpdateAttendanceDto request){
        attendanceService.updateAttendance(request);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{attendance_id}")
    public ResponseEntity<?> deleteRecord(@PathVariable Long attendance_id){
        attendanceService.deleteAttendance(attendance_id);
        return ResponseEntity.noContent().build();
    }
}
