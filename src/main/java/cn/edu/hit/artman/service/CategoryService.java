package cn.edu.hit.artman.service;

import cn.edu.hit.artman.pojo.po.Category;
import cn.edu.hit.artman.pojo.vo.CategoryInfoVO;
import cn.edu.hit.artman.pojo.vo.CategoryTreeEntryVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CategoryService extends IService<Category> {

    Long createCategory(Long userId, String name, Long parentId);

    CategoryInfoVO getCategoryInfoById(Long categoryId, Long UserId);

    List<CategoryTreeEntryVO> getCategoryWholeTree(Long userId);

    CategoryTreeEntryVO getCategoryTree(Long categoryId, Long userId);

    boolean updateCategory(Long categoryId, Long userId, String name, Long parentId);

    boolean deleteCategory(Long categoryId, Long userId);

    List<CategoryInfoVO> getCategoryPath(Long categoryId, Long userId);
}
