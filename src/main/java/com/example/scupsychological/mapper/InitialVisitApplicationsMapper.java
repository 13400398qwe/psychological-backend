package com.example.scupsychological.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.pojo.entity.InitialVisitApplications;
import com.example.scupsychological.pojo.vo.ApplicationInterviewerVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 初访预约申请表 Mapper 接口
 * </p>
 *
 * @author tpj
 * @since 2025-06-21
 */
@Mapper
public interface InitialVisitApplicationsMapper extends BaseMapper<InitialVisitApplications> {
    ApplicationInterviewerVO selectApplicationDetailForInterviewer(@Param("applicationId")Long applicationId, @Param("interviewerId")Long interviewerId);
}
