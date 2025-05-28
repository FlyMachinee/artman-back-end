package cn.edu.hit.artman.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Schema(title = "用户修改密码DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatePasswordDTO implements Serializable {

    @Schema(title = "旧密码", example = "123456")
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @Schema(title = "新密码", example = "654321")
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
