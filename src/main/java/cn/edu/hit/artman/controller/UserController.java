package cn.edu.hit.artman.controller;

import cn.edu.hit.artman.common.result.Result;
import cn.edu.hit.artman.pojo.dto.*;
import cn.edu.hit.artman.pojo.po.User;
import cn.edu.hit.artman.pojo.vo.UserInfoVO;
import cn.edu.hit.artman.pojo.vo.UserLoginVO;
import cn.edu.hit.artman.service.UserService;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "UserController", description = "用户接口")
@RestController("UserController")
@RequestMapping("/users")
@CrossOrigin
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
        summary = "用户注册",
        description = """
            用户注册接口，要求用户提供用户名、密码和邮箱
            用户名和邮箱必须唯一
            密码进行加密存储
        """,
        responses = {
            @ApiResponse(responseCode = "201", description = "注册成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @PostMapping("/register")
    public Result<Object> register(@RequestBody @Valid UserRegisterDTO userRegisterDTO) {
        boolean isSuccess = userService.register(
            userRegisterDTO.getUsername(),
            userRegisterDTO.getEmail(),
            userRegisterDTO.getPassword()
        );
        if (isSuccess) {
            return Result.created("注册成功");
        } else {
            return Result.internalServerError("插入表失败，原因未知");
        }
    }

    @Operation(
        summary = "用户以用户名登录",
        description = """
            用户登录接口，要求用户提供用户名和密码
            登录成功后返回用户信息和令牌
            用户名和密码进行验证
        """,
        responses = {
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @PostMapping("/login-by-name")
    public Result<UserLoginVO> loginByName(@RequestBody @Valid UserLoginByNameDTO userLoginByNameDTO) {
        UserLoginVO userLoginVO = userService.loginByName(
            userLoginByNameDTO.getUsername(),
            userLoginByNameDTO.getPassword()
        );
        return Result.ok(userLoginVO, "登录成功");
    }

    @Operation(
        summary = "用户以邮箱登录",
        description = """
            用户登录接口，要求用户提供邮箱和密码
            登录成功后返回用户信息和令牌
            邮箱和密码进行验证
        """,
        responses = {
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @PostMapping("/login-by-email")
    public Result<UserLoginVO> loginByEmail(@RequestBody @Valid UserLoginByEmailDTO userLoginByEmailDTO) {
        UserLoginVO userLoginVO = userService.loginByEmail(
            userLoginByEmailDTO.getEmail(),
            userLoginByEmailDTO.getPassword()
        );
        return Result.ok(userLoginVO, "登录成功");
    }

    @Operation(
        summary = "用户登出",
        description = """
            用户登出接口，要求用户提供令牌
            成功后销毁用户的刷新令牌
        """,
        responses = {
            @ApiResponse(responseCode = "200", description = "登出成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @PostMapping("/logout")
    public Result<Object> logout(@RequestHeader("X-User-Id") Long userId) {
        boolean isSuccess = userService.logout(userId);
        if (isSuccess) {
            return Result.ok("登出成功");
        } else {
            return Result.internalServerError("登出失败，原因未知");
        }
    }

    @Operation(
        summary = "用户更新密码",
        description = """
            用户更新密码接口，要求用户提供旧密码和新密码
            旧密码进行验证，新密码进行加密存储
            成功后返回更新成功信息
        """,
        responses = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @PutMapping("/password")
    public Result<Object> updatePassword(@RequestBody @Valid UserUpdatePasswordDTO userUpdatePasswordDTO,
                                         @RequestHeader("X-User-Id") Long userId) {
        boolean isSuccess = userService.updatePassword(
            userId,
            userUpdatePasswordDTO.getOldPassword(),
            userUpdatePasswordDTO.getNewPassword()
        );
        if (isSuccess) {
            return Result.ok("更新密码成功");
        } else {
            return Result.internalServerError("更新密码失败，原因未知");
        }
    }

    @Operation(
        summary = "获取用户信息",
        description = """
            获取用户信息接口，要求用户提供用户ID
            返回用户的基本信息，包括用户名、邮箱等
            用户ID必须存在""",
        responses = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "用户未找到"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @GetMapping("/info/{userId}")
    public Result<UserInfoVO> getUserInfo(
            @Parameter(
                name = "userId",
                description = "用户ID",
                required = true,
                example = "12345",
                in = ParameterIn.PATH
            )
            @PathVariable Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return Result.notFound("用户未找到");
        }

        UserInfoVO userInfoVO = BeanUtil.copyProperties(user, UserInfoVO.class);
        return Result.ok(userInfoVO, "获取用户信息成功");
    }

    @Operation(
        summary = "更新用户信息",
        description = """
            更新用户信息接口，要求用户提供新的用户名、邮箱等信息
            用户ID必须存在
            成功后返回更新成功信息
            用户名和邮箱必须唯一""",
        responses = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @PutMapping("/info")
    public Result<Object> updateUserInfo(@RequestBody UserUpdateInfoDTO userUpdateInfoDTO,
                                         @RequestHeader("X-User-Id") Long userId) {
        User user = BeanUtil.copyProperties(userUpdateInfoDTO, User.class);
        user.setUserId(userId);

        boolean isSuccess = userService.updateById(user);
        if (isSuccess) {
            return Result.ok("更新用户信息成功");
        } else {
            return Result.internalServerError("更新用户信息失败，原因未知");
        }
    }


}
