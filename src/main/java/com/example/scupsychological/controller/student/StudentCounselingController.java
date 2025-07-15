package com.example.scupsychological.controller.student;

import com.example.scupsychological.common.Result;
import com.example.scupsychological.pojo.vo.CounselingCaseVO;
import com.example.scupsychological.pojo.vo.CounselingSessionVO;
import com.example.scupsychological.security.LoginUser;
import com.example.scupsychological.service.CounselingStudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Tag(name = "学生 - 我的正式咨询")
@RestController
@RequestMapping("/api/student/counseling-cases")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentCounselingController {

    private final CounselingStudentService counselingStudentService;

    @GetMapping
    @Operation(summary = "获取我的所有正式咨询个案")
    public Result<List<CounselingCaseVO>> listMyCases(@AuthenticationPrincipal LoginUser loginUser) {
        List<CounselingCaseVO> cases = counselingStudentService.findMyCases(loginUser.getUserId());
        return Result.success(cases);
    }

    @GetMapping("/{caseId}/sessions")
    @Operation(summary = "获取我某个案下的所有咨询安排")
    public Result<List<CounselingSessionVO>> listMySessions(
            @PathVariable Long caseId,
            @AuthenticationPrincipal LoginUser loginUser) {

        List<CounselingSessionVO> sessions = counselingStudentService.findMySessions(loginUser.getUserId(), caseId);
        return Result.success(sessions);
    }
}
