package cn.edu.hit.artman.controller;

import cn.edu.hit.artman.common.result.Result;
import cn.edu.hit.artman.pojo.dto.ArticleComplexSearchDTO;
import cn.edu.hit.artman.pojo.dto.ArticleCreateDTO;
import cn.edu.hit.artman.pojo.dto.ArticleSimpleSearchDTO;
import cn.edu.hit.artman.pojo.dto.ArticleUpdateDTO;
import cn.edu.hit.artman.pojo.vo.ArticleInfoVO;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ArticleController", description = "文章接口")
@RestController("ArticleController")
@RequestMapping("/articles")
@CrossOrigin
@RequiredArgsConstructor
public class ArticleController {

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
    public Result<Long> createArticle(@RequestBody ArticleCreateDTO articleCreateDTO,
                                        @RequestHeader("X-User-Id") Long userId) {
        return Result.internalServerError("未实现");
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
        return Result.internalServerError("未实现");
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
        @RequestBody ArticleSimpleSearchDTO articleSimpleSearchDTO,
        @RequestHeader("X-User-Id") Long userId) {
        return Result.internalServerError("未实现");
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
        @RequestBody ArticleComplexSearchDTO articleComplexSearchDTO,
        @RequestHeader("X-User-Id") Long userId) {
        return Result.internalServerError("未实现");
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
        return Result.internalServerError("未实现");
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
        return Result.internalServerError("未实现");
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
    public Result<Object> deleteArticle(
        @Parameter(
            name = "articleId",
            description = "文章ID",
            required = true,
            in = ParameterIn.PATH
        )
        @PathVariable Long articleId,
        @RequestHeader("X-User-Id") Long userId) {
        return Result.internalServerError("未实现");
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
        @PathVariable Integer pageSize) {
        return Result.internalServerError("未实现");
    }

}
