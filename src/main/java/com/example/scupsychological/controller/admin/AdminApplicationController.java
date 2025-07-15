package com.example.scupsychological.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.common.Result;
import com.example.scupsychological.pojo.dto.AdminApplicationCreateDto;
import com.example.scupsychological.pojo.dto.AdminApplicationUpdateDto;
import com.example.scupsychological.pojo.dto.ApplicationAdminQueryDto;
import com.example.scupsychological.pojo.dto.ApplicationReviewDto;
import com.example.scupsychological.pojo.vo.ApplicationDetailVO;
import com.example.scupsychological.pojo.vo.ApplicationListVO;
import com.example.scupsychological.service.InitialVisitApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台管理模块 - 初访申请管理")
@RestController
@RequestMapping("/api/admin/initial-visit-applications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminApplicationController {

    private final InitialVisitApplicationService applicationService;


    /**
     * 审核一个指定的初访申请
     * @param id 申请记录的ID
     * @param reviewDto 包含审核决策的DTO
     * @return 更新后的申请视图对象
     */
    @PutMapping("/{id}/review")
    @Operation(summary = "审核指定的初访申请 (批准/拒绝)")
    public Result<ApplicationDetailVO> reviewApplication(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationReviewDto reviewDto) {

        ApplicationDetailVO updatedApplication = applicationService.reviewApplication(id, reviewDto);
        return Result.success(updatedApplication, "审核操作成功");
    }
    @GetMapping
    @Operation(summary = "分页、筛选获取所有初访申请")
    public Result<Page<ApplicationListVO>> listAllApplications(ApplicationAdminQueryDto queryDto) {
        // 1. 将封装了所有查询参数的 DTO 对象，直接传递给 Service 层
        Page<ApplicationListVO> resultPage = applicationService.listAllApplications(queryDto);

        // 2. 将 Service 返回的分页结果包装成统一响应格式并返回
        return Result.success(resultPage);
    }

    @PostMapping
    @Operation(summary = "管理员为学生新增预约")
    public Result<ApplicationDetailVO> createApplicationForStudent(@Valid @RequestBody AdminApplicationCreateDto createDto) {
        ApplicationDetailVO newApp = applicationService.createApplicationForStudent(createDto);
        return Result.success(newApp, "预约创建成功");
    }

    @PutMapping("/{id}")
    @Operation(summary = "管理员改约（老师/时间/地点）")
    public Result<ApplicationDetailVO> updateApplication(@PathVariable Long id, @Valid @RequestBody AdminApplicationUpdateDto updateDto) {
        ApplicationDetailVO updatedApp = applicationService.updateApplication(id, updateDto);
        return Result.success(updatedApp, "改约成功");
    }
    @PostMapping("/{id}/cancel")
    @Operation(summary = "管理员取消初访预约")
    public Result<Object> cancelApplication(
            @PathVariable Long id) {

        applicationService.cancelApplicationByAdmin(id);

        return Result.success("预约已成功取消");
    }

    // ... 其他接口，如获取申请列表 ...
}
