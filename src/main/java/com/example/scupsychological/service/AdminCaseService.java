package com.example.scupsychological.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.pojo.dto.ExtensionApprovalDto;
import com.example.scupsychological.pojo.dto.PendingExtensionQueryDto;
import com.example.scupsychological.pojo.vo.PendingExtensionRequestVO;
import jakarta.validation.Valid;

import java.util.List;

public interface AdminCaseService {
    public void approveExtension(Long requestId, @Valid ExtensionApprovalDto approvalDto);

    public void rejectExtension(Long requestId, @Valid ExtensionApprovalDto approvalDto);

    Page<PendingExtensionRequestVO> findPendingExtensionRequests(PendingExtensionQueryDto queryDto);
}
