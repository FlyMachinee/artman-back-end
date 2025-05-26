package cn.edu.hit.artman.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Schema(title = "评论信息VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentInfoVO implements Serializable {

    @Schema(title = "一级评论ID")
    private Long commentId;

    @Schema(title = "发布评论的用户ID")
    private Long userId;

    @Schema(title = "发布评论的用户名")
    private String username;

    @Schema(title = "发布评论的用户昵称")
    private String nickname;

    @Schema(title = "发布评论的用户头像")
    private String avatar;

    @Schema(title = "评论内容")
    private String content;

    @Schema(title = "评论时间")
    private LocalDateTime createTime;

    @Schema(title = "拥有的二级评论列表")
    private List<SubCommentInfoVO> subComments;
}
