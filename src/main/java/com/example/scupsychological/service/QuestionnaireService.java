package com.example.scupsychological.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.pojo.dto.*;
import com.example.scupsychological.pojo.entity.InitialVisitApplications;
import com.example.scupsychological.pojo.entity.Questions;
import com.example.scupsychological.pojo.vo.EvaluationResult;
import com.example.scupsychological.pojo.vo.QuestionVO;
import com.example.scupsychological.pojo.vo.QuestionnaireEvaluationResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface QuestionnaireService {
    @Transactional
        // 关键：整个操作是一个事务，要么全部成功，要么全部失败

    List<QuestionVO> getRandomQuestions();



    EvaluationResult evaluate(@NotNull(message = "问卷答案不能为空") Map<Long, Long> answers);

    Questions createQuestion(@Valid QuestionCreateDto createDto);

    Page<Questions> listQuestions(QuestionQueryDto queryDto);

    Questions updateQuestion(Long id, QuestionUpdateDto updateDto);

    void deleteQuestion(Long id);
}
