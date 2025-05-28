package cn.edu.hit.artman.pojo.po;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评论信息表
 */
@TableName(value ="comment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment implements Serializable {
    /**
     * 评论ID
     */
    @TableId(value = "comment_id", type = IdType.AUTO)
    private Long commentId;

    /**
     * 评论所属文章ID
     */
    @TableField(value = "article_id")
    private Long articleId;

    /**
     * 评论用户ID，NULL表示该用户已注销
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 评论内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 回复评论的ID，NULL表示该评论没有回复某个评论（即：1级评论或回复1级评论的评论）
     */
    @TableField(value = "reply_id")
    private Long replyId;

    /**
     * 评论所属1级评论的ID，NULL表示该评论为1级评论
     */
    @TableField(value = "root_id")
    private Long rootId;

    /**
     * 
     */
    @TableField(
        value = "create_time",
        insertStrategy = FieldStrategy.NEVER
    )
    private LocalDateTime createTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}