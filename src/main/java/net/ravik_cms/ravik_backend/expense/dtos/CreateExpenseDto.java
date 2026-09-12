package net.ravik_cms.ravik_backend.expense.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateExpenseDto {
    private String title;
    private String description;
    private Double amount;
    private LocalDate date;
    private Long categoryId;
}
