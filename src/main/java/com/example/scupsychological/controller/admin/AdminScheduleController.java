package com.example.scupsychological.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.common.Result;
import com.example.scupsychological.pojo.dto.*;
import com.example.scupsychological.pojo.vo.ScheduleSlotVO;
import com.example.scupsychological.pojo.entity.ScheduleSlots;
import com.example.scupsychological.pojo.entity.ScheduleTemplates;
import com.example.scupsychological.pojo.vo.ScheduleTemplateVO;
import com.example.scupsychological.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Tag(name = "后台管理模块 - 值班与排班管理")
@RestController
@Slf4j
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // 整个 Controller 都需要 ADMIN 权限
public class AdminScheduleController {

    private final ScheduleService scheduleService;

    // ====== 模板管理 ======
    @PostMapping("/schedule-templates")
    @Operation(summary = "创建值班模板")
    public Result<ScheduleTemplateVO> createTemplate(@Valid @RequestBody ScheduleTemplateCreateDto createDto) {
        ScheduleTemplateVO templateVO = scheduleService.createTemplate(createDto);
        return Result.success(templateVO,"模板创建成功");
    }

    @GetMapping("/schedule-templates")
    @Operation(summary = "分页获取值班模板")
    public Result<Page<ScheduleTemplateVO>> listTemplates(
            @Parameter(description = "页码, 从1开始") @RequestParam(value = "pageNum", defaultValue = "1") long pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "10") long pageSize
    ) {
        Page<ScheduleTemplateVO>PageVO = scheduleService.listTemplates(pageNum,pageSize);
        return Result.success(PageVO, "查询成功");
    }

    @PutMapping("/schedule-templates/{id}")
    @Operation(summary = "更新值班模板")
    public Result<Object> updateTemplate(@PathVariable Long id, @Valid @RequestBody ScheduleTemplates updateDto) {
        ScheduleTemplateVO templateVO = scheduleService.updateTemplate(id, updateDto);
        return Result.success(templateVO, "更新成功");
    }
    @DeleteMapping("/schedule-templates/{id}")
    @Operation(summary = "删除值班模板")
    public Result<Object> deleteTemplate(@PathVariable Long id)
    {
        scheduleService.deleteTemplate(id);
        return Result.success("删除成功");
    }

    // ====== 号源管理 ======
    @PostMapping("/schedule-slots/generate")
    @Operation(summary = "根据模板批量生成号源")
    public Result<Integer> generateSlots(@Valid @RequestBody SlotGenerationRequestDto requestDto) {
        int count = scheduleService.generateSlotsFromTemplates(requestDto.getStartDate(), requestDto.getEndDate());
        return Result.success(count, "成功生成 " + count + " 个可预约号源");
    }

    @GetMapping("/schedule-slots/pageQuery")
    @Operation(summary = "分页获取号源")
    public Result<Page<ScheduleSlotVO>> pageSlots(SlotAdminQueryDto queryDto) {
        // 直接将查询 DTO 传递给 Service 层
        Page<ScheduleSlotVO> resultPage = scheduleService.listSlotsByPage(queryDto);
        return Result.success(resultPage);
    }

    @PostMapping
    @Operation(summary = "手动创建单个号源(用于补录)")
    public Result<ScheduleSlotVO> createSlot(@Valid @RequestBody ScheduleSlotCreateDto createDto) {
        ScheduleSlotVO newSlot = scheduleService.createSlotManually(createDto);
        return Result.success(newSlot, "号源创建成功");
    }

    @PutMapping("/{id}")
    @Operation(summary = "手动更新单个号源")
    public Result<ScheduleSlotVO> updateSlot(@PathVariable Long id, @Valid @RequestBody ScheduleSlotUpdateDto updateDto) {
        ScheduleSlotVO updatedSlot = scheduleService.updateSlotManually(id, updateDto);
        return Result.success(updatedSlot, "号源更新成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "手动删除单个号源")
    public Result<Object> deleteSlot(@PathVariable Long id) {
        scheduleService.deleteSlotLogically(id);
        return Result.success("号源删除成功");
    }

    @GetMapping("/available-times")
    @Operation(summary = "获取指定日期所有可用的初访时间点 (聚合后)")
    public Result<List<LocalTime>> getAvailableTimes(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<LocalTime> times = scheduleService.findAvailableTimeSlotsByDate(date);
        return Result.success(times);
    }
    // ... 其他手动增删改查号源的接口 ...
}
