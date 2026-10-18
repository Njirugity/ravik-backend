package net.ravik_cms.ravik_backend.fileStorage.service.impl;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.fileStorage.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "app.storage.type", havingValue = "local")
public class LocalFileStorageServiceImpl implements FileStorageService {
    private final Path root;

    public LocalFileStorageServiceImpl(@Value("${app.storage.local-root}") String rootDir) throws IOException {
        this.root = Paths.get(rootDir).toAbsolutePath().normalize();
        Files.createDirectories(root);

    }
    @Override
    public String store(String keyPrefix, MultipartFile file){
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String key = keyPrefix + "/" + UUID.randomUUID() + (extension != null ? "." + extension.toLowerCase() : "");
        Path target = getSafePath(key);
        try {
            Files.createDirectories(target.getParent());
            try (InputStream in = file.getInputStream()){
                Files.copy(in,target, StandardCopyOption.REPLACE_EXISTING);
                }
            return key;
            }
        catch (IOException e){
            throw new UncheckedIOException("Failed to store file", e);
        }
    }

    @Override
    public Resource load (String storageKey){
       Path path = getSafePath(storageKey);
       if(!Files.exists(path)){
           throw new RuntimeException("File not found in storage: " + storageKey);
       }
       return new FileSystemResource(path);
    }
    @Override
    public void delete(String storageKey){
        try {
            Files.deleteIfExists(getSafePath(storageKey));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to delete file", e);
        }
    }
    private Path getSafePath(String key){
        Path path = root.resolve(key).normalize();
        if(!path.startsWith(root)){
            throw new SecurityException("Invalid security key");
        }
        return path;
    }
}
