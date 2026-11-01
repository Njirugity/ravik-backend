package net.ravik_cms.ravik_backend.common.dtos;

/**
 * JPQL constructor-expression projection: an amount summed per calendar month.
 */
public record MonthAmountProjection(Integer year, Integer month, Double total) {
}
