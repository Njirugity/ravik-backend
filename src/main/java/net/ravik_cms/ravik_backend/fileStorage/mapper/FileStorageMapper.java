package net.ravik_cms.ravik_backend.fileStorage.mapper;

import net.ravik_cms.ravik_backend.fileStorage.dtos.FileStorageResponseDto;
import net.ravik_cms.ravik_backend.fileStorage.entity.FileStorage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FileStorageMapper {
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "milestone.id", target = "milestoneId")
    @Mapping(source = "size", target = "fileSize")
    FileStorageResponseDto toResponse(FileStorage file);
}
