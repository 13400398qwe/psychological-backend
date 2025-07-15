package com.example.scupsychological.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.common.Result;
import com.example.scupsychological.pojo.dto.QuestionCreateDto;
import com.example.scupsychological.pojo.dto.QuestionQueryDto;
import com.example.scupsychological.pojo.dto.QuestionUpdateDto;
import com.example.scupsychological.pojo.entity.Questions;
import com.example.scupsychological.service.QuestionnaireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "后台管理模块 - 问卷管理")
@RestController
@RequestMapping("/api/admin/questionnaires")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminQuestionnaireController {

    private final QuestionnaireService questionAdminService; // 一个专门处理管理员操作的服务

    @PostMapping
    @Operation(summary = "创建一道新题目到题库")
    public Result<Questions> createQuestion(@Valid @RequestBody QuestionCreateDto createDto) {
        Questions newQuestion = questionAdminService.createQuestion(createDto);
        return Result.success(newQuestion, "题目创建成功");
    }

    @GetMapping
    @Operation(summary = "分页、筛选获取题库中的所有题目")
    public Result<Page<Questions>> listQuestions(QuestionQueryDto queryDto) {
        Page<Questions> questionPage = questionAdminService.listQuestions(queryDto);
        return Result.success(questionPage);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新一道题目")
    public Result<Questions> updateQuestion(@PathVariable Long id, @Valid @RequestBody QuestionUpdateDto updateDto) {
        Questions updatedQuestion = questionAdminService.updateQuestion(id, updateDto);
        return Result.success(updatedQuestion, "题目更新成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除一道题目")
    public Result<Object> deleteQuestion(@PathVariable Long id) {
        questionAdminService.deleteQuestion(id);
        return Result.success("题目删除成功");
    }

}
