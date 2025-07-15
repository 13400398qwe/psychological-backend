package com.example.scupsychological.controller.student;

import com.example.scupsychological.common.Result;
import com.example.scupsychological.pojo.dto.QuestionQueryDto;
import com.example.scupsychological.pojo.entity.Questions;
import com.example.scupsychological.pojo.vo.QuestionVO;
import com.example.scupsychological.service.QuestionnaireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "学生获取问卷模块")
@RestController
@RequestMapping("/api/student/questionnaires")
@RequiredArgsConstructor
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;
    @GetMapping("/random")
    public Result<List<QuestionVO>> getRandomQuestions() {
        return Result.success(questionnaireService.getRandomQuestions());
    }
}