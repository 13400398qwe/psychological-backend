package com.example.scupsychological.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.pojo.dto.*;
import com.example.scupsychological.pojo.vo.ApplicationDetailVO;
import com.example.scupsychological.pojo.vo.ApplicationListVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

public interface InitialVisitApplicationService {
    @Transactional
    void createApplication(Long studentId, StudentApplicationCreateDto createDto);

    ApplicationDetailVO reviewApplication(Long id, @Valid ApplicationReviewDto reviewDto);


    //分页查询所有初访问申请记录
    Page<ApplicationListVO> listAllApplications(ApplicationAdminQueryDto queryDto);

    ApplicationDetailVO createApplicationForStudent(@Valid AdminApplicationCreateDto createDto);

    void cancelApplication(Long userId, Long id);

    ApplicationDetailVO updateApplication(Long id, @Valid AdminApplicationUpdateDto updateDto);

    Page<ApplicationListVO> listMyApplications(Long userId,long page,long size);

    void cancelApplicationByAdmin(Long id);
}
