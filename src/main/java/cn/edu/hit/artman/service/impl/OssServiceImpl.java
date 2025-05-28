package cn.edu.hit.artman.service.impl;

import cn.edu.hit.artman.service.OssService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.ByteArrayInputStream;

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
    public void deleteFile(String objectName) {
        ossClient.deleteObject(bucketName, objectName);
    }

    @Override
    public String generateObjectName(Long userId, Long articleId) {
        return "user/" + userId + "/article/" + articleId + ".md";
    }
}
