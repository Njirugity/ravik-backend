package net.ravik_cms.ravik_backend.attendance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.AttendanceStatus;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAttendanceRecordDto {
    private LocalDate date;
    private AttendanceStatus status;
    private Long memberId;
    private UUID milestoneId;
}
