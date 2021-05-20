package com.boot.contactmanager.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

// upload image
@Component
public class ImageUploader {
    public String uploadImage(MultipartFile image) throws Exception {
        // not file present
        if (image.isEmpty()) {
            throw new Exception("file isnt an image");
        }

        // file not image
        if (!image.getContentType().split("/")[0].equals("image")) {
            throw new Exception("file format invalid");
        }

        // produce a unique(ish) filename
        String fileName = System.currentTimeMillis() + '_' + image.getOriginalFilename();
        Path path = Paths
                .get(new ClassPathResource("static/images/").getFile().getAbsolutePath() + File.separator + fileName);
        Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }
}
