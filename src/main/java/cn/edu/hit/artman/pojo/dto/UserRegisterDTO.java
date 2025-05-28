package cn.edu.hit.artman.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Schema(title = "用户注册DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDTO implements Serializable {

    @Schema(title = "用户名", example = "user1")
    @NotEmpty(message = "用户名不能为空")
    private String username;

    @Schema(title = "邮箱", example = "1234@123.com")
    @NotEmpty(message = "邮箱不能为空")
    private String email;

    @Schema(title = "密码", example = "123456")
    @NotEmpty(message = "密码不能为空")
    private String password;
}
