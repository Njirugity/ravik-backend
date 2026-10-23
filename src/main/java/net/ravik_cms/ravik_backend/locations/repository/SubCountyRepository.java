package net.ravik_cms.ravik_backend.locations.repository;

import net.ravik_cms.ravik_backend.locations.entity.SubCounty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCountyRepository extends JpaRepository<SubCounty, Long> {
    List<SubCounty> findAllByCountyIdOrderByNameAsc(Long countyId);
}
