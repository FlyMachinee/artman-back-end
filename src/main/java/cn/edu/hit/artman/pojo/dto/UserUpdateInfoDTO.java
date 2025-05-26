package cn.edu.hit.artman.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Schema(title = "用户更新信息DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateInfoDTO implements Serializable {

    @Schema(title = "新昵称", example = "奶龙")
    private String nickname;

    @Schema(title = "新头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(title = "新个人简介", example = "这是我的个人简介")
    private String profile;
}
