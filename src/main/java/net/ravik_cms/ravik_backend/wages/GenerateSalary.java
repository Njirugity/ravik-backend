package net.ravik_cms.ravik_backend.wages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class GenerateSalary {
    private LocalDate startDate;
    private LocalDate endDate;

}
