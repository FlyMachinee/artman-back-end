package cn.edu.hit.artman.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Schema(title = "评论创建DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateDTO implements Serializable {

    @Schema(title = "评论内容", example = "这是一条评论")
    @NotBlank(message = "评论内容不能为空")
    private String content;

    @Schema(
        title = "所回复的评论ID",
        description = """
            当回复一个二级评论时，replyId 为该二级评论的 ID
            否则 replyId 为 null""",
        example = "12345"
    )
    private Long replyId;

    @Schema(
        title = "所在的一级评论ID",
        description = """
            当该评论为一级评论时，rootId 为 null
            否则 rootId 为其所在的一级评论的 ID""",
        example = "54321"
    )
    private Long rootId;
}
