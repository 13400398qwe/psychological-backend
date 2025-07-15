package com.example.scupsychological.controller.counselor;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.common.Result;
import com.example.scupsychological.pojo.dto.*;
import com.example.scupsychological.pojo.entity.CounselingCases;
import com.example.scupsychological.pojo.entity.CounselingSessions;
import com.example.scupsychological.pojo.vo.CaseReportContentVO;
import com.example.scupsychological.pojo.vo.CounselorCaseDetailVO;
import com.example.scupsychological.pojo.vo.CounselorCaseListVO;
import com.example.scupsychological.pojo.vo.MyScheduleSlotVO;
import com.example.scupsychological.security.LoginUser;
import com.example.scupsychological.service.CounselingCaseService;
import com.example.scupsychological.service.CounselingService;
import com.example.scupsychological.service.MyScheduleService;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "咨询师模块")
@RestController
@RequestMapping("/api/counselor")
@RequiredArgsConstructor
@PreAuthorize("hasRole('COUNSELOR')")
public class CounselorController {

    private final CounselingService counselingService;
    private final CounselingCaseService counselingCaseService;
    private final MyScheduleService myScheduleService;
    // ... 获取个案列表和详情的 GET 接口 ...

    @GetMapping("/cases")
    @Operation(summary = "获取我的个案列表")
    public Result<Page<CounselorCaseListVO>> getMyCases(
            CounselorCaseQueryDto queryDto,
            @AuthenticationPrincipal LoginUser loginUser) {

        Page<CounselorCaseListVO> casePage = counselingService.getMyCases(loginUser.getUserId(), queryDto);
        return Result.success(casePage);
    }

    @GetMapping("/cases/{caseId}")
    @Operation(summary = "获取单个个案详情（含所有咨询安排）")
    public Result<CounselorCaseDetailVO> getCaseDetail(
            @PathVariable Long caseId,
            @AuthenticationPrincipal LoginUser loginUser) {

        CounselorCaseDetailVO caseDetail = counselingService.getCaseDetail(loginUser.getUserId(), caseId);
        return Result.success(caseDetail);
    }
    @PutMapping("/sessions/{sessionId}")
    @Operation(summary = "录入单次咨询记录（更新状态和备注）")
    public Result<CounselingSessions> updateSession(
            @PathVariable Long sessionId,
            @Valid @RequestBody CounselingSessionUpdateDto updateDto,
            @AuthenticationPrincipal LoginUser loginUser) {

        CounselingSessions updatedSession = counselingService.updateSession(loginUser.getUserId(), sessionId, updateDto);
        return Result.success(updatedSession, "咨询记录更新成功");
    }

    @PostMapping("/cases/{caseId}/request-extension")
    @Operation(summary = "为个案申请追加咨询次数")
    public Result<Object> requestExtension(
            @PathVariable Long caseId,
            @Valid @RequestBody CaseExtensionRequestDto requestDto,
            @AuthenticationPrincipal LoginUser loginUser) {

        counselingService.requestExtension(loginUser.getUserId(), caseId, requestDto);
        return Result.success("加时申请已提交，请等待中心审批");
    }


    @PostMapping("/case-reports")
    @Operation(summary = "提交结案报告")
    public Result<CounselingCases> submitCaseReport(
            @Valid @RequestBody CaseReportSubmitDto submitDto,
            @AuthenticationPrincipal LoginUser loginUser) {

        // 直接调用 CounselingService 中的方法
        CounselingCases updatedCase = counselingService.submitReport(loginUser.getUserId(), submitDto);
        return Result.success(updatedCase, "结案报告提交成功");
    }
    @GetMapping("/case-reports/{caseId}/export")
    @Operation(summary = "导出结案报告为Word文档")
    public ResponseEntity<byte[]> exportReport(@PathVariable Long caseId, @AuthenticationPrincipal LoginUser loginUser) {
        byte[] wordBytes = counselingCaseService.exportReportAsWord(loginUser.getUserId(), caseId);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=case_report_" + caseId + ".docx");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .body(wordBytes);
    }
    @GetMapping("/slots")
    @Operation(summary = "分页获取我的具体日程安排")
    public Result<Page<MyScheduleSlotVO>> listMySlots(
            MySlotsQueryDto queryDto,
            @AuthenticationPrincipal LoginUser loginUser) {

        Page<MyScheduleSlotVO> slotsPage = myScheduleService.findMySlotsByPageForCounselor(loginUser.getUserId(), queryDto);
        return Result.success(slotsPage);
    }
    //返回结案报告的内容
    @GetMapping("/case-reports")
    @Operation(summary = "获取个案的结案报告内容")
    public Result<Object> getCaseReportContent(@AuthenticationPrincipal LoginUser loginUser,Long pageNum, Long pageSize) {
        Page<CaseReportContentVO> rst=counselingCaseService.getCaseReportContent(loginUser.getUserId(), pageNum, pageSize);
        return Result.success(rst);
    }
}
