package com.example.scupsychological.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("case_extension_requests")
public class CaseExtensionRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long caseId;
    private Long counselorId;
    private Integer requestedSessions;
    private String reason;
    private String status;
    private String adminNotes;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "CaseExtensionRequest{" +
                "id=" + id +
                ", caseId=" + caseId +
                ", counselorId=" + counselorId +
                ", requestedSessions=" + requestedSessions +
                ", reason='" + reason + '\'' +
                ", status='" + status + '\'' +
                ", adminNotes='" + adminNotes + '\'' +
                ", processedAt=" + processedAt +
                ", createdAt=" + createdAt +
                '}';
    }
}
