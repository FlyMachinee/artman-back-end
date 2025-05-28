package cn.edu.hit.artman.controller;

import cn.edu.hit.artman.common.result.Result;
import cn.edu.hit.artman.pojo.dto.CategoryCreateDTO;
import cn.edu.hit.artman.pojo.dto.CategoryUpdateDTO;
import cn.edu.hit.artman.pojo.vo.CategoryInfoVO;
import cn.edu.hit.artman.pojo.vo.CategoryTreeEntryVO;
import cn.edu.hit.artman.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "CategoryController", description = "分类接口")
@RestController("CategoryController")
@RequestMapping("/categories")
@CrossOrigin
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
        summary = "创建分类",
        description = """
            用户创建分类
            分类名称不能重复且不能为空
            需要登录
            成功后返回新创建的分类ID""",
        responses = {
            @ApiResponse(responseCode = "201", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @PostMapping()
    public Result<Long> createCategory(@RequestBody @Valid CategoryCreateDTO categoryCreateDTO,
                                       @RequestHeader("X-User-Id") Long userId) {
        Long categoryId = categoryService.createCategory(
            userId,
            categoryCreateDTO.getName(),
            categoryCreateDTO.getParentId()
        );

        if (categoryId != null) {
            return Result.created(categoryId, "分类创建成功");
        } else {
            return Result.internalServerError("创建分类失败，原因未知");
        }
    }

    @Operation(
        summary = "获取分类信息",
        description = "根据分类ID获取分类信息",
        responses = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权访问"),
            @ApiResponse(responseCode = "404", description = "分类未找到"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @GetMapping("/{categoryId}")
    public Result<CategoryInfoVO> getCategoryInfo(
        @Parameter(
            name = "categoryId",
            description = "分类ID",
            required = true,
            in = ParameterIn.PATH
        )
        @PathVariable Long categoryId,
        @RequestHeader("X-User-Id") Long userId) {

        CategoryInfoVO categoryInfoVO = categoryService.getCategoryInfoById(categoryId, userId);
        return Result.ok(categoryInfoVO, "获取分类信息成功");
    }

    @Operation(
        summary = "获取分类树",
        description = "获取指定分类ID下的分类树",
        responses = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "404", description = "分类未找到"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @GetMapping("/tree/{categoryId}")
    public Result<List<CategoryTreeEntryVO>> getCategoryTree(
        @Parameter(
            name = "categoryId",
            description = """
                分类ID
                categoryId为0时，表示返回该用户的所有分类（树状）
                否则，返回以categoryId为根的分类树""",
            required = true,
            in = ParameterIn.PATH
        )
        @PathVariable Long categoryId,
        @RequestHeader("X-User-Id") Long userId) {

        if (categoryId == 0) {
            List<CategoryTreeEntryVO> categoryTree = categoryService.getCategoryWholeTree(userId);
            return Result.ok(categoryTree, "获取分类树成功");
        } else {
            CategoryTreeEntryVO categoryTree = categoryService.getCategoryTree(categoryId, userId);
            return Result.ok(List.of(categoryTree), "获取分类树成功");
        }
    }

    @Operation(
        summary = "更新分类",
        description = """
            更新指定分类的信息
            需要登录，只能更新自己的分类
            分类名称不能重复且不能为空
            不能出现循环引用
            成功后返回更新后的分类信息""",
        responses = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权访问"),
            @ApiResponse(responseCode = "404", description = "分类未找到"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @PutMapping("/{categoryId}")
    public Result<Object> updateCategory(
        @Parameter(
            name = "categoryId",
            description = "要更新的分类ID",
            required = true,
            in = ParameterIn.PATH
        )
        @PathVariable Long categoryId,
        @RequestBody CategoryUpdateDTO categoryUpdateDTO,
        @RequestHeader("X-User-Id") Long userId) {

        boolean isSuccess = categoryService.updateCategory(
            categoryId,
            userId,
            categoryUpdateDTO.getName(),
            categoryUpdateDTO.getParentId()
        );
        if (isSuccess) {
            return Result.ok("更新分类成功");
        } else {
            return Result.internalServerError("更新分类失败，原因未知");
        }
    }

    @Operation(
        summary = "删除分类",
        description = """
            删除指定分类及其子分类
            需要登录，只能删除自己的分类
            删除分类后，所有该分类下的文章将被移动至顶级分类""",
        responses = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权访问"),
            @ApiResponse(responseCode = "404", description = "分类未找到"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @DeleteMapping("/{categoryId}")
    public Result<Object> deleteCategory(
        @Parameter(
            name = "categoryId",
            description = "要删除的分类ID",
            required = true,
            in = ParameterIn.PATH
        )
        @PathVariable Long categoryId,
        @RequestHeader("X-User-Id") Long userId) {

        boolean isSuccess = categoryService.deleteCategory(categoryId, userId);
        if (isSuccess) {
            return Result.ok("删除分类成功");
        } else {
            return Result.internalServerError("删除分类失败，原因未知");
        }
    }

    @Operation(
        summary = "获取分类到根节点的路径",
        description = """
            获取指定分类的路径信息
            包括该分类及其所有父分类
            需要登录，只能获取自己的分类路径
            返回的列表从当前分类到根分类""",
        responses = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "403", description = "无权访问"),
            @ApiResponse(responseCode = "404", description = "分类未找到"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
        }
    )
    @GetMapping("/path/{categoryId}")
    public Result<List<CategoryInfoVO>> getCategoryPath(
        @Parameter(
            name = "categoryId",
            description = "分类ID",
            required = true,
            in = ParameterIn.PATH
        )
        @PathVariable Long categoryId,
        @RequestHeader("X-User-Id") Long userId) {

        List<CategoryInfoVO> categoryPath = categoryService.getCategoryPath(categoryId, userId);
        return Result.ok(categoryPath, "获取分类路径成功");
    }
}
