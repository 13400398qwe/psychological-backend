package com.example.scupsychological.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.common.exception.BaseException;
import com.example.scupsychological.mapper.InitialVisitApplicationsMapper;
import com.example.scupsychological.mapper.QuestionsMapper;
import com.example.scupsychological.mapper.ScheduleSlotsMapper;
import com.example.scupsychological.pojo.dto.QuestionCreateDto;
import com.example.scupsychological.pojo.dto.QuestionQueryDto;
import com.example.scupsychological.pojo.dto.QuestionUpdateDto;
import com.example.scupsychological.pojo.entity.Questions;
import com.example.scupsychological.pojo.vo.EvaluationResult;
import com.example.scupsychological.pojo.vo.QuestionVO;
import com.example.scupsychological.service.QuestionnaireService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionnaireServiceImpl implements QuestionnaireService {

    private final ObjectMapper objectMapper; // 用于转换 JSON
    private final RedisTemplate stringRedisTemplate; // 用于操作 Redis
    private final QuestionsMapper questionMapper;
    private final ScheduleSlotsMapper slotMapper;
    private final InitialVisitApplicationsMapper applicationsMapper;
    private static final String CACHE_KEY_PREFIX = "questionnaire:struct:";



    @Override
    public List<QuestionVO> getRandomQuestions() {
        // 1. 使用 MyBatis-Plus 的 QueryWrapper，并利用数据库的随机排序功能
        QueryWrapper<Questions> wrapper = new QueryWrapper<>();
        wrapper.eq("is_active", true)
                .last("ORDER BY RAND() LIMIT 20"); // 核心：随机排序并取前20条

        List<Questions> questions = questionMapper.selectList(wrapper);

        // 2. 转换为 VO 并返回
        return questions.stream().map(q -> {
            QuestionVO vo = new QuestionVO();
            vo.setId(q.getId());
            vo.setQuestionText(q.getQuestionText());
            return vo;
        }).collect(Collectors.toList());
    }


    @Override
    public EvaluationResult evaluate(Map<Long, Long> answers) {
        EvaluationResult evaluationResult = evaluateAnswers(answers);
        return evaluationResult;
    }

    @Override
    public Questions createQuestion(QuestionCreateDto createDto) {
        // 检查题目内容是否已存在，防止重复录入
        if (questionMapper.exists(new QueryWrapper<Questions>().eq("question_text", createDto.getQuestionText()))) {
            throw new BaseException("创建失败，已存在相同的题目内容");
        }

        Questions question = new Questions();
        BeanUtils.copyProperties(createDto, question);
        // 因为选项是固定的，所以 options 字段可以不存或存一个固定的标识
        // question.setOptions(null);

        questionMapper.insert(question);
        return question;
    }

    @Override
    public Page<Questions> listQuestions(QuestionQueryDto queryDto) {
        Page<Questions> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        QueryWrapper<Questions> wrapper = new QueryWrapper<>();

        // 构建动态查询条件
        if (StringUtils.hasText(queryDto.getQuestionText())) {
            wrapper.like("question_text", queryDto.getQuestionText());
        }
        if (queryDto.getIsCrisisIndicator() != null) {
            wrapper.eq("is_crisis_indicator", queryDto.getIsCrisisIndicator());
        }
        if (queryDto.getIsActive() != null) {
            wrapper.eq("is_active", queryDto.getIsActive());
        }

        // 按创建时间倒序
        wrapper.orderByDesc("created_at");

        questionMapper.selectPage(page, wrapper);
        return page;
    }

    @Override
    public Questions updateQuestion(Long id, QuestionUpdateDto updateDto) {
        Questions question = questionMapper.selectById(id);
        BeanUtils.copyProperties(updateDto, question);
        questionMapper.updateById(question);
        return question;
    }

    @Override
    public void deleteQuestion(Long id) {
        if(questionMapper.deleteById(id) != 1)
            throw new BaseException( "题目删除失败");
        log.info("题目删除成功");
        return;
    }

    /**
     * 评估答案的私有方法
     */
    private EvaluationResult evaluateAnswers(Map<Long, Long> answers) {
        int totalScore = 0;
        // 固定分数映射

        for (Map.Entry<Long, Long> entry : answers.entrySet()) {
            totalScore += entry.getValue();
        }

        // 判断是否紧急 (这里可以加入更复杂的逻辑，比如检查高危题)
        boolean isUrgent = totalScore > 80; // 假设总分200，超过150为紧急

        return new EvaluationResult(totalScore, isUrgent);
    }

    /**
     * 创建用于存储的问卷快照JSON
     */
    private String createQuestionnaireSnapshot(Map<Long, String> answers) {
        // 1. 一次性查出所有相关题目的文本内容
        List<Long> questionIds = new ArrayList<>(answers.keySet());
        Map<Long, String> questionTextMap = questionMapper.selectBatchIds(questionIds)
                .stream()
                .collect(Collectors.toMap(Questions::getId, Questions::getQuestionText));

        // 2. 组装成一个包含题目、答案的列表
        List<Map<String, Object>> snapshotList = answers.entrySet().stream().map(entry -> {
            Map<String, Object> snapshotEntry = new HashMap<>();
            snapshotEntry.put("questionId", entry.getKey());
            snapshotEntry.put("questionText", questionTextMap.getOrDefault(entry.getKey(), "题目已删除"));
            snapshotEntry.put("studentAnswer", entry.getValue());
            return snapshotEntry;
        }).collect(Collectors.toList());

        // 3. 序列化为JSON字符串
        try {
            return new ObjectMapper().writeValueAsString(snapshotList);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }






}

