package com.example.scupsychological.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.scupsychological.pojo.entity.Questions;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 问卷题目表 Mapper 接口
 * </p>
 *
 * @author tpj
 * @since 2025-06-24
 */
@Mapper
public interface QuestionsMapper extends BaseMapper<Questions> {
    int insertBatch(@Param("questionnaireQuestionsList") List<Questions> questionnaireQuestionsList);
}
