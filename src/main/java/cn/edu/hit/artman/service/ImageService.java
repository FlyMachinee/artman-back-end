package cn.edu.hit.artman.service;

import cn.edu.hit.artman.pojo.po.Image;
import cn.edu.hit.artman.pojo.vo.ImageInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService extends IService<Image> {

    ImageInfoVO uploadImage(MultipartFile file, String name, Long userId);

    List<ImageInfoVO> getUserImages(Long userId);

    void deleteImage(Long imageId, Long userId);

}
