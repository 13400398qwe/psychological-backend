package com.example.scupsychological.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.common.exception.BaseException;
import com.example.scupsychological.mapper.InitialVisitApplicationsMapper;
import com.example.scupsychological.mapper.InitialVisitRecordsMapper;
import com.example.scupsychological.mapper.ScheduleSlotsMapper;
import com.example.scupsychological.pojo.dto.InitialVisitRecordCreateDto;
import com.example.scupsychological.pojo.dto.VisitRecordQueryDto;
import com.example.scupsychological.pojo.entity.InitialVisitApplications;
import com.example.scupsychological.pojo.entity.InitialVisitRecords;
import com.example.scupsychological.pojo.vo.VisitRecordInterviewerVO;
import com.example.scupsychological.service.ApplicationRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationRecordServiceImpl implements ApplicationRecordService {

    private final InitialVisitApplicationsMapper applicationMapper;
    private final InitialVisitRecordsMapper recordMapper;
    private final ScheduleSlotsMapper scheduleSlotsMapper;

    @Override
    @Transactional
    public InitialVisitRecords createRecord(Long currentInterviewerId, InitialVisitRecordCreateDto createDto) {
        Long applicationId = createDto.getApplicationId();

        // 1. 查找对应的初访申请记录
        InitialVisitApplications application = applicationMapper.selectById(applicationId);
        if (application == null) {
            throw new BaseException("操作失败，关联的初访申请不存在");
        }

        // 2. 【安全校验】确认该任务是否真的指派给了当前操作的初访员
        if (!currentInterviewerId.equals(application.getAssignedInterviewerId())) {
            throw new AccessDeniedException("无权为不属于您的任务录入记录");
        }

        // 3. 【状态校验】确认申请状态是否为“已批准，待初访”
        if (!"APPROVED".equals(application.getStatus())) {
            throw new BaseException("操作失败，该申请当前状态无法录入记录");
        }

        // 4. 【防重复校验】确认该申请是否已经录入过记录了
        if (recordMapper.exists(new QueryWrapper<InitialVisitRecords>().eq("application_id", applicationId))) {
            throw new BaseException("操作失败，请勿重复提交记录");
        }

        // 5. 所有校验通过，创建并保存初访记录实体
        InitialVisitRecords newRecord = new InitialVisitRecords();
        BeanUtils.copyProperties(createDto, newRecord);
        recordMapper.insert(newRecord);

        // 6. 【状态流转】更新初访申请的状态为“已完成”
        application.setStatus("COMPLETED");
        applicationMapper.updateById(application);
        scheduleSlotsMapper.markSlotAsCompleted(application.getScheduleSlotId());

        return newRecord;
    }

    @Override
    public Page<VisitRecordInterviewerVO> findMyRecordsByPage(Long interviewerId, VisitRecordQueryDto queryDto) {
        // 1. 创建分页对象
        Page<VisitRecordInterviewerVO> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());

        // 2. 调用自定义的 Mapper 方法，传入分页对象、当前初访员ID和筛选条件
        recordMapper.selectRecordsByInterviewer(page, interviewerId, queryDto);

        return page;
    }
}
