package com.example.scupsychological.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.pojo.dto.InitialVisitRecordCreateDto;
import com.example.scupsychological.pojo.dto.VisitRecordQueryDto;
import com.example.scupsychological.pojo.entity.InitialVisitRecords;
import com.example.scupsychological.pojo.vo.VisitRecordInterviewerVO;
import jakarta.validation.Valid;

public interface ApplicationRecordService {
    InitialVisitRecords createRecord(Long userId, @Valid InitialVisitRecordCreateDto createDto);

    Page<VisitRecordInterviewerVO> findMyRecordsByPage(Long userId, VisitRecordQueryDto queryDto);
}
