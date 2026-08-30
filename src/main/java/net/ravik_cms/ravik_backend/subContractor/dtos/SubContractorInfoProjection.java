package net.ravik_cms.ravik_backend.subContractor.dtos;

public record SubContractorInfoProjection(
        Long id,
        String title,
        String jobType,
        String email,
        String address,
        String phoneNumber) {
}
