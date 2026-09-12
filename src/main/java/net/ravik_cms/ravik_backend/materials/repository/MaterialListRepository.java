package net.ravik_cms.ravik_backend.materials.repository;

import net.ravik_cms.ravik_backend.common.enums.DeletionStatus;
import net.ravik_cms.ravik_backend.materials.entity.MaterialList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaterialListRepository extends JpaRepository<MaterialList, UUID> {
    Optional<MaterialList> findByIdAndProjectIdAndStatus(UUID id, UUID projectId, DeletionStatus status);
    List<MaterialList> findAllByProjectIdAndStatus(UUID projectId, DeletionStatus status);
}
