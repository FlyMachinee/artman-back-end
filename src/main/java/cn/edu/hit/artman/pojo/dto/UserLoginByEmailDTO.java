package cn.edu.hit.artman.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Schema(title = "用户以邮箱登录DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginByEmailDTO implements Serializable {

    @Schema(title = "邮箱", example = "555@555.com")
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @Schema(title = "密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;
}
