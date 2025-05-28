package cn.edu.hit.artman.mapper;

import cn.edu.hit.artman.common.enumeration.ArticleStatus;
import cn.edu.hit.artman.pojo.po.Article;
import cn.edu.hit.artman.pojo.vo.ArticleInfoVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleMapper extends BaseMapper<Article> {

    List<ArticleInfoVO> getAllArticlesInfoByUserId(Long userId);

    List<ArticleInfoVO> getSimpleSearchArticlesInfoByUserId(
        Long userId, Boolean isShared, ArticleStatus status, LocalDateTime startDate, LocalDateTime endDate);

    ArticleInfoVO getArticleInfoById(Long articleId);

    void setCategoryIdNull(Long articleId);

    List<ArticleInfoVO> getSimpleSearchSharedArticles(LocalDateTime startDate, LocalDateTime endDate);
}




