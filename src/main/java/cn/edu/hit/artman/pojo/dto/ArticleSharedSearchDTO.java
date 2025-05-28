package cn.edu.hit.artman.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(title = "文章共享搜索DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleSharedSearchDTO implements Serializable {

    @Schema(title = "搜索开始日期", example = "2023-01-01T00:00:00")
    private LocalDateTime startDate;

    @Schema(title = "搜索结束日期", example = "2023-12-31T23:59:59")
    private LocalDateTime endDate;

    @Schema(title = "搜索关键词", example = "Spring Boot")
    private String keyword;
}
