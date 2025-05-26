package cn.edu.hit.artman.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Schema(title = "用户登录返回信息VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginVO implements Serializable {

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

    @Schema(title = "访问令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(title = "刷新令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;
}
