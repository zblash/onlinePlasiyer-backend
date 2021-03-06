package com.marketing.web.services.storage;

import com.marketing.web.validations.ValidImg;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.stream.Stream;

@Service
@Validated
public class StorageServiceImpl implements StorageService {

    private final Path rootLocation = Paths.get("upload-dir");

    @Override
    public String store(@ValidImg MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        fileName = String.valueOf(new Date().getTime()).concat(fileName);
        fileName = fileName.toLowerCase().replaceAll(" ", "-");
        try {
            Files.copy(file.getInputStream(), this.rootLocation.resolve(fileName));
        }catch (IOException e){
            throw new RuntimeException();
        }
        return fileName;
    }

    @Override
    public Stream<Path> loadAll() {
        return null;
    }

    @Override
    public Path load(String filename) {
        return null;
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("FAIL!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("FAIL!");
        }
    }

    @Override
    public byte[] loadAsByteArray(String filename) throws IOException {
        Path file = rootLocation.resolve(filename);
        InputStream targetStream = new FileInputStream(file.toFile());
        return StreamUtils.copyToByteArray(targetStream);
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void init() {
        if (!Files.exists(rootLocation))
        {
            try {
                Files.createDirectory(rootLocation);
            } catch (IOException e) {
                throw new RuntimeException("Could not initialize storage!");
            }
        }
    }
}