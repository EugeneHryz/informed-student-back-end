package edu.example.controller;

import java.io.InputStream;
import edu.example.repository.exception.FileReadException;
import edu.example.service.MinioFileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final MinioFileStorageService minioFileStorageService;

    @GetMapping
    public ResponseEntity<Resource> getFile(@RequestParam String filename) throws FileReadException {

        InputStream fileInputStream = minioFileStorageService.get(filename);
        if (fileInputStream == null) {
            return ResponseEntity.notFound().build();
        }

        InputStreamResource resource = new InputStreamResource(fileInputStream);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}