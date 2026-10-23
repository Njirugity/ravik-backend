package net.ravik_cms.ravik_backend.locations.repository;

import net.ravik_cms.ravik_backend.locations.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByCode(String code);
    List<Country> findAllByOrderByNameAsc();
}
