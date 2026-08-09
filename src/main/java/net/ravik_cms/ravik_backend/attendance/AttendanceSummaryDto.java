package net.ravik_cms.ravik_backend.attendance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceSummaryDto {
    private String userName;
    private String jobTitle;
    private Long daysPresent;
    private Long daysOfWeek;
    private String weekOf;
}
