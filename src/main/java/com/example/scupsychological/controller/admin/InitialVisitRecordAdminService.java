package com.example.scupsychological.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.pojo.dto.AdminRecordQueryDto;
import com.example.scupsychological.pojo.vo.InitialVisitRecordAdminVO;

public interface InitialVisitRecordAdminService {
    public Page<InitialVisitRecordAdminVO> listAllRecordsByPage(AdminRecordQueryDto queryDto);
}
