package com.marketing.web.services.storage;

import com.marketing.web.validations.ValidImg;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    String store(@ValidImg MultipartFile file);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    byte[] loadAsByteArray(String filename) throws IOException;

    void deleteAll();

    void init();

}
