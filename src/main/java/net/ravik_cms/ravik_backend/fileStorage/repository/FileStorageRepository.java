package net.ravik_cms.ravik_backend.fileStorage.repository;

import net.ravik_cms.ravik_backend.common.enums.DeletionStatus;
import net.ravik_cms.ravik_backend.fileStorage.entity.FileStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileStorageRepository extends JpaRepository<FileStorage, UUID> {
    List<FileStorage> findByProjectIdAndDeletionStatusOrderByUploadedAt(UUID projectId, DeletionStatus status);
    List<FileStorage> findByMilestoneIdAndDeletionStatusOrderByUploadedAt(UUID milestoneId, DeletionStatus status);
    Optional<FileStorage> findByIdAndDeletionStatus(UUID id, DeletionStatus status);
}
