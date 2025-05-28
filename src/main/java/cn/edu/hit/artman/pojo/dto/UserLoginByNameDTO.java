package cn.edu.hit.artman.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Schema(title = "用户以用户名登录DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginByNameDTO implements Serializable {

    @Schema(title = "用户名", example = "user1")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(title = "密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;
}
