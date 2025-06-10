package cn.edu.hit.artman.service.impl;

import cn.edu.hit.artman.common.exception.ArtManException;
import cn.edu.hit.artman.mapper.ImageMapper;
import cn.edu.hit.artman.mapper.UserMapper;
import cn.edu.hit.artman.pojo.po.User;
import cn.edu.hit.artman.pojo.vo.ImageInfoVO;
import cn.edu.hit.artman.service.OssService;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.hit.artman.pojo.po.Image;
import cn.edu.hit.artman.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static java.net.HttpURLConnection.*;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image>
    implements ImageService{

    private final ImageMapper imageMapper;
    private final UserMapper userMapper;
    private final OssService ossService;

    // 配置允许的图片类型和最大文件大小
    private static final List<String> ALLOWED_IMAGE_TYPES =
        List.of("image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Override
    public ImageInfoVO uploadImage(MultipartFile file, String name, Long userId) {

        // 文件校验
        if (file.isEmpty()) {
            throw new ArtManException(HTTP_BAD_REQUEST, "上传文件为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ArtManException(HTTP_BAD_REQUEST, "文件大小超出限制 (最大5MB)");
        }
        String contentType = file.getContentType();
        if (!ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new ArtManException(HTTP_BAD_REQUEST, "不支持的文件类型，只支持 JPG, PNG, GIF, BMP, WEBP");
        }

        // 生成文件名和路径
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // images/userId/uuid.extension
        String objectName = "images/" + userId + "/" + UUID.randomUUID() + fileExtension;

        String uploadedUrl;
        try {
            // 上传到OSS，使用新的 InputStream 上传方法
            uploadedUrl = ossService.uploadFile(objectName, file.getInputStream(), file.getSize());

            // 保存图片信息到数据库
            Image image = new Image();
            image.setUserId(userId);
            image.setName(name != null && !name.trim().isEmpty() ? name : originalFilename); // 如果没有提供名称，使用原始文件名
            image.setUrl(uploadedUrl);

            imageMapper.insert(image);

            // 转换为VO并返回
            return BeanUtil.copyProperties(image, ImageInfoVO.class);

        } catch (IOException e) {
            log.error("文件读取失败或OSS上传过程中IO错误", e);
            throw new ArtManException(HTTP_INTERNAL_ERROR, "文件上传失败：IO错误");
        } catch (RuntimeException e) {
            log.error("OSS服务操作失败: {}" + e.getMessage(), e);
            throw new ArtManException(HTTP_INTERNAL_ERROR, "图片上传到云存储失败：" + e.getMessage());
        }
    }

    @Override
    public List<ImageInfoVO> getUserImages(Long userId) {

        // 检查用户ID是否有效
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ArtManException(HTTP_NOT_FOUND, "用户不存在");
        }

        List<Image> images = imageMapper.getImagesByUserId(userId);
        if (images == null || images.isEmpty()) {
            return List.of(); // 返回空列表而不是null
        }

        // 转换为VO列表
        return BeanUtil.copyToList(images, ImageInfoVO.class);
    }

    @Override
    public void deleteImage(Long imageId, Long userId) {
        // 检查图片是否存在
        Image image = imageMapper.selectById(imageId);
        if (image == null) {
            throw new ArtManException(HTTP_NOT_FOUND, "图片不存在");
        }

        // 检查用户是否有权限删除该图片
        if (!image.getUserId().equals(userId)) {
            throw new ArtManException(HTTP_FORBIDDEN, "无权删除他人图片");
        }

        // 从OSS删除文件
        try {
            // 从URL解析出objectName
            String objectName = extractObjectNameFromUrl(image.getUrl());
            ossService.deleteFile(objectName);
        } catch (Exception e) {
            log.error("OSS图片文件删除失败，但尝试继续删除数据库记录: {}", e);
            // 即使OSS删除失败，也尝试删除数据库记录，避免数据库中存在无效记录
        }

        // 从数据库删除记录
        int deletedRows = imageMapper.deleteById(imageId);
        if (deletedRows <= 0) {
            throw new ArtManException(HTTP_INTERNAL_ERROR, "图片数据库记录删除失败");
        }
    }

    // 辅助方法：从OSS URL中提取对象名称
    private String extractObjectNameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        // 假设urlPrefix是如 "http://your-bucket.oss-cn-beijing.aliyuncs.com/"
        // objectName是 "images/1/uuid.jpg"
        // 确保urlPrefix以 '/' 结尾
        String urlPrefix = ossService.getUrlPrefix(); // Assuming OssService has getUrlPrefix()
        if (url.startsWith(urlPrefix)) {
            return url.substring(urlPrefix.length());
        }
        // 如果URL结构不符合预期，需要根据实际OSS URL格式调整
        // 否则抛出异常或者返回null，具体取决于业务需求
        throw new IllegalArgumentException("无法从URL中提取OSS对象名称: " + url);
    }
}




