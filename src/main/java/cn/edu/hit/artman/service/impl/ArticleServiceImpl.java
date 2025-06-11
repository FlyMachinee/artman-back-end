package cn.edu.hit.artman.service.impl;

import cn.edu.hit.artman.common.enumeration.ArticleStatus;
import cn.edu.hit.artman.common.exception.ArtManException;
import cn.edu.hit.artman.mapper.ArticleMapper;
import cn.edu.hit.artman.mapper.CategoryMapper;
import cn.edu.hit.artman.pojo.dto.ArticleCreateDTO;
import cn.edu.hit.artman.pojo.dto.ArticleSimpleSearchDTO;
import cn.edu.hit.artman.pojo.dto.ArticleUpdateDTO;
import cn.edu.hit.artman.pojo.po.Category;
import cn.edu.hit.artman.pojo.vo.ArticleInfoVO;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.hit.artman.pojo.po.Article;
import cn.edu.hit.artman.service.ArticleService;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static java.net.HttpURLConnection.*;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService{

    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional // 确保事务一致性
    public Long createArticleWithMetaInfo(Long userId, ArticleCreateDTO articleCreateDTO) {
        Article article = BeanUtil.copyProperties(articleCreateDTO, Article.class);
        article.setUserId(userId);

        // 检查标题
        if (article.getTitle().isEmpty()) {
            throw new ArtManException(HTTP_BAD_REQUEST, "标题不能为空");
        }

        // 检查分类是否存在
        if (article.getCategoryId() != 0) {
            Category category = categoryMapper.selectById(article.getCategoryId());
            if (category == null) {
                throw new ArtManException(HTTP_NOT_FOUND, "分类不存在");
            }
            if (!category.getUserId().equals(userId)) {
                throw new ArtManException(HTTP_FORBIDDEN, "该分类不可用，非本用户创建");
            }
        } else {
            article.setCategoryId(null); // 如果分类为0，表示顶级分类，不设置分类ID
        }

        if (articleMapper.insert(article) <= 0) {
            throw new ArtManException(HTTP_INTERNAL_ERROR, "创建文章失败");
        }

        return article.getArticleId();
    }

    @Override
    public PageInfo<ArticleInfoVO> getAllArticlesByUserId(Long userId, Integer pageNum, Integer pageSize) {
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);

        // 构建查询条件：根据 userId 查询所有文章
        List<ArticleInfoVO> articles = articleMapper.getAllArticlesInfoByUserId(userId);

        // 使用 PageInfo 包装结果
        return new PageInfo<>(articles);
    }

    @Override
    public PageInfo<ArticleInfoVO> getSimpleSearchArticlesByUserId(
        Long userId, Integer pageNum, Integer pageSize, ArticleSimpleSearchDTO articleSimpleSearchDTO) {

        // 开启分页
        PageHelper.startPage(pageNum, pageSize);

        // 构建查询条件：根据 userId 和其他搜索条件查询文章
        List<ArticleInfoVO> articles =
            articleMapper.getSimpleSearchArticlesInfoByUserId(
                userId,
                articleSimpleSearchDTO.getIsShared(),
                articleSimpleSearchDTO.getStatus(),
                articleSimpleSearchDTO.getStartDate(),
                articleSimpleSearchDTO.getEndDate()
            );

        // 使用 PageInfo 包装结果
        return new PageInfo<>(articles);
    }

    @Override
    public ArticleInfoVO getArticleInfoById(Long articleId) {
        // 根据 articleId 查询文章信息
        ArticleInfoVO articleInfo = articleMapper.getArticleInfoById(articleId);

        // 如果未找到文章，抛出异常
        if (articleInfo == null) {
            throw new ArtManException(HTTP_NOT_FOUND, "文章不存在");
        }

        return articleInfo;
    }

    @Override
    @Transactional
    public boolean updateArticleWithMetaInfo(Long userId, Long articleId, ArticleUpdateDTO articleUpdateDTO) {
       // 检查文章是否存在
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new ArtManException(HTTP_NOT_FOUND, "文章不存在");
        }

        // 检查用户权限
        if (!article.getUserId().equals(userId)) {
            throw new ArtManException(HTTP_FORBIDDEN, "无权限修改该文章");
        }

        // 检查标题
        if (articleUpdateDTO.getTitle() != null && articleUpdateDTO.getTitle().trim().isEmpty()) {
            throw new ArtManException(HTTP_BAD_REQUEST, "标题不能为空");
        }

        // 检查共享状态
        // 只能从未共享变为共享，不能从共享变为未共享
        if (articleUpdateDTO.getIsShared() != null &&
            !articleUpdateDTO.getIsShared() &&
            article.getIsShared()) {
            throw new ArtManException(HTTP_BAD_REQUEST, "不能将已共享的文章修改为未共享状态");
        }

        // 检查文章状态
        // 只能从未发布变为已发布，不能从已发布变为未发布
        if (articleUpdateDTO.getStatus() != null &&
            articleUpdateDTO.getStatus() == ArticleStatus.DRAFT &&
            article.getStatus() == ArticleStatus.PUBLISHED) {
            throw new ArtManException(HTTP_BAD_REQUEST, "不能将已发布的文章修改为草稿状态");
        }

        // 更新文章信息
        BeanUtil.copyProperties(articleUpdateDTO, article);

        // 检查分类
        Long categoryId = articleUpdateDTO.getCategoryId();
        if (categoryId == null) {
            // 不更新分类，保持原有分类
            article.setCategoryId(null);

        } else if (categoryId == 0) {
            // 更新为顶级分类
            articleMapper.setCategoryIdNull(articleId);
            article.setCategoryId(null);

        } else {
            // 检查分类是否存在
            Category category = categoryMapper.selectById(categoryId);
            if (category == null) {
                throw new ArtManException(HTTP_NOT_FOUND, "分类不存在");
            }

            // 检查分类是否可被该用户使用
            if (!category.getUserId().equals(userId)) {
                throw new ArtManException(HTTP_FORBIDDEN, "该分类不可用，非本用户创建");
            }

            // 更新分类
            article.setCategoryId(categoryId);
        }

        return articleMapper.updateById(article) > 0;
    }

    @Override
    @Transactional
    public boolean deleteArticle(Long userId, Long articleId) {
       // 检查文章是否存在
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new ArtManException(HTTP_NOT_FOUND, "文章不存在");
        }

        // 检查用户权限
        if (!article.getUserId().equals(userId)) {
            throw new ArtManException(HTTP_FORBIDDEN, "无权限删除该文章");
        }

        // 删除文章
        return articleMapper.deleteById(articleId) > 0;
    }

    @Override
    public PageInfo<ArticleInfoVO> getSimpleSearchSharedArticles(
        Integer pageNum, Integer pageSize, LocalDateTime startDate, LocalDateTime endDate) {

        // 开启分页
        PageHelper.startPage(pageNum, pageSize);

        // 构建查询条件：根据共享状态和时间范围查询文章
        List<ArticleInfoVO> articles =
            articleMapper.getSimpleSearchSharedArticles(startDate, endDate);

        // 使用 PageInfo 包装结果
        return new PageInfo<>(articles);
    }
}




