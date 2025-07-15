package com.example.scupsychological.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.scupsychological.pojo.entity.CounselingSessions;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 单次咨询记录表 Mapper 接口
 * </p>
 *
 * @author tpj
 * @since 2025-06-21
 */
@Mapper
public interface CounselingSessionsMapper extends BaseMapper<CounselingSessions> {

    int  insertBatch(List<CounselingSessions> sessionsToCreate);
}
