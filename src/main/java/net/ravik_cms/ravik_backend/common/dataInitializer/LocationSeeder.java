package net.ravik_cms.ravik_backend.common.dataInitializer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ravik_cms.ravik_backend.locations.entity.Country;
import net.ravik_cms.ravik_backend.locations.entity.County;
import net.ravik_cms.ravik_backend.locations.entity.SubCounty;
import net.ravik_cms.ravik_backend.locations.entity.Ward;
import net.ravik_cms.ravik_backend.locations.repository.CountryRepository;
import net.ravik_cms.ravik_backend.locations.repository.CountyRepository;
import net.ravik_cms.ravik_backend.locations.repository.SubCountyRepository;
import net.ravik_cms.ravik_backend.locations.repository.WardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds the Kenyan administrative hierarchy (country, 47 counties, 290 sub-counties
 * i.e. IEBC constituencies, and 1,450 wards) from {@code locations/kenya.json}.
 * <p>
 * This is reference data, so it runs regardless of {@code app.seed.dev-data}, and
 * runs first so wards exist before any project is created. Skips if Kenya already exists.
 */
@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class LocationSeeder implements CommandLineRunner {
    private static final String DATA_FILE = "locations/kenya.json";

    private final CountryRepository countryRepository;
    private final CountyRepository countyRepository;
    private final SubCountyRepository subCountyRepository;
    private final WardRepository wardRepository;
    private final JsonMapper jsonMapper;

    private record WardData(String code, String name) {}
    private record SubCountyData(String code, String name, List<WardData> wards) {}
    private record CountyData(String code, String name, List<SubCountyData> subCounties) {}
    private record CountryData(String code, String name, List<CountyData> counties) {}

    @Override
    @Transactional
    public void run(String... args) throws IOException {
        CountryData data;
        try (InputStream in = new ClassPathResource(DATA_FILE).getInputStream()) {
            data = jsonMapper.readValue(in, CountryData.class);
        }
        if (countryRepository.findByCode(data.code()).isPresent()) {
            log.info("Locations already seeded for {}, skipping", data.name());
            return;
        }

        Country country = countryRepository.save(new Country(null, data.name(), data.code()));
        List<SubCounty> subCounties = new ArrayList<>();
        List<Ward> wards = new ArrayList<>();
        for (CountyData countyData : data.counties()) {
            County county = countyRepository.save(new County(null, countyData.name(), countyData.code(), country));
            for (SubCountyData subCountyData : countyData.subCounties()) {
                SubCounty subCounty = new SubCounty(null, subCountyData.name(), subCountyData.code(), county);
                subCounties.add(subCounty);
                for (WardData wardData : subCountyData.wards()) {
                    wards.add(new Ward(null, wardData.name(), wardData.code(), subCounty));
                }
            }
        }
        subCountyRepository.saveAll(subCounties);
        wardRepository.saveAll(wards);

        log.info("Seeded {}: {} counties, {} sub-counties, {} wards",
                data.name(), data.counties().size(), subCounties.size(), wards.size());
    }
}
