package net.ravik_cms.ravik_backend.fileStorage.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String store(String keyPrefix, MultipartFile file);
    Resource load (String storageKey);
    void delete(String storageKey);
}
