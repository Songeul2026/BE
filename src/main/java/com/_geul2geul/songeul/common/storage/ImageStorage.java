package com._geul2geul.songeul.common.storage;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorage {

    String store(MultipartFile image);

}
