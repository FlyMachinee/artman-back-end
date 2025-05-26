package cn.edu.hit.artman.pojo.dto;

import cn.edu.hit.artman.common.enumeration.ArticleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(title = "文章复杂搜索DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleComplexSearchDTO implements Serializable {

    @Schema(title = "文章是否为共享", example = "true")
    private Boolean isShared;

    @Schema(title = "文章状态", example = "draft")
    private ArticleStatus status;

    @Schema(title = "文章所属分类id (或其子分类)", example = "5")
    private Long categoryId;

    @Schema(title = "搜索开始日期", example = "2023-01-01T00:00:00")
    private LocalDateTime startDate;

    @Schema(title = "搜索结束日期", example = "2023-12-31T23:59:59")
    private LocalDateTime endDate;

    @Schema(title = "搜索关键字", example = "Spring Boot")
    private String keyword;
}
