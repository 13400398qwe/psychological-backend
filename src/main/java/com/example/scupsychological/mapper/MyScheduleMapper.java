package com.example.scupsychological.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.pojo.dto.MySlotsQueryDto;
import com.example.scupsychological.pojo.vo.MyScheduleSlotVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MyScheduleMapper {
    Page<MyScheduleSlotVO> selectMySlotsPageForVisitor(Page<MyScheduleSlotVO> page, @Param("staffId")Long userId, @Param("query")MySlotsQueryDto queryDto);
    Page<MyScheduleSlotVO> selectMySlotsPageForCounselor(Page<MyScheduleSlotVO> page, @Param("counselorId")Long userId, @Param("query")MySlotsQueryDto queryDto);
}
