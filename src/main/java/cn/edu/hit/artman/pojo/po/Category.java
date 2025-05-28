package cn.edu.hit.artman.pojo.po;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分类信息表
 */
@TableName(value ="category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category implements Serializable {
    /**
     * 分类ID
     */
    @TableId(value = "category_id", type = IdType.AUTO)
    private Long categoryId;

    /**
     * 分类所属用户
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 分类名
     */
    @TableField(value = "name")
    private String name;

    /**
     * 父分类ID，NULL为顶级分类
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 分类创建时间
     */
    @TableField(
        value = "create_time",
        insertStrategy = FieldStrategy.NEVER,
        updateStrategy = FieldStrategy.NEVER
    )
    private LocalDateTime createTime;

    /**
     * 分类更新时间
     */
    @TableField(
        value = "update_time",
        insertStrategy = FieldStrategy.NEVER,
        updateStrategy = FieldStrategy.NEVER
    )
    private LocalDateTime updateTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}