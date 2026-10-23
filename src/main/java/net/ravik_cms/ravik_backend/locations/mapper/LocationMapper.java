package net.ravik_cms.ravik_backend.locations.mapper;

import net.ravik_cms.ravik_backend.locations.dto.LocationOptionDto;
import net.ravik_cms.ravik_backend.locations.dto.ProjectLocationDto;
import net.ravik_cms.ravik_backend.locations.entity.Country;
import net.ravik_cms.ravik_backend.locations.entity.County;
import net.ravik_cms.ravik_backend.locations.entity.SubCounty;
import net.ravik_cms.ravik_backend.locations.entity.Ward;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationOptionDto toOption(Country country);
    LocationOptionDto toOption(County county);
    LocationOptionDto toOption(SubCounty subCounty);
    LocationOptionDto toOption(Ward ward);
    List<LocationOptionDto> toCountryOptions(List<Country> countries);
    List<LocationOptionDto> toCountyOptions(List<County> counties);
    List<LocationOptionDto> toSubCountyOptions(List<SubCounty> subCounties);
    List<LocationOptionDto> toWardOptions(List<Ward> wards);

    @Mapping(source = "id", target = "wardId")
    @Mapping(source = "name", target = "ward")
    @Mapping(source = "subCounty.id", target = "subCountyId")
    @Mapping(source = "subCounty.name", target = "subCounty")
    @Mapping(source = "subCounty.county.id", target = "countyId")
    @Mapping(source = "subCounty.county.name", target = "county")
    @Mapping(source = "subCounty.county.country.id", target = "countryId")
    @Mapping(source = "subCounty.county.country.name", target = "country")
    ProjectLocationDto toProjectLocation(Ward ward);
}
