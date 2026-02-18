package net.ravik_cms.ravik_backend.attendance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceDayDto {
    private LocalDate date;
    private boolean present;
}
