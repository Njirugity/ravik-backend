package net.ravik_cms.ravik_backend.locations.repository;

import net.ravik_cms.ravik_backend.locations.entity.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WardRepository extends JpaRepository<Ward, Long> {
    Optional<Ward> findByCode(String code);
    List<Ward> findAllBySubCountyIdOrderByNameAsc(Long subCountyId);
}
