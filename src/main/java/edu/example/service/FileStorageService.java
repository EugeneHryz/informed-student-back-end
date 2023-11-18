package edu.example.service;

import edu.example.repository.MinioFileStorage;
import edu.example.repository.exception.FileReadException;
import edu.example.repository.exception.FileWriteException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
public class FileStorageService {

    private final MinioFileStorage fileStorage;

    @Autowired
    public FileStorageService(MinioFileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    /**
     * save file in the Minio storage with file extension as prefix
     * @param file file to save
     * @return generated file name
     * @throws IOException
     */
    public String saveFile(MultipartFile file) throws IOException, FileWriteException {

        String fileExt = FilenameUtils.getExtension(file.getOriginalFilename());
        String generatedFileName = UUID.randomUUID().toString();

        String objectName = fileExt + "/" + generatedFileName;
        if (fileStorage.isObjectExist(objectName)) {
            throw new RuntimeException("Object with generated name already exists. This is an internal error");
        }
        fileStorage.saveObject(objectName, file.getSize(), file.getInputStream());
        return generatedFileName;
    }

    public InputStream getFile(String prefix, String generatedFilename) throws FileReadException {
        return fileStorage.getObject(prefix + "/" + generatedFilename);
    }
}
