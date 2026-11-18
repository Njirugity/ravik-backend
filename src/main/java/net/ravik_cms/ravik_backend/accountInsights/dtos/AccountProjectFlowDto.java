package net.ravik_cms.ravik_backend.accountInsights.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountProjectFlowDto {
    private UUID projectId;
    private String projectTitle;
    private Double income;
    private Double payments;
}
