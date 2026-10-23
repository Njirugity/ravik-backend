package net.ravik_cms.ravik_backend.locations.dto;

/**
 * The full location trace of a project, resolved upwards from its ward.
 */
public record ProjectLocationDto(
        Long wardId,
        String ward,
        Long subCountyId,
        String subCounty,
        Long countyId,
        String county,
        Long countryId,
        String country
) {
}
