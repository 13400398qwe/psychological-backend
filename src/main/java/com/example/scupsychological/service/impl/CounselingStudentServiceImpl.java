package com.example.scupsychological.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.scupsychological.mapper.CounselingCasesMapper;
import com.example.scupsychological.mapper.CounselingSessionsMapper;
import com.example.scupsychological.pojo.entity.CounselingCases;
import com.example.scupsychological.pojo.entity.CounselingSessions;
import com.example.scupsychological.service.CounselingStudentService;
import com.example.scupsychological.pojo.vo.CounselingCaseVO;
import com.example.scupsychological.pojo.vo.CounselingSessionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CounselingStudentServiceImpl implements CounselingStudentService {

    private final CounselingCasesMapper caseMapper;
    private final CounselingSessionsMapper sessionMapper;

    @Override
    public List<CounselingCaseVO> findMyCases(Long studentId) {
        // 这里需要一个自定义的 Mapper 方法来进行多表 JOIN，以获取咨询师姓名等信息
        // return caseMapper.selectMyCaseVOs(studentId);
        // 此处返回示例，实际开发需实现上面的 Mapper 查询
        return List.of();
    }

    @Override
    public List<CounselingSessionVO> findMySessions(Long studentId, Long caseId) {
        // 1. 安全校验：确认这个个案确实属于当前登录的学生
        CounselingCases myCase = caseMapper.selectOne(
                new QueryWrapper<CounselingCases>()
                        .eq("id", caseId)
                        .eq("student_id", studentId)
        );
        if (myCase == null) {
            throw new AccessDeniedException("您无权查看此个案的咨询安排");
        }

        // 2. 查询该个案下的所有咨询安排
        List<CounselingSessions> sessions = sessionMapper.selectList(
                new QueryWrapper<CounselingSessions>().eq("case_id", caseId).orderByAsc("session_number")
        );

        // 3. 转换为 VO 列表并返回
        return sessions.stream().map(session -> {
            CounselingSessionVO vo = new CounselingSessionVO();
            BeanUtils.copyProperties(session, vo);
            vo.setSessionId(session.getId()); // 确保ID被正确复制
            return vo;
        }).collect(Collectors.toList());
    }
}
