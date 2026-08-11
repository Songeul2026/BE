package com._geul2geul.songeul.common.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "app.storage.type", havingValue = "s3")
public class S3ImageStorage implements ImageStorage {

    private final S3Client s3Client;
    private final ImageFileValidator validator;
    private final String bucket;

    public S3ImageStorage(S3Client s3Client, ImageFileValidator validator,
                           @Value("${app.storage.bucket}") String bucket) {
        this.s3Client = s3Client;
        this.validator = validator;
        this.bucket = bucket;
    }

    @Override
    public String store(MultipartFile image) {
        String extension = validator.validateAndExtractExtension(image);
        String storedFileName = UUID.randomUUID() + "." + extension;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(storedFileName)
                .contentType(image.getContentType())
                .build();

        try {
            s3Client.putObject(request, RequestBody.fromInputStream(image.getInputStream(), image.getSize()));
        } catch (IOException e) {
            throw new UncheckedIOException("이미지 저장에 실패했습니다.", e);
        }

        return storedFileName;
    }

}
