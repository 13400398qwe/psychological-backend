package com.example.scupsychological.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.pojo.dto.*;
import com.example.scupsychological.pojo.entity.ScheduleTemplates;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.example.scupsychological.pojo.vo.ScheduleSlotVO;
import com.example.scupsychological.pojo.vo.ScheduleTemplateVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

public interface ScheduleService {

    ScheduleTemplateVO createTemplate(@Valid ScheduleTemplateCreateDto createDto);

    Page<ScheduleTemplateVO> listTemplates(long pageNum, long pageSize);

    ScheduleTemplateVO updateTemplate(Long id, @Valid ScheduleTemplates updateDto);

    int generateSlotsFromTemplates(LocalDate startDate, LocalDate endDate);

    Page<ScheduleSlotVO> listSlotsByPage(SlotAdminQueryDto queryDto);

    ScheduleSlotVO createSlotManually(@Valid ScheduleSlotCreateDto createDto);

    ScheduleSlotVO updateSlotManually(Long id, @Valid ScheduleSlotUpdateDto updateDto);

    void deleteSlotLogically(Long id);

    List<ScheduleSlotVO> findAvailableSlots(AvailableSlotQueryDto queryDto);

    boolean bookSlot(@NotNull(message = "必须选择一个预约时间段") Long scheduleSlotId);

    List<LocalTime> findAvailableTimeSlotsByDate(LocalDate date);

    void deleteTemplate(Long id);

    List<ScheduleSlotVO> findAvailableConselorSlots(@Valid AvailableSlotQueryDto queryDto);
}
