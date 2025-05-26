package cn.edu.hit.artman.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Schema(title = "分类更新DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryUpdateDTO implements Serializable {

    @Schema(title = "新分类名称", example = "编程语言")
    private String name;

    @Schema(
        title = "新父分类id",
        description = """
          设置 parentId 为 0 表示置为顶级层级
          设置 parentId 为 >0 值表示置为某一层级之下
          设置 parentId 为 null 则表示不修改层级""",
        example = "null"
    )
    private Long parentId;
}
