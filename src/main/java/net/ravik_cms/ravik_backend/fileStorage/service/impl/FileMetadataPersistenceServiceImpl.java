package net.ravik_cms.ravik_backend.fileStorage.service.impl;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.AuthorizationService;
import net.ravik_cms.ravik_backend.common.enums.DeletionStatus;
import net.ravik_cms.ravik_backend.common.exception.EmptyResourceException;
import net.ravik_cms.ravik_backend.common.exception.InvalidFileStructureException;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.fileStorage.dtos.FileStorageResponseDto;
import net.ravik_cms.ravik_backend.fileStorage.entity.FileStorage;
import net.ravik_cms.ravik_backend.fileStorage.mapper.FileStorageMapper;
import net.ravik_cms.ravik_backend.fileStorage.repository.FileStorageRepository;
import net.ravik_cms.ravik_backend.fileStorage.service.FileMetadataPersistenceService;
import net.ravik_cms.ravik_backend.fileStorage.service.FileStorageService;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;
import net.ravik_cms.ravik_backend.milestones.repository.MilestonesRepository;
import net.ravik_cms.ravik_backend.projects.entity.Projects;
import net.ravik_cms.ravik_backend.projects.repository.ProjectsRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileMetadataPersistenceServiceImpl implements FileMetadataPersistenceService {
    private final FileStorageService storageService;
    private final FileStorageMapper fileStorageMapper;
    private final FileStorageRepository fileStorageRepository;
    private final MilestonesRepository milestonesRepository;
    private final ProjectsRepository projectsRepository;
    private final AuthorizationService authorizationService;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png",          // Images
            "pdf", "doc", "docx", "txt",   // Documents
            "xls", "xlsx",                 // Spreadsheets
            "csv"                          // CSV
    );

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg", "image/png",
            "application/pdf", "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/csv", "application/csv", "text/x-csv"
    );

    @Override
    @Transactional
    public List<FileStorageResponseDto> uploadToMilestone(UUID milestoneId, List<MultipartFile> file){
        authorizationService.authorize("UPLOAD_FILE");
        Milestones milestone = milestonesRepository.findById(milestoneId).
                orElseThrow(()-> new ResourceNotFoundException("Milestone not found"));
        Projects project = milestone.getProject();
        List<FileStorageResponseDto> docs = new ArrayList<>();
        List<FileStorage> filesToSave = new ArrayList<>();
        String keyPrefix = "project/" + project.getId() + "/milestone/" + milestoneId;
        for(MultipartFile f : file){
          validate(f);
          FileStorage doc = newDocument(f);
          doc.setProject(project);
          doc.setMilestone(milestone);

          filesToSave.add(doc);
          docs.add(save(doc, keyPrefix, f));
        }

        fileStorageRepository.saveAll(filesToSave);
        return  docs;
    }


    @Override
    @Transactional
    public List<FileStorageResponseDto> uploadToProject(UUID projectId, List<MultipartFile> file){
        authorizationService.authorize("UPLOAD_FILE");
        Projects project = projectsRepository.findById(projectId).
                orElseThrow(()-> new ResourceNotFoundException("Project not found"));
        List<FileStorageResponseDto> docs = new ArrayList<>();
        List<FileStorage> filesToSave = new ArrayList<>();
        String keyPrefix = "project/" + projectId;
        for(MultipartFile f : file){
            validate(f);
            FileStorage doc = newDocument(f);
            doc.setProject(project);

            filesToSave.add(doc);
            docs.add(save(doc, keyPrefix, f));
        }
        fileStorageRepository.saveAll(filesToSave);
        return docs;
    }

    @Override
    public List<FileStorageResponseDto> listProjectFiles(UUID projectId){
        authorizationService.authorize("READ_FILE");
        return fileStorageRepository.findByProjectIdAndDeletionStatusOrderByUploadedAt(projectId,
                        DeletionStatus.NOT_DELETED).stream()
                .map(fileStorageMapper::toResponse).toList();
    }

    @Override
    public List<FileStorageResponseDto> listMilestoneFiles(UUID milestoneId){
        authorizationService.authorize("READ_FILE");
        return fileStorageRepository.findByMilestoneIdAndDeletionStatusOrderByUploadedAt(milestoneId,
                        DeletionStatus.NOT_DELETED).stream()
                .map(fileStorageMapper::toResponse).toList();
    }

    @Override
    public FileStorage getFile(UUID fileId){
        authorizationService.authorize("READ_FILE");
        return fileStorageRepository.findByIdAndDeletionStatus(fileId, DeletionStatus.NOT_DELETED).
                orElseThrow(()-> new ResourceNotFoundException("File not found"));
    }

    @Override
    public Resource load(FileStorage file){
        authorizationService.authorize("READ_FILE");
        return storageService.load(file.getStorageKey());
    }

    @Override
    public  void delete( UUID fileId){
        authorizationService.authorize("DELETE_FILE");
        FileStorage file = getFile(fileId);
        file.setDeletionStatus(DeletionStatus.DELETED);
        fileStorageRepository.save(file);
    }
    private FileStorage newDocument(MultipartFile file){
        FileStorage doc = new FileStorage();
        doc.setFileName(StringUtils.getFilename(StringUtils.cleanPath(file.getOriginalFilename())));
        doc.setContentType(file.getContentType());
        doc.setSize(file.getSize());
        doc.setUploadedAt(LocalDateTime.now());
        return doc;
    }
    private FileStorageResponseDto save(FileStorage doc, String keyPrefix, MultipartFile file){
        String storageKey = storageService.store(keyPrefix, file);
        doc.setStorageKey(storageKey);
        return fileStorageMapper.toResponse(fileStorageRepository.save(doc));
    }

    private void validate(MultipartFile file){
        if(file == null || file.isEmpty()){
            throw new EmptyResourceException("Files cannot be empty");
        }
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if(ext == null || !ALLOWED_EXTENSIONS.contains(ext.toLowerCase())){
            throw new InvalidFileStructureException("File type not allowed");
        }
        String contentType = file.getContentType();
        if(contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())){
            throw new InvalidFileStructureException("Content type not allowed");
        }
    }
}
