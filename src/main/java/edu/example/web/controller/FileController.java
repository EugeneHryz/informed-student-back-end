package edu.example.web.controller;

import edu.example.repository.exception.FileReadException;
import edu.example.service.MinioFileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final MinioFileStorageService minioFileStorageService;

    @GetMapping
    public ResponseEntity<Resource> getFile(@RequestParam String filename) throws FileReadException {
        var file = minioFileStorageService.getModel(filename);

        InputStream fileInputStream = minioFileStorageService.get(filename);
        if (fileInputStream == null) {
            return ResponseEntity.notFound().build();
        }

        InputStreamResource resource = new InputStreamResource(fileInputStream);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
                        URLEncoder.encode(file.getOriginalName(), StandardCharsets.UTF_8) + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}