package cn.edu.hit.artman.service.impl;

import cn.edu.hit.artman.common.exception.ArtManException;
import cn.edu.hit.artman.mapper.CategoryMapper;
import cn.edu.hit.artman.pojo.vo.CategoryInfoVO;
import cn.edu.hit.artman.pojo.vo.CategoryTreeEntryVO;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.hit.artman.pojo.po.Category;
import cn.edu.hit.artman.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.net.HttpURLConnection.*;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

    private final CategoryMapper categoryMapper;

    @Override
    public Long createCategory(Long userId, String name, Long parentId) {

        // 检查分类名称是否为空
        if (name == null || name.trim().isEmpty()) {
            throw new ArtManException(HTTP_BAD_REQUEST, "分类名称不能为空");
        }
        name = name.trim();

        // 检查同级分类名称是否重复
        Category existingCategory =
            categoryMapper.getCategoryByNameAndParentId(name, parentId);
        if (existingCategory != null) {
            throw new ArtManException(HTTP_BAD_REQUEST, "同级分类名称不能重复，请选择其他名称");
        }

        // 检查父分类是否存在
        if (parentId != 0) {
            Category parentCategory = categoryMapper.selectById(parentId);
            if (parentCategory == null) {
                throw new ArtManException(HTTP_NOT_FOUND, "父分类不存在");
            }
            // 检查用户是否有权限创建在该父分类下
            if (!parentCategory.getUserId().equals(userId)) {
                throw new ArtManException(HTTP_FORBIDDEN, "无权在该父分类下创建分类，该分类不属于您");
            }
        } else {
            // 如果parentId为0，表示创建顶级分类
            parentId = null; // 设置为null表示顶级分类
        }

        // 创建新分类
        Category newCategory = new Category();
        newCategory.setName(name);
        newCategory.setParentId(parentId);
        newCategory.setUserId(userId);

        return categoryMapper.insert(newCategory) > 0 ? newCategory.getCategoryId() : null;
    }

    @Override
    public CategoryInfoVO getCategoryInfoById(Long categoryId, Long UserId) {

        Category category = categoryMapper.selectById(categoryId);

        // 检查分类是否存在
        if (category == null) {
            throw new ArtManException(HTTP_NOT_FOUND, "分类不存在");
        }

        // 检查用户是否有权限访问该分类
        if (!category.getUserId().equals(UserId)) {
            throw new ArtManException(HTTP_FORBIDDEN, "无权访问该分类信息");
        }

        return BeanUtil.copyProperties(category, CategoryInfoVO.class);
    }

    @Override
    public List<CategoryTreeEntryVO> getCategoryWholeTree(Long userId) {
        return categoryMapper.getCategoryWholeTree(userId);
    }

    @Override
    public CategoryTreeEntryVO getCategoryTree(Long categoryId, Long userId) {

        // 检查分类是否存在
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new ArtManException(HTTP_NOT_FOUND, "分类不存在");
        }

        // 检查用户是否有权限访问该分类
        if (!category.getUserId().equals(userId)) {
            throw new ArtManException(HTTP_FORBIDDEN, "无权访问该分类信息");
        }

        CategoryTreeEntryVO categoryTreeEntryVO =
            BeanUtil.copyProperties(category, CategoryTreeEntryVO.class);

        // 获取该分类的树状结构
        List<CategoryTreeEntryVO> children = categoryMapper.getCategoryTree(categoryId);

        if (children != null && !children.isEmpty()) {
            categoryTreeEntryVO.setChildren(children);
        } else {
            categoryTreeEntryVO.setChildren(List.of());
        }

        return categoryTreeEntryVO;
    }

    @Override
    public boolean updateCategory(Long categoryId, Long userId, String name, Long parentId) {
        // 检查分类是否存在
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new ArtManException(HTTP_NOT_FOUND, "分类不存在");
        }

        // 检查用户是否有权限修改该分类
        if (!category.getUserId().equals(userId)) {
            throw new ArtManException(HTTP_FORBIDDEN, "无权修改该分类信息");
        }

        // 检查分类名
        if (name != null) {
            if (name.trim().isEmpty()) {
                throw new ArtManException(HTTP_BAD_REQUEST, "分类名称不能为空");
            }
            // 检查同级分类名称是否重复
            Category existingCategory =
                categoryMapper.getCategoryByNameAndParentId(name, category.getParentId());
            if (existingCategory != null && !existingCategory.getCategoryId().equals(categoryId)) {
                throw new ArtManException(HTTP_BAD_REQUEST, "同级分类名称不能重复，请选择其他名称");
            }
        }
        category.setName(name);

        // 处理不同的parentId情况
        if (parentId == null) {
            // 不修改父分类
            category.setParentId(null);
        } else if (parentId == 0L) {
            // 设置为顶级分类
            categoryMapper.setParentIdNull(categoryId);
            category.setParentId(null);
        } else {
            // 检查新的父分类ID是否有效
            if (parentId.equals(categoryId)) {
                throw new ArtManException(HTTP_BAD_REQUEST, "不能将分类设置为自己的父分类");
            }

            // 检查新的父分类是否存在
            Category parentCategory = categoryMapper.selectById(parentId);
            if (parentCategory == null) {
                throw new ArtManException(HTTP_NOT_FOUND, "父分类不存在");
            }

            // 检查用户是否有权限修改在该父分类下
            if (!parentCategory.getUserId().equals(userId)) {
                throw new ArtManException(HTTP_FORBIDDEN, "无权将该分类作为新的父分类，该分类不属于您");
            }

            // 检查是否有循环引用
            List<Category> path = categoryMapper.getCategoryPath(parentId);
            if (path.stream().anyMatch(c -> c.getCategoryId().equals(categoryId))) {
                throw new ArtManException(HTTP_BAD_REQUEST, "出现循环引用，不能将新的父分类设置为该分类的子分类");
            }

            category.setParentId(parentId);
        }

        return categoryMapper.updateById(category) > 0;
    }

    @Override
    public boolean deleteCategory(Long categoryId, Long userId) {

        // 检查分类是否存在
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new ArtManException(HTTP_NOT_FOUND, "分类不存在");
        }

        // 检查用户是否有权限删除该分类
        if (!category.getUserId().equals(userId)) {
            throw new ArtManException(HTTP_FORBIDDEN, "无权删除该分类信息");
        }

        // 删除分类及其子分类
        return categoryMapper.deleteById(categoryId) > 0;
    }

    @Override
    public List<CategoryInfoVO> getCategoryPath(Long categoryId, Long userId) {
        // 检查分类是否存在
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new ArtManException(HTTP_NOT_FOUND, "分类不存在");
        }

        // 检查用户是否有权限访问该分类
        if (!category.getUserId().equals(userId)) {
            throw new ArtManException(HTTP_FORBIDDEN, "无权访问该分类信息");
        }

        // 获取分类路径
        List<Category> path = categoryMapper.getCategoryPath(categoryId);

        // 将路径转换为VO列表
        return BeanUtil.copyToList(path, CategoryInfoVO.class);
    }
}




