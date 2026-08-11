package com._geul2geul.songeul.common.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "app.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalImageStorage implements ImageStorage {

    private final Path storageRoot;
    private final ImageFileValidator validator;

    public LocalImageStorage(@Value("${app.upload.dir}") String uploadDir, ImageFileValidator validator) {
        this.validator = validator;
        this.storageRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(storageRoot);
        } catch (IOException e) {
            throw new UncheckedIOException("업로드 디렉토리를 생성할 수 없습니다: " + storageRoot, e);
        }
    }

    @Override
    public String store(MultipartFile image) {
        String extension = validator.validateAndExtractExtension(image);
        String storedFileName = UUID.randomUUID() + "." + extension;

        try {
            Files.copy(image.getInputStream(), storageRoot.resolve(storedFileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("이미지 저장에 실패했습니다.", e);
        }

        return storedFileName;
    }

}
