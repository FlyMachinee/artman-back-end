package cn.edu.hit.artman.mapper;

import cn.edu.hit.artman.pojo.po.Category;
import cn.edu.hit.artman.pojo.vo.CategoryTreeEntryVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface CategoryMapper extends BaseMapper<Category> {

    Category getCategoryByNameAndParentId(String name, Long parentId);

    List<Category> getCategoryByUserId(Long userId);

    List<CategoryTreeEntryVO> getCategoryWholeTree(Long userId);

    List<CategoryTreeEntryVO> getCategoryTree(Long parentId);

    List<Category> getCategoryPath(Long categoryId);

    void setParentIdNull(Long categoryId);
}




