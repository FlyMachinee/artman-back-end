package cn.edu.hit.artman.controller;

import cn.edu.hit.artman.common.exception.ArtManException;
import cn.edu.hit.artman.common.result.Result;
import cn.edu.hit.artman.pojo.document.ArticleDocument;
import cn.edu.hit.artman.pojo.dto.*;
import cn.edu.hit.artman.pojo.po.Article;
import cn.edu.hit.artman.pojo.po.User;
import cn.edu.hit.artman.pojo.vo.ArticleInfoVO;
import cn.edu.hit.artman.service.ArticleService;
import cn.edu.hit.artman.service.ElasticSearchArticleService;
import cn.edu.hit.artman.service.OssService;
import cn.edu.hit.artman.service.UserService;
import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Set;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

@Tag(name = "ArticleController", description = "文章接口")
@RestController("ArticleController")
@RequestMapping("/articles")
@CrossOrigin
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final UserService userService;
    private final OssService ossService;
    private final ElasticSearchArticleService elasticSearchArticleService;

    @Operation(
        summary = "创建文章",
        description = """
            创建文章，要求用户已登录
            文章状态默认为草稿，isShared 默认为 false
            文章创建后返回文章 ID
            用户只能创建自己的文章，不能创建其他用户的文章
        """,
        responses = {
            @ApiResponse(responseCode = "201", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @PostMapping()
    @Transactional
    public Result<Long> createArticle(@RequestBody @Valid ArticleCreateDTO articleCreateDTO,
                                        @RequestHeader("X-User-Id") Long userId) {

        Long articleId = articleService.createArticleWithMetaInfo(userId, articleCreateDTO);
        String articleContent = articleCreateDTO.getContent();

        String objectName = ossService.generateObjectName(userId, articleId);
        String contentUrl = ossService.uploadFile(objectName, articleContent);

        Article updatedArticle = new Article();
        updatedArticle.setArticleId(articleId);
        updatedArticle.setContentUrl(contentUrl);
        if (!articleService.updateById(updatedArticle)) {
            throw new ArtManException(HTTP_INTERNAL_ERROR, "更新文章URL失败，未知数据库原因");
        }

        // 同步到 Elasticsearch
        Article article = articleService.getById(articleId);
        User user = userService.getById(userId);
        ArticleDocument articleDocument = BeanUtil.copyProperties(article, ArticleDocument.class);
        articleDocument.setId(articleId);
        articleDocument.setUsername(user.getUsername());
        articleDocument.setNickname(user.getNickname());
        articleDocument.setStatus(article.getStatus().getValue());
        articleDocument.setContent(articleContent);
        elasticSearchArticleService.syncArticle(articleDocument);


        return Result.created(articleId, "文章创建成功");
    }

    @Operation(
        summary = "获取用户所有文章",
        description = """
            获取用户所有文章，要求用户已登录
            支持分页查询，返回分页信息和文章列表
        """,
        responses = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @GetMapping("/all/{pageNum}/{pageSize}")
    public Result<PageInfo<ArticleInfoVO>> getAllArticles(
        @Parameter(
            name = "pageNum",
            description = "页码",
            required = true,
            in = ParameterIn.PATH
        )
        @PathVariable Integer pageNum,
        @Parameter(
            name = "pageSize",
            description = "每页大小",
            required = true,
            in = ParameterIn.PATH
        )
        @PathVariable Integer pageSize,
        @RequestHeader("X-User-Id") Long userId) {

        PageInfo<ArticleInfoVO> articles = articleService.getAllArticlesByUserId(userId, pageNum, pageSize);
        return Result.ok(articles, "获取用户所有文章成功");
    }

    @Operation(
        summary = "文章简单搜索",
        description = """
            简单搜索支持模糊查询标题和内容，要求用户已登录
            支持分页查询，返回分页信息和文章列表
            支持创建时间过滤，按创建时间降序
            支持按状态过滤（草稿、已发布）
            支持按共享状态过滤（共享、非共享）
        """,
        responses = {
            @ApiResponse(responseCode = "200", description = "搜索成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @GetMapping("/simple-search/{pageNum}/{pageSize}")
    public Result<PageInfo<ArticleInfoVO>> simpleSearch(
        @Parameter(
            name = "pageNum",
            description = "页码",
            required = true,
            in = ParameterIn.PATH
        )
        @PathVariable Integer pageNum,
        @Parameter(
            name = "pageSize",
            description = "每页大小",
            required = true,
            in = ParameterIn.PATH
        )
        @PathVariable Integer pageSize,
        ArticleSimpleSearchDTO articleSimpleSearchDTO,
        @RequestHeader("X-User-Id") Long userId) {

        PageInfo<ArticleInfoVO> articles = articleService.getSimpleSearchArticlesByUserId(
            userId, pageNum, pageSize, articleSimpleSearchDTO);

        return Result.ok(articles, "文章简单搜索成功");
    }

    @Operation(
        summary = "文章复杂搜索",
        description = """
            复杂搜索支持多条件组合查询，要求用户已登录
            支持分页查询，返回分页信息和文章列表
            支持创建时间过滤，按创建时间降序
            支持模糊搜索标题和内容
            支持按分类过滤
            支持按状态过滤（草稿、已发布）
            支持按共享状态过滤（共享、非共享）
        """,
        responses = {
            @ApiResponse(responseCode = "200", description = "搜索成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @GetMapping("/complex-search/{pageNum}/{pageSize}")
    public Result<Object> complexSearch(
        @Parameter(
            name = "pageNum",
            description = "页码",
            required = true,
            in = ParameterIn.PATH
        )
        @PathVariable Integer pageNum,
        @Parameter(
            name = "pageSize",
            description = "每页大小",
            required = true,
            in = ParameterIn.PATH
        )
        @PathVariable Integer pageSize,
        ArticleComplexSearchDTO articleComplexSearchDTO,
        @RequestHeader("X-User-Id") Long userId) {

        Long categoryId = articleComplexSearchDTO.getCategoryId();
        Set<Long> categoryIds = categoryId == null || categoryId == 0 ?
            null :
            Collections.singleton(categoryId);

        PageInfo<ArticleInfoVO> articles = elasticSearchArticleService.complexSearchUserArticles(
            userId,
            pageNum,
            pageSize,
            articleComplexSearchDTO.getIsShared(),
            articleComplexSearchDTO.getStatus(),
            categoryIds,
            articleComplexSearchDTO.getStartDate(),
            articleComplexSearchDTO.getEndDate(),
            articleComplexSearchDTO.getKeyword()
        );
        return Result.ok(articles, "获取用户文章列表成功");
    }

    @Operation(
        summary = "根据id获取文章详情",
        description = "根据id获取文章详情，要求用户已登录，只能查看自己的文章或发布的共享文章",
        responses = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "403", description = "无权访问"),
            @ApiResponse(responseCode = "404", description = "文章未找到"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @GetMapping("/{articleId}")
    public Result<ArticleInfoVO> getArticle(
        @Parameter(
            name = "articleId",
            description = "文章ID",
            required = true,
            in = ParameterIn.PATH
        )
        @PathVariable Long articleId,
        @RequestHeader("X-User-Id") Long userId) {

        ArticleInfoVO articleInfo = articleService.getArticleInfoById(articleId);

        if (articleInfo.getIsShared() || articleInfo.getUserId().equals(userId)) {
            return Result.ok(articleInfo, "获取文章详情成功");
        } else {
            return Result.forbidden("无权访问该文章");
        }
    }

    @Operation(
        summary = "更新文章",
        description = """
            更新文章信息，要求用户已登录且为文章所有者
            isShared 只能从 false 变为 true
            status 只能从 draft 变为 published""",
        responses = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "403", description = "无权访问"),
            @ApiResponse(responseCode = "404", description = "文章未找到"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @PutMapping("/{articleId}")
    @Transactional
    public Result<Object> updateArticle(
        @Parameter(
            name = "articleId",
            description = "文章ID",
            required = true,
            in = ParameterIn.PATH
        )
        @PathVariable Long articleId,
        @RequestBody ArticleUpdateDTO articleUpdateDTO,
        @RequestHeader("X-User-Id") Long userId) {

        if (!articleService.updateArticleWithMetaInfo(userId, articleId, articleUpdateDTO)) {
            throw new ArtManException(HTTP_INTERNAL_ERROR, "更新文章失败，未知数据库原因");
        }

        // 如果更新了内容，则需要重新上传到OSS
        if (articleUpdateDTO.getContent() != null) {
            String objectName = ossService.generateObjectName(userId, articleId);
            String contentUrl = ossService.uploadFile(objectName, articleUpdateDTO.getContent());

            Article updatedArticle = new Article();
            updatedArticle.setArticleId(articleId);
            updatedArticle.setContentUrl(contentUrl);
            if (!articleService.updateById(updatedArticle)) {
                throw new ArtManException(HTTP_INTERNAL_ERROR, "更新文章URL失败，未知数据库原因");
            }
        }

        // 同步到 Elasticsearch
        Article article = articleService.getById(articleId);
        User user = userService.getById(userId);
        ArticleDocument articleDocument = BeanUtil.copyProperties(article, ArticleDocument.class);
        articleDocument.setId(articleId);
        articleDocument.setUsername(user.getUsername());
        articleDocument.setNickname(user.getNickname());
        articleDocument.setStatus(article.getStatus().getValue());
        articleDocument.setContent(articleUpdateDTO.getContent());
        elasticSearchArticleService.syncArticle(articleDocument);

        return Result.ok("文章更新成功");
    }

    @Operation(
        summary = "删除文章",
        description = "删除文章，要求用户已登录且为文章所有者",
        responses = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "403", description = "无权访问"),
            @ApiResponse(responseCode = "404", description = "文章未找到"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @DeleteMapping("/{articleId}")
    @Transactional
    public Result<Object> deleteArticle(
        @Parameter(
            name = "articleId",
            description = "文章ID",
            required = true,
            in = ParameterIn.PATH
        )
        @PathVariable Long articleId,
        @RequestHeader("X-User-Id") Long userId) {

        // 删除元数据
        if (!articleService.deleteArticle(userId, articleId)) {
            throw new ArtManException(HTTP_INTERNAL_ERROR, "删除文章失败，未知数据库原因");
        }

        // 删除OSS中的内容
        String objectName = ossService.generateObjectName(userId, articleId);
        ossService.deleteFile(objectName);

        // 从 Elasticsearch 删除文章
        elasticSearchArticleService.deleteArticle(articleId);

        return Result.ok("文章删除成功");
    }

    @Operation(
        summary = "获取共享文章列表",
        description = """
            获取所有共享文章，支持分页查询
            只返回已发布的共享文章
            可按时间筛选
            可按关键字搜索标题和内容
        """,
        responses = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @GetMapping("/shared/{pageNum}/{pageSize}")
    public Result<Object> getSharedArticles(
        @Parameter(
            name = "pageNum",
            description = "页码",
            required = true,
            in = ParameterIn.PATH
        )
        @PathVariable Integer pageNum,
        @Parameter(
            name = "pageSize",
            description = "每页大小",
            required = true,
            in = ParameterIn.PATH
        )
        @PathVariable Integer pageSize,
        ArticleSharedSearchDTO articleSharedSearchDTO) {

        String keyword = articleSharedSearchDTO.getKeyword();
        PageInfo<ArticleInfoVO> articles;

        if (keyword == null || keyword.trim().isEmpty()) {
            // 如果 keyword 为 null 或空，使用数据库的简单搜索
            // 注意：这里调用的是 ArticleService 的数据库查询方法
            articles = articleService.getSimpleSearchSharedArticles(
                pageNum,
                pageSize,
                articleSharedSearchDTO.getStartDate(),
                articleSharedSearchDTO.getEndDate());
        } else {
            // 如果 keyword 不为空，使用 Elasticsearch 搜索
            articles = elasticSearchArticleService.searchSharedArticles(
                pageNum,
                pageSize,
                articleSharedSearchDTO.getStartDate(),
                articleSharedSearchDTO.getEndDate(),
                keyword);
        }
        return Result.ok(articles, "获取共享文章列表成功");
    }

}
