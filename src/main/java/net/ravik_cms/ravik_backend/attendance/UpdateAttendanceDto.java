package net.ravik_cms.ravik_backend.attendance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAttendanceDto {
    private Long memberId;
    private LocalDate date;
    private boolean present;
}
