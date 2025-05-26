package cn.edu.hit.artman.pojo.dto;

import cn.edu.hit.artman.common.enumeration.ArticleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Schema(title = "文章更新DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleUpdateDTO implements Serializable {

    @Schema(title = "文章标题", example = "如何使用Spring Boot")
    private String title;

    @Schema(title = "文章摘要", example = "本文将介绍如何使用Spring Boot进行开发")
    private String summary;

    @Schema(title = "文章内容", example = "Spring Boot是一个开源的Java框架...")
    private String content;

    @Schema(title = "文章所属分类id", example = "5")
    private Long categoryId;

    @Schema(title = "文章是否为共享", example = "true")
    private Boolean isShared;

    @Schema(title = "文章状态", example = "draft")
    private ArticleStatus status;
}
