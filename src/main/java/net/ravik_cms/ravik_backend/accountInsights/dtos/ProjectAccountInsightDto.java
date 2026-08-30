package net.ravik_cms.ravik_backend.accountInsights.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectAccountInsightDto {
    private UUID accountId;
    private String name;
    private String type;
    private Double incomeThisProject;
    private Double paymentsThisProject;
}
