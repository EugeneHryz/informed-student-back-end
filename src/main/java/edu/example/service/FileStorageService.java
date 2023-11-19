package edu.example.service;

import edu.example.repository.exception.FileReadException;
import edu.example.repository.exception.FileWriteException;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface FileStorageService {

    List<MinioFileStorageService.FileSaveResult> save(List<MultipartFile> files) throws FileWriteException;

    InputStream get(String filename) throws FileReadException;

    void delete(String filename) throws FileWriteException;
}
