package cn.edu.hit.artman.pojo.vo;

import cn.edu.hit.artman.common.enumeration.ArticleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(title = "文章信息VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleInfoVO implements Serializable {

    @Schema(title = "文章ID", example = "12345")
    private Long articleId;

    @Schema(title = "作者ID", example = "67890")
    private Long userId;

    @Schema(title = "作者用户名", example = "john_doe")
    private String username;

    @Schema(title = "作者昵称", example = "奶龙")
    private String nickname;

    @Schema(title = "文章所属分类ID", example = "5")
    private Long categoryId;

    @Schema(title = "文章标题", example = "如何使用Spring Boot")
    private String title;

    @Schema(title = "文章摘要", example = "本文将介绍如何使用Spring Boot进行开发")
    private String summary;

    @Schema(title = "文章内容URL", example = "https://example.com/article/12345/content")
    private String contentUrl;

    @Schema(title = "是否为共享文章", example = "true")
    private Boolean isShared;

    @Schema(title = "文章状态", example = "draft")
    private ArticleStatus status;

    @Schema(title = "创建时间", example = "2023-01-01T00:00:00")
    private LocalDateTime createTime;

    @Schema(title = "更新时间", example = "2023-01-02T12:00:00")
    private LocalDateTime updateTime;
}
