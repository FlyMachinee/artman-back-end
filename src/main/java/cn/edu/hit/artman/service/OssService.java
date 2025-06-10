package cn.edu.hit.artman.service;

import java.io.InputStream;

public interface OssService {

    String uploadFile(String objectName, String content);

    String uploadFile(String objectName, InputStream inputStream, long contentLength);

    void deleteFile(String objectName);

    String generateObjectName(Long userId, Long articleId);

    String getUrlPrefix();
}
