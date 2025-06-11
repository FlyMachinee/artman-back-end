package cn.edu.hit.artman.service.impl;

import cn.edu.hit.artman.service.OssService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Service
public class OssServiceImpl implements OssService {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;
    @Value("${aliyun.oss.bucketName}")
    private String bucketName;
    @Value("${aliyun.oss.urlPrefix}")
    private String urlPrefix;

    private OSS ossClient;

    @PostConstruct
    public void init() {
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    @Override
    public String uploadFile(String objectName, String content) {
        ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content.getBytes()));
        return urlPrefix + objectName;
    }

    @Override
    public String uploadFile(String objectName, InputStream inputStream, long contentLength) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        ossClient.putObject(bucketName, objectName, inputStream, metadata);
        return urlPrefix + objectName;
    }

    @Override
    public void deleteFile(String objectName) {
        ossClient.deleteObject(bucketName, objectName);
    }

    @Override
    public String generateObjectName(Long userId, Long articleId) {
        return "user/" + userId + "/article/" + articleId + ".md";
    }

    @Override
    public String getUrlPrefix() {
        return urlPrefix;
    }

    @Override
    public String getContentFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            // Open a connection to the URL
            URLConnection connection = url.openConnection();
            // Set a read timeout (optional, but good practice)
            connection.setReadTimeout(5000); // 5 seconds

            // Get the input stream from the connection
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                // Read all lines from the BufferedReader and join them into a single String
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        } catch (IOException e) {
            // It's good practice to log the error or wrap it in a custom exception
            // For simplicity, re-throwing as a RuntimeException here, but consider
            // a more specific exception handling strategy for a production application.
            throw new RuntimeException("Failed to get content from URL: " + urlString, e);
        }
    }
}
