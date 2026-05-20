package net.ravik_cms.ravik_backend.attendance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceInfoDto {
    private Long memberId;
    private String userName;
    private String role;
    private List<AttendanceDayDto> days;
}
