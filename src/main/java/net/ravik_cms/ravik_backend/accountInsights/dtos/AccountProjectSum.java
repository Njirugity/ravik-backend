package net.ravik_cms.ravik_backend.accountInsights.dtos;

import java.util.UUID;

/** One project's total of income or payments on an account, from a grouped query. */
public record AccountProjectSum(UUID projectId, String projectTitle, Double amount) {
}
