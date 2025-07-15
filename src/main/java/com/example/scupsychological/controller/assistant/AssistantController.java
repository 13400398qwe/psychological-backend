package com.example.scupsychological.controller.assistant;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.common.Result;
import com.example.scupsychological.pojo.dto.AvailableSlotQueryDto;
import com.example.scupsychological.pojo.dto.CaseAssistantQueryDto;
import com.example.scupsychological.pojo.dto.CaseScheduleDto;
import com.example.scupsychological.pojo.entity.CounselingCases;
import com.example.scupsychological.pojo.vo.CaseAssistantListVO;
import com.example.scupsychological.pojo.vo.ScheduleSlotVO;
import com.example.scupsychological.security.LoginUser;
import com.example.scupsychological.service.CounselingCaseService;
import com.example.scupsychological.pojo.vo.PendingCaseVO;
import com.example.scupsychological.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "心理助理模块")
@RestController
@RequestMapping("/api/assistant")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ASSISTANT')")
public class AssistantController {

    private final CounselingCaseService caseService;
    private final ScheduleService slotService;

    @GetMapping("/pending-cases")
    @Operation(summary = "获取待安排正式咨询的列表")
    public Result<List<PendingCaseVO>> getPendingCases() {
        List<PendingCaseVO> pendingCases = caseService.findPendingCases();
        return Result.success(pendingCases);
    }

    @PostMapping("/counseling-cases")
    @Operation(summary = "创建新个案并自动安排8周咨询")
    public Result<CounselingCases> scheduleNewCase(@AuthenticationPrincipal LoginUser loginUser, @Valid @RequestBody CaseScheduleDto scheduleDto) {
        CounselingCases newCase = caseService.scheduleNewCase(scheduleDto, loginUser.getUserId());
        return Result.success(newCase, "个案安排成功");
    }
    @GetMapping("/available")
    @Operation(summary = "获取可预约的号源列表 (供心理助理选择)")
    public Result<List<ScheduleSlotVO>> listAvailableSlots(@Valid AvailableSlotQueryDto queryDto) {
        List<ScheduleSlotVO> availableSlots = slotService.findAvailableConselorSlots(queryDto);
        return Result.success(availableSlots);
    }
    @GetMapping("/my-counseling-cases")
    @Operation(summary = "分页获取我负责的所有正式咨询个案列表")
    public Result<Page<CaseAssistantListVO>> listMyCases(
            CaseAssistantQueryDto queryDto,
            @AuthenticationPrincipal LoginUser loginUser) {

        Page<CaseAssistantListVO> resultPage = caseService.listMyCases(loginUser.getUserId(), queryDto);
        return Result.success(resultPage);
    }

    // ... 其他管理接口 ...
}
