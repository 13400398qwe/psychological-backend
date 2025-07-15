package com.example.scupsychological.service;

import com.example.scupsychological.pojo.vo.CounselingCaseVO;
import com.example.scupsychological.pojo.vo.CounselingSessionVO;

import java.util.List;

public interface CounselingStudentService {
    List<CounselingCaseVO> findMyCases(Long userId);

    List<CounselingSessionVO> findMySessions(Long userId, Long caseId);
}
