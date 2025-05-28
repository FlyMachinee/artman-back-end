package cn.edu.hit.artman.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Schema(title = "分类创建DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateDTO implements Serializable {

    @Schema(title = "分类名称", example = "编程语言")
    @NotBlank(message = "分类名称不能为空")
    private String name;

    @Schema(title = "父分类id", example = "0")
    @NotNull(message = "父分类ID不能为空，若为顶级分类，请填写0")
    private Long parentId;
}
