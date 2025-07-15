package com.example.scupsychological.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.pojo.vo.ApplicationInterviewerVO;


public interface AppInterviewerService {
    public Page<ApplicationInterviewerVO> listAssignedToMe(long pageNum, long pageSize,Long userId);

    ApplicationInterviewerVO getAssignedDetail(Long userId, Long id);
}
