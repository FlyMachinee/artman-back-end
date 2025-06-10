package cn.edu.hit.artman.controller;

import cn.edu.hit.artman.common.result.Result;
import cn.edu.hit.artman.pojo.vo.ImageInfoVO;
import cn.edu.hit.artman.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "ImageController", description = "图片管理接口")
@RestController("ImageController")
@RequestMapping("/images")
@CrossOrigin
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @Operation(
        summary = "上传图片",
        description = "上传图片以在文章中使用，需要认证",
        responses = {
            @ApiResponse(responseCode = "201", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "文件类型或大小无效"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<ImageInfoVO> uploadImage(
        @RequestPart("file") MultipartFile file,
        @RequestParam(
            value = "name",
            required = false
        ) String name,
        @RequestHeader("X-User-Id") Long userId) {

        ImageInfoVO imageInfoVO = imageService.uploadImage(file, name, userId);
        return Result.created(imageInfoVO, "图片上传成功");
    }

    @Operation(
        summary = "获取用户图片",
        description = "获取已认证用户上传的图片列表，需要认证",
        responses = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @GetMapping
    public Result<List<ImageInfoVO>> getUserImages(
        @RequestHeader("X-User-Id") Long userId) { // 假设通过header获取用户ID

        List<ImageInfoVO> images = imageService.getUserImages(userId);
        return Result.ok(images, "获取用户图片列表成功");
    }

    @Operation(
        summary = "删除图片",
        description = "删除已上传的图片，用户只能删除自己的图片，需要认证",
        responses = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权删除他人图片"),
            @ApiResponse(responseCode = "404", description = "图片不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @DeleteMapping("/{imageId}")
    public Result<Object> deleteImage(
        @Parameter(
            name = "imageId",
            description = "要删除的图片ID",
            required = true,
            in = ParameterIn.PATH
        )
        @PathVariable("imageId") Long imageId,
        @RequestHeader("X-User-Id") Long userId) { // 假设通过header获取用户ID

        imageService.deleteImage(imageId, userId);
        return Result.ok("图片删除成功");
    }
}
