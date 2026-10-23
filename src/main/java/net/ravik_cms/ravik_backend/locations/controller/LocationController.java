package net.ravik_cms.ravik_backend.locations.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.locations.dto.LocationOptionDto;
import net.ravik_cms.ravik_backend.locations.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/locations")
@Tag(name = "Locations", description = "Country, county, sub-county and ward lookups for location dropdowns")
public class LocationController {
    private final LocationService locationService;

    @GetMapping("/countries")
    @Operation(summary = "List all countries")
    public ResponseEntity<List<LocationOptionDto>> getCountries() {
        return ResponseEntity.ok(locationService.getCountries());
    }

    @GetMapping("/countries/{countryId}/counties")
    @Operation(summary = "List the counties in a country")
    public ResponseEntity<List<LocationOptionDto>> getCounties(@PathVariable Long countryId) {
        return ResponseEntity.ok(locationService.getCounties(countryId));
    }

    @GetMapping("/counties/{countyId}/sub-counties")
    @Operation(summary = "List the sub-counties in a county")
    public ResponseEntity<List<LocationOptionDto>> getSubCounties(@PathVariable Long countyId) {
        return ResponseEntity.ok(locationService.getSubCounties(countyId));
    }

    @GetMapping("/sub-counties/{subCountyId}/wards")
    @Operation(summary = "List the wards in a sub-county")
    public ResponseEntity<List<LocationOptionDto>> getWards(@PathVariable Long subCountyId) {
        return ResponseEntity.ok(locationService.getWards(subCountyId));
    }
}
