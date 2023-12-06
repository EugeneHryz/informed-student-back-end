package edu.example.repository;

import edu.example.TestContext;
import edu.example.repository.exception.FileReadException;
import edu.example.repository.exception.FileWriteException;
import io.minio.errors.ErrorResponseException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class MinioFileStorageServiceTest extends TestContext {

    @Autowired
    MinioFileStorage fileStorage;

    File testFile = new File("testFile.txt");
    File resultFile = new File("resultFile.txt");

    @BeforeEach
    @SneakyThrows
    public void createTestFiles() {
       testFile.createNewFile();
       resultFile.createNewFile();
       var writer = new BufferedWriter(new FileWriter(testFile));
       writer.write("HELLO WORLD!!!");
       writer.close();
    }

    @AfterEach
    public void deleteTestFiles() throws FileWriteException {
        testFile.delete();
        resultFile.delete();
        for (String fileName : fileStorage.getObjectList())
            fileStorage.deleteObject(fileName);
    }

    @Test
    public void saveFile() throws IOException, FileWriteException {
        // when
        var inputStream = new FileInputStream(testFile);
        fileStorage.saveObject(testFile.getName(),
                testFile.length(),
                inputStream);
        inputStream.close();

        // then
        fileStorage.deleteObject(testFile.getName());
    }

    @Test
    public void getFile() throws IOException, FileReadException, FileWriteException {
        // given
        var inputStream = new FileInputStream(testFile);
        fileStorage.saveObject(testFile.getName(),
                testFile.length(),
                inputStream);
        inputStream.close();

        // when
        var result = fileStorage.getObject(testFile.getName());
        var outStream = new FileOutputStream(resultFile);
        byte[] buffer = new byte[8 * 1024];
        int bytesRead;
        while ((bytesRead = result.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        outStream.close();
        result.close();

        // then
        assertTrue(FileUtils.contentEquals(testFile, resultFile));
    }

    @Test
    public void getNonExistingFile() {
        FileReadException exception = assertThrows(FileReadException.class,
                () -> fileStorage.getObject("testFileu74921.txt"));

        assertInstanceOf(ErrorResponseException.class, exception.getCause());
    }

}
