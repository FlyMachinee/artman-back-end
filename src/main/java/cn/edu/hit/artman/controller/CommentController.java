package cn.edu.hit.artman.controller;

import cn.edu.hit.artman.common.result.Result;
import cn.edu.hit.artman.pojo.dto.CommentCreateDTO;
import cn.edu.hit.artman.pojo.po.Comment;
import cn.edu.hit.artman.pojo.vo.CommentInfoVO;
import cn.edu.hit.artman.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "CommentController", description = "评论管理接口")
@RestController("CommentController")
@RequestMapping("/articles")
@CrossOrigin
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(
        summary = "创建评论",
        description = """
            用户在文章下创建评论
            需要登录
            成功后返回新创建的评论ID
            """,
        responses = {
            @ApiResponse(responseCode = "201", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权访问"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @PostMapping("/{articleId}/comments")
    public Result<Long> createComment(
        @Parameter(
            name = "articleId",
            description = "评论的文章ID",
            required = true,
            in = ParameterIn.PATH
        )
        @PathVariable("articleId") Long articleId,
        @RequestBody @Valid CommentCreateDTO commentCreateDTO,
        @RequestHeader("X-User-Id") Long userId) {

        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setContent(commentCreateDTO.getContent());
        comment.setReplyId(commentCreateDTO.getReplyId());
        comment.setRootId(commentCreateDTO.getRootId());
        comment.setUserId(userId);

        return Result.created(commentService.createComment(comment), "评论创建成功");
    }

    @Operation(
        summary = "获取文章评论列表",
        description = "根据文章ID获取评论列表",
        responses = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "403", description = "无权访问"),
            @ApiResponse(responseCode = "404", description = "文章未找到"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @GetMapping("/{articleId}/comments")
    public Result<List<CommentInfoVO>> getCommentsByArticleId(
        @Parameter(
            name = "articleId",
            description = "文章ID",
            required = true,
            in = ParameterIn.PATH
        )
        @PathVariable("articleId") Long articleId) {

        List<CommentInfoVO> comments = commentService.getArticleComments(articleId);
        if (comments == null) {
            return Result.internalServerError("获取评论失败，内部错误");
        } else {
            return Result.ok(comments, "获取评论成功");
        }
    }

    @Operation(
        summary = "删除评论",
        description = "根据评论ID删除评论",
        responses = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权访问"),
            @ApiResponse(responseCode = "404", description = "文章/评论未找到"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @DeleteMapping("/{articleId}/comments/{commentId}")
    public Result<Object> deleteComment(
        @Parameter(
            name = "articleId",
            description = "文章ID",
            required = true,
            in = ParameterIn.PATH
        )
        @PathVariable("articleId") Long articleId,
        @Parameter(
            name = "commentId",
            description = "评论ID",
            required = true,
            in = ParameterIn.PATH
        )
        @PathVariable("commentId") Long commentId,
        @RequestHeader("X-User-Id") Long userId) {

        boolean isSuccess = commentService.deleteComment(articleId, commentId, userId);
        if (isSuccess) {
            return Result.ok("评论删除成功");
        } else {
            return Result.internalServerError("评论删除失败，内部错误");
        }
    }

}
