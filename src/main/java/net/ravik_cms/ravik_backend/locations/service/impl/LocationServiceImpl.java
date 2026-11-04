package net.ravik_cms.ravik_backend.locations.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.locations.dto.LocationOptionDto;
import net.ravik_cms.ravik_backend.locations.entity.Ward;
import net.ravik_cms.ravik_backend.locations.mapper.LocationMapper;
import net.ravik_cms.ravik_backend.locations.repository.CountryRepository;
import net.ravik_cms.ravik_backend.locations.repository.CountyRepository;
import net.ravik_cms.ravik_backend.locations.repository.SubCountyRepository;
import net.ravik_cms.ravik_backend.locations.repository.WardRepository;
import net.ravik_cms.ravik_backend.locations.service.LocationService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Default {@link LocationService} implementation.
 * <p>
 * Serves the seeded Country &gt; County &gt; SubCounty &gt; Ward hierarchy used to
 * populate cascading location dropdowns. These are global reference data, so no
 * project-scoped permission checks are applied.
 */
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final CountryRepository countryRepository;
    private final CountyRepository countyRepository;
    private final SubCountyRepository subCountyRepository;
    private final WardRepository wardRepository;
    private final LocationMapper locationMapper;

    @Override
    @Transactional
    public List<LocationOptionDto> getCountries() {
        return locationMapper.toCountryOptions(countryRepository.findAllByOrderByNameAsc());
    }

    @Override
    @Transactional
    public List<LocationOptionDto> getCounties(Long countryId) {
        return locationMapper.toCountyOptions(countyRepository.findAllByCountryIdOrderByNameAsc(countryId));
    }

    @Override
    @Transactional
    public List<LocationOptionDto> getSubCounties(Long countyId) {
        return locationMapper.toSubCountyOptions(subCountyRepository.findAllByCountyIdOrderByNameAsc(countyId));
    }

    @Override
    @Transactional
    public List<LocationOptionDto> getWards(Long subCountyId) {
        return locationMapper.toWardOptions(wardRepository.findAllBySubCountyIdOrderByNameAsc(subCountyId));
    }

    /**
     * Resolves a ward for assignment to another entity (e.g. a project).
     *
     * @param wardId id of the ward
     * @return the ward
     * @throws ResourceNotFoundException if no ward exists with the given id
     */
    @Override
    public Ward getWard(Long wardId) {
        return wardRepository.findById(wardId)
                .orElseThrow(() -> new ResourceNotFoundException("Ward not found"));
    }
}
