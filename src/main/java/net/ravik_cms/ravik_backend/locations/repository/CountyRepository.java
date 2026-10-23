package net.ravik_cms.ravik_backend.locations.repository;

import net.ravik_cms.ravik_backend.locations.entity.County;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountyRepository extends JpaRepository<County, Long> {
    List<County> findAllByCountryIdOrderByNameAsc(Long countryId);
}
