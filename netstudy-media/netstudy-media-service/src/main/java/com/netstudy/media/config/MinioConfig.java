package com.netstudy.media.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Dico
 * @version 1.0
 * @description 文件传输配置
 * @date 2024/4/24 15:10
 **/
@Configuration
public class MinioConfig {
    @Value("${minio.endpoint}")
    private String endPoint;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(endPoint)
                        .credentials(accessKey, secretKey)
                        .build();
        return minioClient;
    }
}
