package com.example.scupsychological.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.controller.admin.InitialVisitRecordAdminService;
import com.example.scupsychological.mapper.InitialVisitRecordsMapper;
import com.example.scupsychological.pojo.dto.AdminRecordQueryDto;
import com.example.scupsychological.pojo.vo.InitialVisitRecordAdminVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InitialVisitRecordAdminServiceImpl implements InitialVisitRecordAdminService {
    private final InitialVisitRecordsMapper recordMapper;

    @Override
    public Page<InitialVisitRecordAdminVO> listAllRecordsByPage(AdminRecordQueryDto queryDto) {
        // 1. 创建分页对象
        Page<InitialVisitRecordAdminVO> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());

        // 2. 调用自定义的 Mapper 方法，传入分页对象和所有筛选条件
        recordMapper.selectRecordsForAdmin(page, queryDto);

        return page;
    }
}
