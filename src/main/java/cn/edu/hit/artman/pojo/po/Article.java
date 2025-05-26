package cn.edu.hit.artman.pojo.po;

import cn.edu.hit.artman.common.enumeration.ArticleStatus;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章信息表
 */
@TableName(value ="article")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article implements Serializable {
    /**
     * 文章ID
     */
    @TableId(value = "article_id", type = IdType.AUTO)
    private Long articleId;

    /**
     * 文章作者ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 文章所属分类ID，NULL为无分类
     */
    @TableField(value = "category_id")
    private Long categoryId;

    /**
     * 文章标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 文章摘要
     */
    @TableField(value = "summary")
    private String summary;

    /**
     * 文章URL
     */
    @TableField(value = "content_url")
    private String contentUrl;

    /**
     * 文章是否共享
     */
    @TableField(value = "is_shared")
    private Boolean shared;

    /**
     * 文章状态：草稿、发布
     */
    @TableField(value = "status")
    private ArticleStatus status;

    /**
     * 文章创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 文章更新时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}