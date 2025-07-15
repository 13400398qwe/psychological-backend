package com.example.scupsychological.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.pojo.dto.MySlotsQueryDto;
import com.example.scupsychological.pojo.vo.MyScheduleSlotVO;
import com.example.scupsychological.pojo.vo.MyScheduleTemplateVO;

import java.util.List;

public interface MyScheduleService {
    List<MyScheduleTemplateVO> findMyTemplates(Long userId);

    Page<MyScheduleSlotVO> findMySlotsByPageForInterviewer(Long userId, MySlotsQueryDto queryDto);

    Page<MyScheduleSlotVO> findMySlotsByPageForCounselor(Long userId, MySlotsQueryDto queryDto);
}
