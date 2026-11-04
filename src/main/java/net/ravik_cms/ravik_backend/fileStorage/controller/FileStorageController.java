package net.ravik_cms.ravik_backend.fileStorage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.response.ApiMessageResponse;
import net.ravik_cms.ravik_backend.fileStorage.dtos.FileStorageResponseDto;
import net.ravik_cms.ravik_backend.fileStorage.entity.FileStorage;
import net.ravik_cms.ravik_backend.fileStorage.service.FileMetadataPersistenceService;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "Files storage", description = "Upload, download and storage of files")
public class FileStorageController {
    private final FileMetadataPersistenceService metadataPersistenceService;

    @PostMapping(value = "/projects/{projectId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Uploads project scoped files")
    public ResponseEntity<List<FileStorageResponseDto>> uploadFileToProject(
            @PathVariable UUID projectId,
            @RequestParam("files")List<MultipartFile> file
            ){
        List<FileStorageResponseDto> doc = metadataPersistenceService.uploadToProject(projectId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(doc);
    }
    @PostMapping(value = "/milestones/{milestoneId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Uploads milestone scoped files")
    public ResponseEntity<List<FileStorageResponseDto>> uploadFileToMilestone(
            @PathVariable UUID milestoneId,
            @RequestParam("files")List<MultipartFile> file
    ){
        List<FileStorageResponseDto> doc = metadataPersistenceService.uploadToMilestone(milestoneId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(doc);
    }
    @GetMapping("/projects/{projectId}")
    @Operation(summary = "List a project's files, including milestones scoped")
    public ResponseEntity<List<FileStorageResponseDto>> listProjectFiles(@PathVariable UUID projectId){
        List<FileStorageResponseDto> docs = metadataPersistenceService.listProjectFiles(projectId);
        return ResponseEntity.ok(docs);
    }
    @GetMapping("/milestones/{milestoneId}")
    @Operation(summary = "List milestone scoped projects")
    public ResponseEntity<List<FileStorageResponseDto>> listMilestoneFiles(@PathVariable UUID milestoneId){
        List<FileStorageResponseDto> docs = metadataPersistenceService.listMilestoneFiles(milestoneId);
        return ResponseEntity.ok(docs);
    }
    @GetMapping("/{fileId}")
    @Operation(summary = "Load a selected file", description = "The inline parameter defaults to false; files are downloaded and not previewed on the browser")
    public ResponseEntity<Resource> loadFile(
            @PathVariable UUID fileId,
            @RequestParam(defaultValue = "false") boolean inline
    ){
        FileStorage file = metadataPersistenceService.getFile(fileId);
        Resource resource = metadataPersistenceService.load(file);

        ContentDisposition disposition = (inline
        ? ContentDisposition.inline(): ContentDisposition.attachment())
                .filename(file.getFileName(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok().contentType(resolveMediaType(file.getContentType())).contentLength(file.getSize()).
                header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString()).
                header("X-Content-Type-Options", "nosniff")
                .body(resource);
    }

    @DeleteMapping("/{fileId}")
    @Operation(summary = "Delete a file")
    public ResponseEntity<ApiMessageResponse> deleteFile(@PathVariable UUID fileId){
        metadataPersistenceService.delete(fileId);
        return ResponseEntity.ok(ApiMessageResponse.of("File deleted successfully", HttpStatus.OK.value()));
    }
    private MediaType resolveMediaType(String contentType) {
        try {
            return MediaType.parseMediaType(contentType);
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
