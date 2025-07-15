package com.example.scupsychological.controller.student;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.common.Result;
import com.example.scupsychological.pojo.dto.*;
import com.example.scupsychological.pojo.vo.ApplicationListVO;
import com.example.scupsychological.pojo.vo.ScheduleSlotVO;
import com.example.scupsychological.security.LoginUser;
import com.example.scupsychological.service.InitialVisitApplicationService;
import com.example.scupsychological.service.QuestionnaireService;
import com.example.scupsychological.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Tag(name = "学生模块 - 初访")
@RestController
@RequestMapping("/api/student/initialVisit")
public class InitialVisitController {

    private final ScheduleService slotService;
    private final InitialVisitApplicationService applicationService;
    private final QuestionnaireService questionnaireService;

    @GetMapping("/available")
    @Operation(summary = "获取可预约的号源列表 (供学生选择)")
    public Result<List<ScheduleSlotVO>> listAvailableSlots(@Valid AvailableSlotQueryDto queryDto) {
        List<ScheduleSlotVO> availableSlots = slotService.findAvailableSlots(queryDto);
        return Result.success(availableSlots);
    }

    @PostMapping("/submit")
    @Operation(summary = "学生提交完整的初访申请")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<Object> createApplication(
            @Valid @RequestBody StudentApplicationCreateDto createDto,
            @AuthenticationPrincipal LoginUser loginUser) {

        applicationService.createApplication(loginUser.getUserId(), createDto);
        return Result.success("您的申请已成功提交，请等待管理员审核。");
    }

    /**
     * 分页、筛选获取所有初访申请列表
     *
     * @return 包含分页信息和申请列表的 Result 对象
     */
    @GetMapping
    @Operation(summary = "分页、筛选获取自己的初访申请")
    public Result<Page<ApplicationListVO>> listAllApplications(@Parameter(description = "页码") @RequestParam(defaultValue = "1") long page,
                                                               @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") long size,
                                                               @AuthenticationPrincipal LoginUser loginUser) {
        Page<ApplicationListVO> pageResult =applicationService.listMyApplications(loginUser.getUserId(),page,size);
        return Result.success(pageResult);

    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "撤销我的一条预约申请")
    public Result<Object> cancelMyApplication(@PathVariable Long id, @AuthenticationPrincipal LoginUser loginUser) {
        applicationService.cancelApplication(loginUser.getUserId(), id);
        return Result.success("申请已成功撤销");
    }
}
