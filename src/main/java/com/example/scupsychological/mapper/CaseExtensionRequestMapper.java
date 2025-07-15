package com.example.scupsychological.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.pojo.dto.PendingExtensionQueryDto;
import com.example.scupsychological.pojo.entity.CaseExtensionRequest;
import com.example.scupsychological.pojo.vo.PendingExtensionRequestVO;
import org.apache.ibatis.annotations.Param;

public interface CaseExtensionRequestMapper extends BaseMapper<CaseExtensionRequest> {
    Page<PendingExtensionRequestVO>  selectPendingRequests(Page<PendingExtensionRequestVO> page, @Param("query") PendingExtensionQueryDto queryDto);
}
