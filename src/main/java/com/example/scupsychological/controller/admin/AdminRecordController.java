package com.example.scupsychological.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.common.Result;
import com.example.scupsychological.pojo.dto.AdminRecordQueryDto;
import com.example.scupsychological.pojo.vo.InitialVisitRecordAdminVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "后台管理模块 - 初访记录管理")
@RestController
@RequestMapping("/api/admin/initial-visit-records")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminRecordController {

    private final InitialVisitRecordAdminService recordAdminService;

    @GetMapping
    @Operation(summary = "分页、筛选获取所有初访记录")
    public Result<Page<InitialVisitRecordAdminVO>> listAllRecords(AdminRecordQueryDto queryDto) {
        Page<InitialVisitRecordAdminVO> resultPage = recordAdminService.listAllRecordsByPage(queryDto);
        return Result.success(resultPage);
    }
}
