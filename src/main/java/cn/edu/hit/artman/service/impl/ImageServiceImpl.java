package cn.edu.hit.artman.service.impl;

import cn.edu.hit.artman.mapper.ImageMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.hit.artman.pojo.po.Image;
import cn.edu.hit.artman.service.ImageService;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image>
    implements ImageService{

}




