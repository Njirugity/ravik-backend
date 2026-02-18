package net.ravik_cms.ravik_backend.wages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WageInfoDto {
    private Long membershipId;
    private String userName;
    private String role;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalAmount;
    private Double baseWage;
    private int workingDays;
}
