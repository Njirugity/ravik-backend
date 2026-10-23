package net.ravik_cms.ravik_backend.locations.service;

import net.ravik_cms.ravik_backend.locations.dto.LocationOptionDto;
import net.ravik_cms.ravik_backend.locations.entity.Ward;

import java.util.List;

public interface LocationService {
    List<LocationOptionDto> getCountries();
    List<LocationOptionDto> getCounties(Long countryId);
    List<LocationOptionDto> getSubCounties(Long countyId);
    List<LocationOptionDto> getWards(Long subCountyId);
    Ward getWard(Long wardId);
}
