package com.chatapp.service;

import com.chatapp.data.entity.Attachment;
import com.chatapp.data.repository.AttachmentRepository;
import com.chatapp.data.repository.UserRepository;
import com.chatapp.exception.FileStorageException;
import com.chatapp.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    @Value("${application.file.upload-dir}")
    private String uploadDir;


    @Transactional
    public Attachment uploadFile(Long userId, MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new FileStorageException("File cannot be empty.");
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        try {

            Map<String, Object> uploadResult = cloudinaryService.uploadFile(file);

            Attachment attachment = new Attachment();
            attachment.setFileName(file.getOriginalFilename());
            attachment.setOriginalName(file.getOriginalFilename());
            attachment.setFileType(file.getContentType());
            attachment.setFileSize(file.getSize());
            attachment.setFileUrl((String) uploadResult.get("secure_url"));
            attachment.setPublicId((String) uploadResult.get("public_id"));
            attachment.setUploadedBy(userId);

            return attachmentRepository.save(attachment);

        } catch (IOException e) {
            throw new FileStorageException("Unable to upload file.");
        }
    }


    // Get Attachment
    public Attachment getAttachment(Long attachmentId) {

        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found."));
    }


    // Download File
    public Path getFile(Long attachmentId) {

        Attachment attachment = getAttachment(attachmentId);
        return Paths.get(attachment.getFileUrl());
    }


    // Delete Attachment
    public void deleteAttachment(Long attachmentId) {

        Attachment attachment = getAttachment(attachmentId);

        try {
            Files.deleteIfExists(Paths.get(attachment.getFileUrl()));
        } catch (IOException ignored) {
        }

        attachmentRepository.delete(attachment);
    }
}