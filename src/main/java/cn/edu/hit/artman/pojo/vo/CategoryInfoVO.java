package cn.edu.hit.artman.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "分类信息VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryInfoVO implements Serializable {

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "分类创建用户ID", example = "12345")
    private Long userId;

    @Schema(description = "分类名称", example = "技术")
    private String name;

    @Schema(description = "父分类ID", example = "null")
    private Long parentId;

    @Schema(description = "创建时间", example = "2023-10-01T12:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2023-10-01T12:00:00")
    private LocalDateTime updateTime;
}
