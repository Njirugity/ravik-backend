package net.ravik_cms.ravik_backend.wages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WageResultDto {
    private int generated;
    private double totalWages;
}
