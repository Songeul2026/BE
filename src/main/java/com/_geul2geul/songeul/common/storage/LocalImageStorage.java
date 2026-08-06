package com._geul2geul.songeul.common.storage;

import com._geul2geul.songeul.common.exception.CustomException;
import com._geul2geul.songeul.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Component
public class LocalImageStorage {

    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024;
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "heic");

    private final Path storageRoot;

    public LocalImageStorage(@Value("${app.upload.dir}") String uploadDir) {
        this.storageRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(storageRoot);
        } catch (IOException e) {
            throw new UncheckedIOException("업로드 디렉토리를 생성할 수 없습니다: " + storageRoot, e);
        }
    }

    public String store(MultipartFile image) {
        validate(image);

        String storedFileName = UUID.randomUUID() + "." + extractExtension(image.getOriginalFilename());

        try {
            Files.copy(image.getInputStream(), storageRoot.resolve(storedFileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("이미지 저장에 실패했습니다.", e);
        }

        return storedFileName;
    }

    private void validate(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_IMAGE_FILE);
        }
        if (image.getSize() > MAX_FILE_SIZE) {
            throw new CustomException(ErrorCode.INVALID_IMAGE_FILE);
        }
        if (!ALLOWED_EXTENSIONS.contains(extractExtension(image.getOriginalFilename()))) {
            throw new CustomException(ErrorCode.INVALID_IMAGE_FILE);
        }
    }

    private String extractExtension(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            throw new CustomException(ErrorCode.INVALID_IMAGE_FILE);
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

}
