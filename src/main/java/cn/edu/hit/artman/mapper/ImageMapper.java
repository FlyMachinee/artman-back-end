package cn.edu.hit.artman.mapper;

import cn.edu.hit.artman.pojo.po.Image;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface ImageMapper extends BaseMapper<Image> {

    List<Image> getImagesByUserId(Long userId);

}




