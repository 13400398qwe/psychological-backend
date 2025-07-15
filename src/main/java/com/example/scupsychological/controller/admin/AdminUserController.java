package com.example.scupsychological.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.common.Result;
import com.example.scupsychological.pojo.dto.UserCreateRequestDto;
import com.example.scupsychological.pojo.dto.UserUpdateRequestDto;
import com.example.scupsychological.pojo.vo.UserVO;
import com.example.scupsychological.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "后台管理模块 - 用户管理")
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final UserService userService; // 假设业务逻辑在 UserService 中

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')") // 关键：只有 ADMIN 角色才能调用此方法
    @Operation(summary = "创建新用户", description = "由管理员创建系统内部用户，如咨询师、助理等")
    @ApiResponse(responseCode = "201", description = "用户创建成功")
    public Result<UserVO> createUser(@Valid @RequestBody UserCreateRequestDto userCreateDto) {
        // 调用 Service 层来执行创建逻辑
        UserVO newUser = userService.createUser(userCreateDto);
        // 创建成功，返回包装好的 Result 对象
        // 也可以返回一个更符合 RESTful 的 ResponseEntity.created(...)
        return Result.success(newUser, "用户创建成功");
    }

    // 1. 使用 @DeleteMapping 来明确表示这是一个删除操作
    @DeleteMapping("/{id}")
    // 2. 修正了 @PreAuthorize 的表达式，去掉了多余的括号
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除用户", description = "管理员根据用户ID逻辑删除一个用户")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @SecurityRequirement(name = "bearerAuth")
    public Result<Object> deleteUser(
            // 3. 使用 @PathVariable 从 URL 路径中获取 ID，这是 RESTful 风格的最佳实践
            @Parameter(description = "要删除的用户ID", required = true) @PathVariable Long id
    ) {
        userService.deleteUser(id);
        // 4. 返回 Result<Void>，因为删除操作成功后无需返回任何数据体
        return Result.success("用户删除成功");
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole(('ADMIN'))")
    @Operation(summary = "更新用户信息", description = "管理员更新用户信息")
    @ApiResponse(responseCode = "200", description = "更新用户信息成功")
    public Result<UserVO> updateUser(@Parameter(description = "要更新的用户ID") @PathVariable Long id,
                                     @Valid @RequestBody UserUpdateRequestDto updateDto) {
        UserVO vo = userService.updateUser(id, updateDto);
        return Result.success(vo);
    }
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSISTANT')")
    @Operation(summary = "获取用户列表")
    @ApiResponse(responseCode = "200", description = "获取用户列表成功")
    public Result<List<UserVO>> listUsers() {
        List<UserVO> voList = userService.listUsers();
        return Result.success(voList);
    }
    @GetMapping("/pageQuery")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "分页获取用户列表")
    public Result<Page<UserVO>> pageQuery(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") long page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "按用户名模糊搜索") @RequestParam(required = false) String username
    ) {
        Page<UserVO> userPage = userService.listUsersByPage(page, size, username);
        return Result.success(userPage);
    }
    //根据id查询人的具体信息
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "根据ID查询用户信息")
    public Result<UserVO> getUserById(@Parameter(description = "用户ID") @PathVariable Long id) {
        UserVO userVO = userService.getUserById(id);
        return Result.success(userVO);
    }
}
