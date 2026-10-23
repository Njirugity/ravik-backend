package net.ravik_cms.ravik_backend.locations.dto;

/**
 * A single selectable item in a location dropdown (country, county, sub-county or ward).
 */
public record LocationOptionDto(
        Long id,
        String code,
        String name
) {
}
