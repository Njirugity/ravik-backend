package net.ravik_cms.ravik_backend.fileStorage.service;

import net.ravik_cms.ravik_backend.fileStorage.dtos.FileStorageResponseDto;
import net.ravik_cms.ravik_backend.fileStorage.entity.FileStorage;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface FileMetadataPersistenceService {
    List<FileStorageResponseDto> uploadToMilestone(UUID milestoneId, List<MultipartFile> file);
    List<FileStorageResponseDto> uploadToProject(UUID projectId, List<MultipartFile> file);
    List<FileStorageResponseDto> listProjectFiles(UUID projectId);
    List<FileStorageResponseDto> listMilestoneFiles(UUID milestoneId);
    FileStorage getFile(UUID fileId);
    Resource load(FileStorage file);
    void delete(UUID fileId);
}
