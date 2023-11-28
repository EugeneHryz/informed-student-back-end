package edu.example.web.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public MinioClient minioClient(
            @Value("${minio.datasource.url}") String endpoint,
            @Value("${minio.datasource.username}") String accessKey,
            @Value("${minio.datasource.password}") String secretKey
    ) {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
