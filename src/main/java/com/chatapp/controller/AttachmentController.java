package com.chatapp.controller;

import com.chatapp.data.entity.Attachment;
import com.chatapp.dto.response.ApiResponse;
import com.chatapp.service.AttachmentService;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Attachment>> upload(@RequestParam Long userId, @RequestParam MultipartFile file) {
        Attachment attachment = attachmentService.uploadFile(userId, file);
        return ResponseEntity.ok(ApiResponse.<Attachment>builder().success(true)
                .message("File uploaded successfully.").data(attachment).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Attachment>> get(@PathVariable Long id) {
        Attachment attachment = attachmentService.getAttachment(id);
        return ResponseEntity.ok(ApiResponse.<Attachment>builder().success(true)
                .message("File details fetched successfully.").data(attachment).build());
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws Exception {
        Path path = attachmentService.getFile(id);
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        attachmentService.deleteAttachment(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("File deleted successfully.").build());
    }

}