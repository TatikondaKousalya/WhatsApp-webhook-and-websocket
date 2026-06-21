package com.chatapp.controller;

import com.chatapp.data.entity.Attachment;
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
    public Attachment upload(@RequestParam Long userId, @RequestParam MultipartFile file){
        return attachmentService.uploadFile(userId,file);

    }

    @GetMapping("/{id}")
    public Attachment get(@PathVariable Long id){
        return attachmentService.getAttachment(id);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws Exception{

        Path path = attachmentService.getFile(id);
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok(resource);

    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id){
        attachmentService.deleteAttachment(id);
        return "File Deleted";
    }

}