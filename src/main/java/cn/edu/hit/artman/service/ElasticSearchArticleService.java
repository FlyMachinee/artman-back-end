package cn.edu.hit.artman.service;

import cn.edu.hit.artman.common.enumeration.ArticleStatus;
import cn.edu.hit.artman.pojo.document.ArticleDocument;
import cn.edu.hit.artman.pojo.po.Article;
import cn.edu.hit.artman.pojo.vo.ArticleInfoVO;
import com.github.pagehelper.PageInfo;

import java.time.LocalDateTime;
import java.util.Set;

public interface ElasticSearchArticleService {

    /**
     * 将文章同步到 Elasticsearch
     * @param article 文章POJO
     */
    void syncArticle(Article article);

    void deleteAllArticles();

    /**
     * 将文章同步到 Elasticsearch
     * @param articleDocument 文章文档对象
     */
    void syncArticle(ArticleDocument articleDocument);

                     /**
     * 从 Elasticsearch 删除文章
     * @param articleId 文章ID
     */
    void deleteArticle(Long articleId);

    /**
     * 从数据库全量同步所有文章到 Elasticsearch（启动时或需要全量更新时调用）
     */
    void syncAllArticlesFromDatabase();

    /**
     * 复杂查询用户文章，支持多条件筛选、关键字搜索和高亮
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param isShared 是否共享
     * @param status 文章状态
     * @param categoryIds 分类ID集合（包含子分类）
     * @param startDate 创建日期开始
     * @param endDate 创建日期结束
     * @param keyword 关键字
     * @return 文章信息VO列表
     */
    PageInfo<ArticleInfoVO> complexSearchUserArticles(
        Long userId, Integer pageNum, Integer pageSize,
        Boolean isShared, ArticleStatus status, Set<Long> categoryIds,
        LocalDateTime startDate, LocalDateTime endDate, String keyword);

    /**
     * 搜索共享文章，支持日期范围筛选和关键字搜索
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param startDate 创建日期开始
     * @param endDate 创建日期结束
     * @param keyword 关键字
     * @return 文章信息VO列表
     */
    PageInfo<ArticleInfoVO> searchSharedArticles(
        Integer pageNum, Integer pageSize,
        LocalDateTime startDate, LocalDateTime endDate, String keyword);
}
