package net.ravik_cms.ravik_backend.income.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateIncomeDto {
    private String source;
    private String description;
    private Double amount;
    private LocalDate date;
    private UUID accountId;
}
