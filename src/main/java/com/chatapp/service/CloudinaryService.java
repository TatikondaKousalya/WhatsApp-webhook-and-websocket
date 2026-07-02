package com.chatapp.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {

        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.emptyMap()
        );

        return uploadResult.get("secure_url").toString();
    }

    public Map<String, Object> uploadFile(MultipartFile file) throws IOException {

        return cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "thread-chat",
                        "resource_type", "auto"
                )
        );
    }

    public void deleteFile(String publicId) throws IOException {

        cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.emptyMap()
        );
    }
}