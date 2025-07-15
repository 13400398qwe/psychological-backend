package com.example.scupsychological.controller.interviewer;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.common.Result;
import com.example.scupsychological.pojo.dto.InitialVisitRecordCreateDto;
import com.example.scupsychological.pojo.dto.MySlotsQueryDto;
import com.example.scupsychological.pojo.dto.VisitRecordQueryDto;
import com.example.scupsychological.pojo.entity.InitialVisitRecords;
import com.example.scupsychological.pojo.vo.ApplicationInterviewerVO;
import com.example.scupsychological.pojo.vo.MyScheduleSlotVO;
import com.example.scupsychological.pojo.vo.VisitRecordInterviewerVO;
import com.example.scupsychological.security.LoginUser;
import com.example.scupsychological.service.AppInterviewerService;
import com.example.scupsychological.service.ApplicationRecordService;
import com.example.scupsychological.service.MyScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interviewer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('VISITOR')")
@Tag(name = "初访员模块")
public class InterviewerController {
    private final AppInterviewerService appInterviewerService;
    private final ApplicationRecordService recordService;
    private final MyScheduleService myScheduleService;
    @GetMapping("/applications")
    public Result<Page<ApplicationInterviewerVO>> listMyAssignedApplications(
            @RequestParam(defaultValue = "1")
            Long pageNum,
            @RequestParam(defaultValue = "10")
            Long pageSize,
            @AuthenticationPrincipal LoginUser loginUser) {
        Page<ApplicationInterviewerVO> resultPage=appInterviewerService.listAssignedToMe(pageNum, pageSize,loginUser.getUserId());
        return Result.success(resultPage);
    }
    @GetMapping("/applications/{id}")
    @Operation(summary = "获取单个初访任务的详细信息")
    public Result<ApplicationInterviewerVO> getApplicationDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal LoginUser loginUser) {

        ApplicationInterviewerVO detail = appInterviewerService.getAssignedDetail(loginUser.getUserId(), id);
        return Result.success(detail);
    }
    @PostMapping("/visit-records")
    @Operation(summary = "提交初访评估记录")
    public Result<InitialVisitRecords> createRecord(
            @Valid @RequestBody InitialVisitRecordCreateDto createDto,
            @AuthenticationPrincipal LoginUser loginUser) {

        InitialVisitRecords newRecord = recordService.createRecord(loginUser.getUserId(), createDto);
        return Result.success(newRecord, "初访记录提交成功");
    }

    /**
     * 【新增接口】
     * 分页获取当前初访员创建的所有初访记录
     * @param queryDto 查询参数
     * @param loginUser 当前登录用户
     * @return 分页的初访记录列表
     */
    @GetMapping("/visit-records")
    @Operation(summary = "获取我提交的所有初访记录")
    public Result<Page<VisitRecordInterviewerVO>> listMyCreatedRecords(
            VisitRecordQueryDto queryDto,
            @AuthenticationPrincipal LoginUser loginUser) {

        Page<VisitRecordInterviewerVO> resultPage = recordService.findMyRecordsByPage(loginUser.getUserId(), queryDto);
        return Result.success(resultPage);
    }

    @GetMapping("/slots")
    @Operation(summary = "分页获取我的具体日程安排")
    public Result<Page<MyScheduleSlotVO>> listMySlots(
            MySlotsQueryDto queryDto,
            @AuthenticationPrincipal LoginUser loginUser) {

        Page<MyScheduleSlotVO> slotsPage = myScheduleService.findMySlotsByPageForInterviewer(loginUser.getUserId(), queryDto);
        return Result.success(slotsPage);
    }
}
