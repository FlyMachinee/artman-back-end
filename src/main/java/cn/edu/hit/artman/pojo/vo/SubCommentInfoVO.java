package cn.edu.hit.artman.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(title = "二级评论信息VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubCommentInfoVO implements Serializable {

    @Schema(title = "二级评论ID")
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

    @Schema(title = "被回复的用户ID")
    private Long replyUserId;

    @Schema(title = "被回复的用户名")
    private String replyUsername;

    @Schema(title = "被回复的用户昵称")
    private String replyNickname;
}
