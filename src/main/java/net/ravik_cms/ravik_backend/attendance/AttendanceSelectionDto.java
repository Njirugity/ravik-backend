package net.ravik_cms.ravik_backend.attendance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.AttendanceStatus;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSelectionDto {
    private Long memberId;
    private String userName;
    private String role;
    private LocalDate date;
    private AttendanceStatus status;
    private UUID milestoneId;
}
