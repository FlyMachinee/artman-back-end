package cn.edu.hit.artman.service.impl;

import cn.edu.hit.artman.common.enumeration.ArticleStatus;
import cn.edu.hit.artman.mapper.ArticleMapper;
import cn.edu.hit.artman.mapper.CategoryMapper; // 引入 CategoryMapper
import cn.edu.hit.artman.mapper.UserMapper;
import cn.edu.hit.artman.pojo.document.ArticleDocument;
import cn.edu.hit.artman.pojo.po.Article;
import cn.edu.hit.artman.pojo.po.Category; // 引入 Category POJO
import cn.edu.hit.artman.pojo.po.User;
import cn.edu.hit.artman.pojo.vo.ArticleInfoVO;
import cn.edu.hit.artman.repository.ArticleDocumentRepository;
import cn.edu.hit.artman.service.ElasticSearchArticleService;
import cn.edu.hit.artman.service.OssService;
import co.elastic.clients.json.JsonData;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.stereotype.Service;

// New Elasticsearch Java API Client imports for query building and sort
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOptionsBuilders;
import co.elastic.clients.elasticsearch._types.FieldValue; // 解决 FieldValue 问题

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticSearchArticleServiceImpl implements ElasticSearchArticleService {

    private final ArticleDocumentRepository articleDocumentRepository;
    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper; // 注入 CategoryMapper
    private final UserMapper userMapper; // 注入 UserMapper，用于获取用户信息
    private final ElasticsearchOperations elasticsearchOperations;
    private final OssService ossService;

    @Override
    public void syncArticle(Article article) {
        ArticleDocument document = new ArticleDocument();
        BeanUtils.copyProperties(article, document);
        document.setId(article.getArticleId());

        if (article.getContentUrl() != null) {
            String content = ossService.getContentFromUrl(article.getContentUrl());
            if (content == null) {
                log.warn("Content URL is null or content not found for article ID: {}", article.getArticleId());
                document.setContent(""); // 如果内容获取失败，设置为空字符串
            } else {
                document.setContent(content); // 从OSS获取内容
            }
        } else {
            document.setContent(""); // 如果没有 contentUrl，内容也为空
        }

        articleDocumentRepository.save(document);
        log.info("Article synced to Elasticsearch: ID={}", article.getArticleId());
    }

    @Override
    public void syncArticle(ArticleDocument articleDocument) {
        // 直接保存 ArticleDocument 对象
        articleDocumentRepository.save(articleDocument);
        log.info("ArticleDocument synced to Elasticsearch: ID={}", articleDocument.getId());
    }

    @Override
    public void deleteArticle(Long articleId) {
        articleDocumentRepository.deleteById(articleId);
        log.info("Article deleted from Elasticsearch: ID={}", articleId);
    }

    @Override
    public void syncAllArticlesFromDatabase() {
        log.info("Starting full synchronization of all articles from database to Elasticsearch...");
        List<Article> allArticles = articleMapper.selectList(null); // 获取所有文章
        List<ArticleDocument> documents = allArticles.stream().map(article -> {
            ArticleDocument document = new ArticleDocument();
            BeanUtils.copyProperties(article, document);
            document.setId(article.getArticleId());
            User user = userMapper.selectById(article.getUserId());
            document.setUsername(user.getUsername());
            document.setNickname(user.getNickname());
            document.setStatus(article.getStatus().getValue());
            // 同步内容，与 syncArticle 方法保持一致，根据实际业务逻辑处理
            if (article.getContentUrl() != null) {
                // 这里也需要根据您的OSSService来获取实际内容
                String content = ossService.getContentFromUrl(article.getContentUrl());
                if (content != null) {
                    document.setContent(content);
                } else {
                    log.warn("Content URL is null or content not found for article ID: {}", article.getArticleId());
                    document.setContent(""); // 如果内容获取失败，设置为空字符串
                }
            } else {
                document.setContent("");
            }
            return document;
        }).collect(Collectors.toList());
        articleDocumentRepository.saveAll(documents);
        log.info("Full synchronization completed. Synced {} articles to Elasticsearch.", documents.size());
    }

    /**
     * 辅助方法：递归获取某个分类及其所有子分类的ID
     * @param categoryId 根分类ID
     * @param allCategories 所有分类列表
     * @param resultIds 存储结果的Set
     */
    private void collectCategoryAndChildrenIds(Long categoryId, List<Category> allCategories, Set<Long> resultIds) {
        if (categoryId == null) return;
        resultIds.add(categoryId);
        for (Category category : allCategories) {
            if (categoryId.equals(category.getParentId())) {
                collectCategoryAndChildrenIds(category.getCategoryId(), allCategories, resultIds);
            }
        }
    }

    @Override
    public PageInfo<ArticleInfoVO> complexSearchUserArticles(
        Long userId, Integer pageNum, Integer pageSize,
        Boolean isShared, ArticleStatus status, Set<Long> categoryIds,
        LocalDateTime startDate, LocalDateTime endDate, String keyword) {

        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        // 必须匹配用户ID
        boolQueryBuilder.must(QueryBuilders.term(t -> t.field("userId").value(userId)));

        // 可选：isShared
        if (isShared != null) {
            boolQueryBuilder.must(QueryBuilders.term(t -> t.field("isShared").value(isShared)));
        }

        // 可选：status
        if (status != null) {
            // 注意：Elasticsearch中的status字段被映射为Keyword类型，所以直接使用其名称进行精确匹配
            boolQueryBuilder.must(QueryBuilders.term(t -> t.field("status").value(status.name())));
        }

        // 可选：categoryId (支持多个，包含子分类)
        if (categoryIds != null && !categoryIds.isEmpty()) {
            Set<Long> allRelatedCategoryIds = new HashSet<>();
            List<Category> allCategories = categoryMapper.selectList(null); // 获取所有分类
            for (Long cId : categoryIds) {
                collectCategoryAndChildrenIds(cId, allCategories, allRelatedCategoryIds);
            }
            // 使用 terms 查询来匹配多个分类ID
            boolQueryBuilder.must(QueryBuilders.terms(t -> t.field("categoryId").terms(ts -> ts.value(
                allRelatedCategoryIds.stream().map(FieldValue::of).collect(Collectors.toList())
            ))));
        }

        // 可选：创建日期范围筛选 - 修复方法调用问题
        if (startDate != null || endDate != null) {
            boolQueryBuilder.must(QueryBuilders.range(r -> {
                r.field("createTime"); // 字段名
                if (startDate != null) {
                    r.gte(JsonData.of(startDate.toString()));
                }
                if (endDate != null) {
                    r.lte(JsonData.of(endDate.toString()));
                }
                return r; // 返回 builder 自身
            }));
        }

        // 可选：关键字搜索（对标题、摘要与内容同时搜索）
        if (keyword != null && !keyword.trim().isEmpty()) {
            boolQueryBuilder.must(QueryBuilders.multiMatch(m -> m.fields("title", "summary", "content").query(keyword)));
        }

        // 分页
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);

        // 构建排序
        SortOptions sortOptions = SortOptionsBuilders.field(f -> f.field("createTime").order(SortOrder.Desc));

        // 构建 NativeQuery
        NativeQuery searchQuery = new NativeQueryBuilder()
            .withQuery(boolQueryBuilder.build()._toQuery()) // 将 BoolQuery.Builder 转换为 Query
            .withPageable(pageable)
            .withSort(sortOptions)
            .build();

        SearchHits<ArticleDocument> searchHits = elasticsearchOperations.search(searchQuery, ArticleDocument.class);

        List<ArticleInfoVO> articleInfoVOS = searchHits.stream().map(searchHit -> {
            ArticleInfoVO vo = new ArticleInfoVO();
            ArticleDocument document = searchHit.getContent();
            BeanUtils.copyProperties(document, vo);
            vo.setArticleId(document.getId());
            vo.setStatus(ArticleStatus.fromValue(document.getStatus())); // 将字符串状态转换为 ArticleStatus 枚举
            return vo;
        }).collect(Collectors.toList());

        PageInfo<ArticleInfoVO> pageInfo = new PageInfo<>(articleInfoVOS);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(searchHits.getTotalHits());
        pageInfo.setPages((int) Math.ceil((double) searchHits.getTotalHits() / pageSize));
        return pageInfo;
    }


    @Override
    public PageInfo<ArticleInfoVO> searchSharedArticles(
        Integer pageNum, Integer pageSize,
        LocalDateTime startDate, LocalDateTime endDate, String keyword) {

        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        // 必须是共享且已发布的文章
        boolQueryBuilder.must(QueryBuilders.term(t -> t.field("isShared").value(true)));
        boolQueryBuilder.must(QueryBuilders.term(t -> t.field("status").value(ArticleStatus.PUBLISHED.getValue())));

        // 可选：创建日期范围筛选
        if (startDate != null || endDate != null) {
            boolQueryBuilder.must(QueryBuilders.range(r -> {
                r.field("createTime"); // 字段名
                if (startDate != null) {
                    r.gte(JsonData.of(startDate.toString()));
                }
                if (endDate != null) {
                    r.lte(JsonData.of(endDate.toString()));
                }
                return r; // 返回 builder 自身
            }));
        }

        // 可选：关键字搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            boolQueryBuilder.must(QueryBuilders.multiMatch(m -> m.fields("title", "summary", "content").query(keyword)));
        }

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);

        SortOptions sortOptions = SortOptionsBuilders.field(f -> f.field("createTime").order(SortOrder.Desc));

        NativeQuery searchQuery = new NativeQueryBuilder()
            .withQuery(boolQueryBuilder.build()._toQuery())
            .withPageable(pageable)
            .withSort(sortOptions)
            .build();

        SearchHits<ArticleDocument> searchHits = elasticsearchOperations.search(searchQuery, ArticleDocument.class);

        List<ArticleInfoVO> articleInfoVOS = searchHits.stream().map(searchHit -> {
            ArticleInfoVO vo = new ArticleInfoVO();
            ArticleDocument document = searchHit.getContent();
            BeanUtils.copyProperties(document, vo);
            vo.setArticleId(document.getId());
            vo.setStatus(ArticleStatus.fromValue(document.getStatus())); // 将字符串状态转换为 ArticleStatus 枚举
            return vo;
        }).collect(Collectors.toList());

        PageInfo<ArticleInfoVO> pageInfo = new PageInfo<>(articleInfoVOS);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(searchHits.getTotalHits());
        pageInfo.setPages((int) Math.ceil((double) searchHits.getTotalHits() / pageSize));
        return pageInfo;
    }
}
