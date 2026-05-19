package net.ravik_cms.ravik_backend.attendance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.enums.AttendanceStatus;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceSingleDayInfoDto {
    private Long memberId;
    private String userName;
    private String role;
    private Long attendanceId;
    private LocalDate date;
    private AttendanceStatus status;
    private String milestoneTitle;
}
