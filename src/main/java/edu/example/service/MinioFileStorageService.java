package edu.example.service;

import edu.example.exception.EntityNotFoundException;
import edu.example.model.FileModel;
import edu.example.repository.FileRepository;
import edu.example.repository.MinioFileStorage;
import edu.example.repository.exception.FileReadException;
import edu.example.repository.exception.FileWriteException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class MinioFileStorageService implements FileStorageService {

    private static final int MAX_ATTEMPTS_TO_GEN_FILENAME = 66;

    private final MinioFileStorage fileStorage;
    private final FileRepository fileRepository;

    /**
     * save file in the Minio storage with file extension as prefix
     * @param file file to save
     * @return generated file name
     * @throws IOException
     */
    private String saveFile(MultipartFile file) throws FileWriteException {

        String fileExt = FilenameUtils.getExtension(file.getOriginalFilename());
        String generatedFileName = String.format("%s/%s", fileExt, UUID.randomUUID());

        int tryCount = 0;
        while (fileStorage.isObjectExist(generatedFileName)) {
            if (tryCount++ > MAX_ATTEMPTS_TO_GEN_FILENAME) {
                throw new RuntimeException("Object with generated name already exists. This is an internal error");
            }
            generatedFileName = String.format("%s/%s", fileExt, UUID.randomUUID());
        }
        try {
            fileStorage.saveObject(generatedFileName, file.getSize(), file.getInputStream());
        } catch (FileWriteException | IOException e) {
            throw new FileWriteException(e);
        }

        return generatedFileName;
    }

    @Override
    public List<FileSaveResult> save(List<MultipartFile> files) throws FileWriteException {
        List<FileSaveResult> result = new ArrayList<>();
        for (var file : files) {
            try {
                result.add(new FileSaveResult(file.getOriginalFilename(), saveFile(file)));
            } catch (FileWriteException ex) {
                for (var saved : result) {
                    try {
                        fileStorage.deleteObject(saved.getSavedFilename());
                    } catch (FileWriteException ignored) {}
                }
                throw new FileWriteException(ex);
            }
        }
        return result;
    }

    @Override
    public InputStream get(String filename) throws FileReadException {
        return fileStorage.getObject(filename);
    }

    @Override
    public FileModel getModel(String filename) {
        return fileRepository.findBySavedByName(filename).orElseThrow(() -> new EntityNotFoundException("File not found"));
    }

    @Override
    public void delete(String filename) throws FileWriteException {
        fileStorage.deleteObject(filename);
    }

    @Data
    @AllArgsConstructor
    public static class FileSaveResult {
        private String originalName;
        private String savedFilename;
    }
}
