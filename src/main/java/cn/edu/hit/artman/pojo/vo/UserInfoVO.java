package cn.edu.hit.artman.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(title = "用户信息VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVO implements Serializable {

    @Schema(title = "用户id", example = "1")
    private Long userId;

    @Schema(title = "用户名", example = "john_doe")
    private String username;

    @Schema(title = "邮箱", example = "555@jj.com")
    private String email;

    @Schema(title = "昵称", example = "奶龙")
    private String nickname;

    @Schema(title = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(title = "个人简介", example = "这是我的个人简介")
    private String profile;

    @Schema(title = "用户创建时间", example = "2023-01-01T12:00:00")
    private LocalDateTime createTime;

    @Schema(title = "用户更新时间", example = "2023-01-02T12:00:00")
    private LocalDateTime updateTime;
}
