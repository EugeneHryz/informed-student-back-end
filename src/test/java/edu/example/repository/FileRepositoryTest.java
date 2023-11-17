package edu.example.repository;

import edu.example.MinioTestConfig;
import edu.example.PostgresTestConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ContextConfiguration(initializers = {MinioTestConfig.Initializer.class, PostgresTestConfig.Initializer.class})
public class FileRepositoryTest {

    @Autowired
    FilesRepository filesRepository;

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
    public void deleteTestFiles() {
        testFile.delete();
        resultFile.delete();
        for (String fileName : filesRepository.getObjectList())
            filesRepository.deleteObject(fileName);
    }

    @Test
    public void saveFile() throws IOException {
        // when
        var inputStream = new FileInputStream(testFile);
        filesRepository.saveObject(testFile.getName(),
                testFile.length(),
                inputStream);
        inputStream.close();

        // then
        filesRepository.deleteObject(testFile.getName());
    }

    @Test
    public void getFile() throws IOException {
        // given
        var inputStream = new FileInputStream(testFile);
        filesRepository.saveObject(testFile.getName(),
                testFile.length(),
                inputStream);
        inputStream.close();

        // when
        var result = filesRepository.getObject(testFile.getName());
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

}
