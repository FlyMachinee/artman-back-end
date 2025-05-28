package cn.edu.hit.artman.pojo.po;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息表
 */
@TableName(value ="user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 用户名，非空唯一
     */
    @TableField(value = "username")
    private String username;

    /**
     * 用户邮箱，非空唯一
     */
    @TableField(value = "email")
    private String email;

    /**
     * 用户密码哈希值
     */
    @TableField(value = "password_hash")
    private String passwordHash;

    /**
     * 用户昵称
     */
    @TableField(value = "nickname")
    private String nickname;

    /**
     * 用户头像URL
     */
    @TableField(value = "avatar")
    private String avatar;

    /**
     * 用户个人简介
     */
    @TableField(value = "profile")
    private String profile;

    /**
     * 用户创建时间
     */
    @TableField(
        value = "create_time",
        insertStrategy = FieldStrategy.NEVER,
        updateStrategy = FieldStrategy.NEVER
    )
    private LocalDateTime createTime;

    /**
     * 用户更新时间
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