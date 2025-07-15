package com.example.scupsychological.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.pojo.entity.ScheduleTemplates;
import com.example.scupsychological.mapper.MyScheduleMapper; // 假设有一个自定义的Mapper
import com.example.scupsychological.mapper.ScheduleTemplatesMapper;
import com.example.scupsychological.pojo.dto.MySlotsQueryDto;
import com.example.scupsychological.pojo.vo.MyScheduleSlotVO;
import com.example.scupsychological.service.MyScheduleService;
import com.example.scupsychological.pojo.vo.MyScheduleTemplateVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyScheduleServiceImpl implements MyScheduleService {

    private final ScheduleTemplatesMapper templateMapper;
    private final MyScheduleMapper myScheduleMapper; // 自定义Mapper，用于多表查询

    @Override
    public List<MyScheduleTemplateVO> findMyTemplates(Long staffId) {
        // 1. 查询该员工的所有激活的值班模板
        List<ScheduleTemplates> templates = templateMapper.selectList(
                new QueryWrapper<ScheduleTemplates>()
                        .eq("staff_id", staffId)
                        .eq("is_active", true)
        );

        // 2. 转换为 VO 并返回
        return templates.stream().map(template -> {
            MyScheduleTemplateVO vo = new MyScheduleTemplateVO();
            BeanUtils.copyProperties(template, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public  Page<MyScheduleSlotVO> findMySlotsByPageForInterviewer(Long userId, MySlotsQueryDto queryDto) {
        // 1. 创建分页对象
        Page<MyScheduleSlotVO> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());

        // 2. 调用自定义的 Mapper 方法进行多表联合分页查询
        //    这个 SQL 需要 JOIN schedule_slots, initial_visit_applications 和 users 表
        //    以获取到预约的学生姓名等信息。
        myScheduleMapper.selectMySlotsPageForVisitor(page, userId, queryDto);

        return page;
    }

    @Override
    public Page<MyScheduleSlotVO> findMySlotsByPageForCounselor(Long userId, MySlotsQueryDto queryDto) {
        Page<MyScheduleSlotVO> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());

        // 2. 调用自定义的 Mapper 方法进行多表联合分页查询
        //    这个 SQL 需要 JOIN schedule_slots, initial_visit_applications 和 users 表
        //    以获取到预约的学生姓名等信息。
        myScheduleMapper.selectMySlotsPageForCounselor(page, userId, queryDto);

        return page;
    }
}
