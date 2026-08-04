package net.ravik_cms.ravik_backend.calendar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalendarInfoDto {
    private UUID id;
    private String name;
    private boolean monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    private Set<LocalDate> holidays =  new HashSet<>();
}
