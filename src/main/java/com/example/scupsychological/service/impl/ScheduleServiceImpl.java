package com.example.scupsychological.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.common.exception.AccountNotFoundException;
import com.example.scupsychological.common.exception.BaseException;
import com.example.scupsychological.mapper.ScheduleSlotsMapper;
import com.example.scupsychological.mapper.ScheduleTemplatesMapper;
import com.example.scupsychological.mapper.UsersMapper;
import com.example.scupsychological.pojo.dto.*;
import com.example.scupsychological.pojo.entity.ScheduleSlots;
import com.example.scupsychological.pojo.entity.ScheduleTemplates;
import com.example.scupsychological.pojo.entity.Users;
import com.example.scupsychological.pojo.vo.ScheduleSlotVO;
import com.example.scupsychological.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.example.scupsychological.pojo.vo.ScheduleTemplateVO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleTemplatesMapper scheduleTemplateMapper;
    private final ScheduleSlotsMapper slotMapper;
    private final UsersMapper userMapper;

    @Override
    public ScheduleTemplateVO createTemplate(ScheduleTemplateCreateDto createDto) {
        if(createDto.getSlotDurationMinutes()!=null&&createDto.getSlotDurationMinutes()<=0)
            throw new BaseException("单次服务时长必须大于0");
        ScheduleTemplates template = new ScheduleTemplates();
        BeanUtils.copyProperties(createDto, template);
        scheduleTemplateMapper.insert(template);
        ScheduleTemplateVO templateVO=new ScheduleTemplateVO();
        BeanUtils.copyProperties(template, templateVO);
        return templateVO;
    }

    @Override
    public Page<ScheduleTemplateVO> listTemplates(long pageNum, long pageSize) {
        QueryWrapper<ScheduleTemplates> wrapper = new QueryWrapper<>();
        Page<ScheduleTemplates> page = new Page<>(pageNum, pageSize);
        wrapper.orderByDesc("created_at");
        scheduleTemplateMapper.selectPage(page, wrapper);
        Page<ScheduleTemplateVO> pageVO = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<ScheduleTemplateVO> templateVOList = page.getRecords().stream().map(template -> {
            ScheduleTemplateVO vo = new ScheduleTemplateVO();
            BeanUtils.copyProperties(template, vo);
            return vo;
        }).collect(Collectors.toList());
        pageVO.setRecords(templateVOList);
        return pageVO;
    }

    @Override
    public ScheduleTemplateVO updateTemplate(Long id, ScheduleTemplates updateDto) {
        ScheduleTemplates template = scheduleTemplateMapper.selectById(id);
        if(template == null)
            throw new AccountNotFoundException("模板不存在");
        BeanUtils.copyProperties(updateDto, template);
        scheduleTemplateMapper.updateById(template);
        ScheduleTemplateVO templateVO = new ScheduleTemplateVO();
        BeanUtils.copyProperties(template, templateVO);
        return templateVO;
    }

    /**
     * 有关slots的服务函数
     * @param startDate
     * @param endDate
     * @return
     */

    @Override
    @Transactional
    public int generateSlotsFromTemplates(LocalDate startDate, LocalDate endDate) {
        // 步骤1：获取模板
        List<ScheduleTemplates> activeTemplates = scheduleTemplateMapper.selectList(
                new QueryWrapper<ScheduleTemplates>()
                        .eq("is_active", true)
                        .le("effective_from", endDate)
                        .ge("effective_to", startDate)
        );
        if (activeTemplates.isEmpty()) {
            return 0;
        }

        // 步骤2：预加载已存在的号源用于去重
        List<Long> staffIds = activeTemplates.stream().map(ScheduleTemplates::getStaffId).distinct().toList();
        List<ScheduleSlots> existingSlotsList = slotMapper.selectList(
                new QueryWrapper<ScheduleSlots>()
                        .in("staff_id", staffIds)
                        .ge("start_time", startDate.atStartOfDay())
                        .lt("end_time", endDate.plusDays(1).atStartOfDay())
        );
        Set<String> existingSlots = existingSlotsList.stream()
                .map(slot -> slot.getStaffId() + "_" + slot.getStartTime())
                .collect(Collectors.toSet());

        List<ScheduleSlots> slotsToCreate = new ArrayList<>();

        // 步骤3-6：遍历、匹配、切分、去重 (这部分纯业务逻辑保持不变)
        for (LocalDate currentDate = startDate; !currentDate.isAfter(endDate); currentDate = currentDate.plusDays(1)) {
            final int dayOfWeek = currentDate.getDayOfWeek().getValue();
            LocalDate finalCurrentDate = currentDate;
            activeTemplates.stream()
                    .filter(template -> template.getDayOfWeek() == dayOfWeek && !finalCurrentDate.isBefore(template.getEffectiveFrom()) && !finalCurrentDate.isAfter(template.getEffectiveTo()))
                    .forEach(template -> {
                        log.debug("为日期 {} 匹配到模板 ID: {}", finalCurrentDate, template.getId());

                        // --- 核心修正：增加对时长的防御性检查 ---
                        Integer duration = template.getSlotDurationMinutes();
                        if (duration == null || duration <= 0) {
                            log.warn("模板 ID: {} 的单次时长(slot_duration_minutes)设置不合法(值: {})，已跳过此模板。", template.getId(), duration);
                            return; // 使用 return 跳过当前 forEach 中的这个有问题的 template
                        }
                        // ----------------------------------------------------

                        LocalTime slotStartTime = template.getStartTime();
                        while (slotStartTime.isBefore(template.getEndTime())) {
                            LocalDateTime startDateTime = LocalDateTime.of(finalCurrentDate, slotStartTime);
                            LocalDateTime endDateTime = startDateTime.plusMinutes(duration);

                            if (endDateTime.toLocalTime().isAfter(template.getEndTime()) && !endDateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
                                break;
                            }

                            String slotKey = template.getStaffId() + "_" + startDateTime;
                            if (!existingSlots.contains(slotKey)) {
                                ScheduleSlots newSlot = new ScheduleSlots();
                                newSlot.setStaffId(template.getStaffId());
                                newSlot.setLocation(template.getLocation());
                                newSlot.setStartTime(startDateTime);
                                newSlot.setEndTime(endDateTime);
                                newSlot.setStatus("AVAILABLE");

                                slotsToCreate.add(newSlot);
                                existingSlots.add(slotKey);
                            }

                            // 准备下一个时间点
                            slotStartTime = slotStartTime.plusMinutes(duration);
                        }
                    });
        }

        // 步骤7：调用 Mapper 的自定义方法进行批量插入
        if (!slotsToCreate.isEmpty()) {
            slotMapper.insertBatch(slotsToCreate);
        }

        return slotsToCreate.size();
    }

    @Override
    public Page<ScheduleSlotVO> listSlotsByPage(SlotAdminQueryDto queryDto) {
        // 1. 创建 MyBatis-Plus 的分页对象
        Page<ScheduleSlots> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());

        // 2. 构建动态查询条件 (QueryWrapper)
        QueryWrapper<ScheduleSlots> wrapper = new QueryWrapper<>();
        if (queryDto.getStaffId() != null) {
            wrapper.eq("staff_id", queryDto.getStaffId());
        }
        if (StringUtils.hasText(queryDto.getStatus())) {
            wrapper.eq("status", queryDto.getStatus());
        }
        if (queryDto.getStartDate() != null) {
            wrapper.ge("start_time", queryDto.getStartDate().atStartOfDay());
        }
        if (queryDto.getEndDate() != null) {
            wrapper.lt("start_time", queryDto.getEndDate().plusDays(1).atStartOfDay());
        }
        wrapper.gt("start_time", LocalDateTime.now());
        // 默认按开始时间降序排序
        wrapper.orderByDesc("start_time");

        // 3. 执行分页查询，获取 Page<ScheduleSlot>
        slotMapper.selectPage(page, wrapper);

        // 4. 将 Page<ScheduleSlot> 转换为 Page<ScheduleSlotVO>，这是关键
        return convertToVoPage(page);
    }
    @Override
    @Transactional
    public ScheduleSlotVO createSlotManually(ScheduleSlotCreateDto createDto) {
        // 1. 业务校验：检查时间是否合法
        if (!createDto.getStartTime().isBefore(createDto.getEndTime())) {
            throw new BaseException("开始时间必须早于结束时间");
        }
        // 2. 业务校验：检查该时间段是否与已有排班重叠
        if (isSlotOverlapping(createDto.getStaffId(), createDto.getStartTime(), createDto.getEndTime(), null)) {
            throw new BaseException("创建失败，该时间段与该员工的已有排班重叠");
        }

        ScheduleSlots newSlot = new ScheduleSlots();
        BeanUtils.copyProperties(createDto, newSlot);
        newSlot.setStatus("AVAILABLE");

        slotMapper.insert(newSlot);
        ScheduleSlotVO createSlotVO = new ScheduleSlotVO();
        BeanUtils.copyProperties(newSlot, createSlotVO);
        // 转换为VO并返回
        return createSlotVO;
    }

    @Override
    @Transactional
    public ScheduleSlotVO updateSlotManually(Long id, ScheduleSlotUpdateDto updateDto) {
        // 1. 查找要更新的号源
        ScheduleSlots existingSlot = slotMapper.selectById(id);
        if (existingSlot == null) {
            throw new BaseException("更新失败，未找到该号源");
        }
        // 已被预约的号源通常不允许修改时间
        if ("BOOKED".equals(existingSlot.getStatus())) {
            throw new BaseException("更新失败，该时间段已被预约，无法修改");
        }

        // 2. 更新字段 (按需)
        BeanUtils.copyProperties(updateDto, existingSlot);

        // 3. 如果更新了时间，需要再次检查重叠
        if (updateDto.getStartTime() != null || updateDto.getStaffId() != null) {
            if (isSlotOverlapping(existingSlot.getStaffId(), existingSlot.getStartTime(), existingSlot.getEndTime(), id)) {
                throw new BaseException("更新失败，修改后的时间段与已有排班重叠");
            }
        }

        slotMapper.updateById(existingSlot);
        ScheduleSlotVO updatedSlotVO = new ScheduleSlotVO();
        BeanUtils.copyProperties(existingSlot, updatedSlotVO);
        return updatedSlotVO;
    }

    @Override
    public void deleteSlotLogically(Long id) {
        ScheduleSlots existingSlot = slotMapper.selectById(id);
        if (existingSlot != null && "BOOKED".equals(existingSlot.getStatus())) {
            throw new BaseException("删除失败，该时间段已被预约");
        }
        // MyBatis-Plus 的 deleteById 会自动处理 @TableLogic 逻辑删除
        slotMapper.deleteById(id);
    }

    @Override
    public List<ScheduleSlotVO> findAvailableSlots(AvailableSlotQueryDto queryDto) {
        // 1. 调用我们新创建的、包含 JOIN 和角色过滤的自定义 Mapper 方法
        List<ScheduleSlots> slots = slotMapper.selectAvailableInterviewerSlots(queryDto);
        // 2. 将查询到的实体列表，转换为用于前端展示的 VO 列表
        //    这个 convertToVoList 方法会负责查询并填充员工姓名等信息
        return convertToVoList(slots);
    }


    @Override
    public boolean bookSlot(Long slotId) {
        // 调用我们自定义的原子更新方法
        int affectedRows = slotMapper.atomicallyBookSlot(slotId);

        // 如果影响的行数大于0，说明更新成功，返回 true
        return affectedRows > 0;
    }

    @Override
    public List<LocalTime> findAvailableTimeSlotsByDate(LocalDate date) {
        // 1. 查询指定日期范围内所有可用的号源
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<ScheduleSlots> availableSlots = slotMapper.selectList(
                new QueryWrapper<ScheduleSlots>()
                        .eq("status", "AVAILABLE")
                        .ge("start_time", startOfDay)
                        .lt("start_time", endOfDay)
        );

        // 2. 提取时间点，去重并排序
        return availableSlots.stream()
                .map(slot -> slot.getStartTime().toLocalTime())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTemplate(Long id) {
        scheduleTemplateMapper.deleteById(id);
        log.info("模板删除成功");
        return;
    }

    @Override
    public List<ScheduleSlotVO> findAvailableConselorSlots(AvailableSlotQueryDto queryDto) {
        List<ScheduleSlots> ret = slotMapper.selectAvailableCounselorSlots(queryDto);
        return convertToVoList(ret);
    }

    private List<ScheduleSlotVO> convertToVoList(List<ScheduleSlots> slots) {
        return slots.stream().map(slot -> {
            ScheduleSlotVO slotVO = new ScheduleSlotVO();
            BeanUtils.copyProperties(slot, slotVO);
            slotVO.setStaffName(userMapper.selectById(slot.getStaffId()).getName());
            return slotVO;
        }).toList();
    }

    // --- 私有辅助方法 ---

    private boolean isSlotOverlapping(Long staffId, LocalDateTime startTime, LocalDateTime endTime, Long excludeSlotId) {
        QueryWrapper<ScheduleSlots> wrapper = new QueryWrapper<>();
        wrapper.eq("staff_id", staffId)
                .lt("start_time", endTime)
                .gt("end_time", startTime);
        if (excludeSlotId != null) {
            wrapper.ne("id", excludeSlotId);
        }
        return slotMapper.exists(wrapper);
    }

    private boolean isBeforeOrEquals(LocalTime time1, LocalTime time2) {
        return !time1.isAfter(time2);
    }
    /**
     * 私有辅助方法，用于将实体分页对象转换为视图分页对象
     */
    private Page<ScheduleSlotVO> convertToVoPage(Page<ScheduleSlots> sourcePage) {
        Page<ScheduleSlotVO> voPage = new Page<>(sourcePage.getCurrent(), sourcePage.getSize(), sourcePage.getTotal());

        List<ScheduleSlots> records = sourcePage.getRecords();
        if (records.isEmpty()) {
            return voPage; // 如果当前页没有数据，直接返回空的VO分页对象
        }

        // a. 批量获取所有相关的员工信息，避免 N+1 查询
        List<Long> staffIds = records.stream().map(ScheduleSlots::getStaffId).distinct().toList();
        Map<Long, Users> userMap = userMapper.selectBatchIds(staffIds).stream()
                .collect(Collectors.toMap(Users::getId, Function.identity()));

        // b. 遍历实体列表，组装 VO 列表
        List<ScheduleSlotVO> voRecords = records.stream().map(slot -> {
            ScheduleSlotVO vo = new ScheduleSlotVO();
            BeanUtils.copyProperties(slot, vo);
            Users staff = userMap.get(slot.getStaffId());
            if (staff != null) {
                vo.setStaffName(staff.getName()); // 设置员工姓名
            }
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voRecords);
        return voPage;
    }

}
