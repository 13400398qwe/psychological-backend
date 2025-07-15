package com.example.scupsychological.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.common.Result;
import com.example.scupsychological.pojo.dto.PendingExtensionQueryDto;
import com.example.scupsychological.service.AdminCaseService;
import com.example.scupsychological.pojo.dto.ExtensionApprovalDto;
import com.example.scupsychological.pojo.vo.PendingExtensionRequestVO; // 假设有一个用于列表的VO
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "后台管理模块 - 个案管理")
@RestController
@RequestMapping("/api/admin/case-extensions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCaseController {

    private final AdminCaseService adminCaseService;

    @GetMapping("/pending")
    @Operation(summary = "分页获取所有待审批的加时申请列表")
    public Result<Page<PendingExtensionRequestVO>> listPendingExtensions(PendingExtensionQueryDto queryDto) {
        Page<PendingExtensionRequestVO> requests = adminCaseService.findPendingExtensionRequests(queryDto);
        return Result.success(requests);
    }

    @PostMapping("/{requestId}/approve")
    @Operation(summary = "【批准】一个加时申请")
    public Result<Object> approveExtension(
            @PathVariable Long requestId,
            @Valid @RequestBody(required = false) ExtensionApprovalDto approvalDto) {

        adminCaseService.approveExtension(requestId, approvalDto);
        return Result.success("加时申请已批准");
    }

    @PostMapping("/{requestId}/reject")
    @Operation(summary = "【拒绝】一个加时申请")
    public Result<Object> rejectExtension(
            @PathVariable Long requestId,
            @Valid @RequestBody ExtensionApprovalDto approvalDto) { // 拒绝时，理由是必需的

        adminCaseService.rejectExtension(requestId, approvalDto);
        return Result.success("加时申请已拒绝");
    }
}