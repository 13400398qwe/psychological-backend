package com.example.scupsychological.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.pojo.dto.AdminRecordQueryDto;
import com.example.scupsychological.pojo.dto.VisitRecordQueryDto;
import com.example.scupsychological.pojo.entity.InitialVisitRecords;
import com.example.scupsychological.pojo.vo.InitialVisitRecordAdminVO;
import com.example.scupsychological.pojo.vo.VisitRecordInterviewerVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 初访记录表 Mapper 接口
 * </p>
 *
 * @author tpj
 * @since 2025-06-21
 */
@Mapper
public interface InitialVisitRecordsMapper extends BaseMapper<InitialVisitRecords> {

    Page<VisitRecordInterviewerVO> selectRecordsByInterviewer(Page<VisitRecordInterviewerVO> page, Long interviewerId, @Param("query")VisitRecordQueryDto queryDto);

    Page<InitialVisitRecordAdminVO> selectRecordsForAdmin(Page<InitialVisitRecordAdminVO> page,  @Param("query")AdminRecordQueryDto queryDto);
}
