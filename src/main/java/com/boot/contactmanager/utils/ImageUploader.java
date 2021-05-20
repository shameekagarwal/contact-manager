package com.boot.contactmanager.utils;

import java.io.File;
import java.util.Map;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

// upload image
@Component
public class ImageUploader {

    @Value("${cloudinaryconfig.cloud_name}")
    private String cloudName;

    @Value("${cloudinaryconfig.api_key}")
    private String apiKey;

    @Value("${cloudinaryconfig.api_secret}")
    private String apiSecret;

    private File multipartToFile(MultipartFile multipart, String fileName) throws Exception {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
        multipart.transferTo(convFile);
        return convFile;
    }

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
        // used by cloudinary for storing in tmp directory
        String fileName = System.currentTimeMillis() + image.getOriginalFilename();

        // CLOUDINARY
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                //
                "cloud_name", cloudName,
                //
                "api_key", apiKey,
                //
                "api_secret", apiSecret));
        Map<?, ?> result = cloudinary.uploader().upload(multipartToFile(image, fileName),
                ObjectUtils.asMap("folder", "contact-manager"));

        System.out.println(result);

        return (String) result.get("secure_url");
    }
}
