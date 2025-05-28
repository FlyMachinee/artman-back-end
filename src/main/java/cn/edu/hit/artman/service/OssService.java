package cn.edu.hit.artman.service;

public interface OssService {

    String uploadFile(String objectName, String content);

    void deleteFile(String objectName);

    String generateObjectName(Long userId, Long articleId);
}
