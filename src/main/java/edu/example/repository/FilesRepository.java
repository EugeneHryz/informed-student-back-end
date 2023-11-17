package edu.example.repository;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
public class FilesRepository {

    private final MinioClient minioClient;

    private String bucketName = "data";

    public FilesRepository(@Value("${minio.datasource.url}") String minioURL,
                           @Value("${minio.datasource.username}") String minioUsername,
                           @Value("${minio.datasource.password}") String minioPassword,
                           @Value("${minio.bucket-name}") String bucketName) {
        this.bucketName = bucketName;

        minioClient = MinioClient.builder()
                .endpoint(minioURL)
                .credentials(minioUsername, minioPassword)
                .build();
    }

    @PostConstruct
    @SneakyThrows
    public void createBucket() {
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());

        if (!bucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName).build());
        }
    }

    public void saveObject(String objectName, Long size, FileInputStream object) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(object, size, -1).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream getObject(String objectName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteObject(String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getObjectList() {
        var iterable = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucketName).build());

        List<String> objectNameList = new LinkedList<>();
        for (Result<Item> result : iterable) {
            try {
                objectNameList.add(result.get().objectName());
            } catch (Exception ignored) {}
        }
        return objectNameList;
    }

}
