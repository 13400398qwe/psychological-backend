package com.example.scupsychological.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.common.Result;
import com.example.scupsychological.security.LoginUser;
import com.example.scupsychological.service.MyScheduleService;
import com.example.scupsychological.pojo.vo.MyScheduleSlotVO;
import com.example.scupsychological.pojo.vo.MyScheduleTemplateVO;
import com.example.scupsychological.pojo.dto.MySlotsQueryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Tag(name = "我的排班模块", description = "供初访员/咨询师查询自己的排班")
@RestController
@RequestMapping("/api/my-schedule")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VISITOR', 'COUNSELOR')") // 只有这两类角色能访问
public class MyScheduleController {

    private final MyScheduleService myScheduleService;
    @GetMapping("/templates")
    @Operation(summary = "获取我的所有值班模板")
    public Result<List<MyScheduleTemplateVO>> listMyTemplates(@AuthenticationPrincipal LoginUser loginUser) {
        List<MyScheduleTemplateVO> templates = myScheduleService.findMyTemplates(loginUser.getUserId());
        return Result.success(templates);
    }

}