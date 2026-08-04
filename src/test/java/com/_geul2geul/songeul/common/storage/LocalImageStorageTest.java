package com._geul2geul.songeul.common.storage;

import com._geul2geul.songeul.common.exception.CustomException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocalImageStorageTest {

    @TempDir
    Path tempDir;

    private LocalImageStorage newStorage() {
        return new LocalImageStorage(tempDir.toString());
    }

    @Test
    void 허용된_형식의_이미지는_저장에_성공한다() throws Exception {
        LocalImageStorage storage = newStorage();
        MockMultipartFile image = new MockMultipartFile("image", "memo.png", "image/png", "content".getBytes());

        String storedFileName = storage.store(image);

        assertThat(storedFileName).endsWith(".png");
        assertThat(Files.exists(tempDir.resolve(storedFileName))).isTrue();
    }

    @Test
    void 허용되지_않은_확장자는_예외가_발생한다() {
        LocalImageStorage storage = newStorage();
        MockMultipartFile image = new MockMultipartFile("image", "memo.gif", "image/gif", "content".getBytes());

        assertThrows(CustomException.class, () -> storage.store(image));
    }

    @Test
    void 파일_용량이_10MB를_초과하면_예외가_발생한다() {
        LocalImageStorage storage = newStorage();
        MockMultipartFile image = new MockMultipartFile(
                "image", "memo.jpg", "image/jpeg", new byte[10 * 1024 * 1024 + 1]);

        assertThrows(CustomException.class, () -> storage.store(image));
    }

    @Test
    void 빈_파일은_예외가_발생한다() {
        LocalImageStorage storage = newStorage();
        MockMultipartFile image = new MockMultipartFile("image", "memo.jpg", "image/jpeg", new byte[0]);

        assertThrows(CustomException.class, () -> storage.store(image));
    }

}
