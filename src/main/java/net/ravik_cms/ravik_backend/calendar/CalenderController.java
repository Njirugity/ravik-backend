package net.ravik_cms.ravik_backend.calendar;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import net.ravik_cms.ravik_backend.authorization.RequirePermission;

@RestController
@RequestMapping("/api/v1/calender")
@RequiredArgsConstructor
public class CalenderController {
    private final CalendarService calendarService;

    @RequirePermission("CREATE_SCHEDULE")
    @PostMapping("/project/{projectId}")
    public ResponseEntity<CalendarInfoDto> createCalender(@PathVariable UUID projectId,
                                                         @Valid @RequestBody CreateCalendarDto request) {
       CalendarInfoDto body =  calendarService.createCalender(projectId, request);
       return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
    @RequirePermission("READ_SCHEDULE")
    @GetMapping("/project/{projectId}")
    public ResponseEntity<CalendarInfoDto> getCalender(@PathVariable UUID projectId){
        CalendarInfoDto body = calendarService.getCalender(projectId);
        return ResponseEntity.ok(body);
    }
    @RequirePermission("UPDATE_SCHEDULE")
    @PutMapping("/{id}")
    public ResponseEntity<CalendarInfoDto> updateCalender(@PathVariable UUID id,
                                                          @Valid @RequestBody CalendarInfoDto request) {
        CalendarInfoDto body = calendarService.updateCalender(id, request);
        return ResponseEntity.ok(body);
    }
    @RequirePermission("DELETE_SCHEDULE")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCalender(@PathVariable UUID id){
        calendarService.deleteCalender(id);
        return ResponseEntity.noContent().build();
    }
}
