package com.chatapp.service;

import com.chatapp.data.entity.Attachment;
import com.chatapp.data.entity.User;
import com.chatapp.data.repository.AttachmentRepository;
import com.chatapp.data.repository.UserRepository;
import com.chatapp.exception.FileStorageException;
import com.chatapp.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final UserRepository userRepository;

    @Value("${application.file.upload-dir}")
    private String uploadDir;


    // Upload Attachment
    public Attachment uploadFile(Long userId, MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new FileStorageException("File cannot be empty.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        try {

            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());

            String fileName = UUID.randomUUID()
                    + (extension != null ? "." + extension : "");

            Path destination = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), destination,
                    StandardCopyOption.REPLACE_EXISTING);

            Attachment attachment = new Attachment();
            attachment.setFileName(fileName);
            attachment.setOriginalName(file.getOriginalFilename());
            attachment.setFilePath(destination.toString());
            attachment.setFileType(file.getContentType());
            attachment.setFileSize(file.getSize());
            attachment.setUploadedBy(user);

            return attachmentRepository.save(attachment);

        } catch (IOException ex) {
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
        return Paths.get(attachment.getFilePath());
    }


    // Delete Attachment
    public void deleteAttachment(Long attachmentId) {

        Attachment attachment = getAttachment(attachmentId);

        try {
            Files.deleteIfExists(Paths.get(attachment.getFilePath()));
        } catch (IOException ignored) {
        }

        attachmentRepository.delete(attachment);
    }
}