package com.example.scupsychological.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scupsychological.mapper.CounselingCasesMapper;
import com.example.scupsychological.pojo.dto.CaseStatsQueryDto;
import com.example.scupsychological.pojo.dto.WorkloadQueryDto;
import com.example.scupsychological.pojo.vo.CaseReportSummaryVO;
import com.example.scupsychological.pojo.vo.CounselorCaseDetailVO;
import com.example.scupsychological.service.StatisticsService;
import com.example.scupsychological.utils.WordGeneratorUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.scupsychological.pojo.vo.CounselorWorkloadVO;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {
    private final CounselingCasesMapper caseMapper;
    private final WordGeneratorUtil wordGeneratorUtil;
    private final ObjectMapper objectMapper;

    @Override
    public Page<CaseReportSummaryVO> queryClosedCases(CaseStatsQueryDto queryDto) {
        Page<CaseReportSummaryVO> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        // 这里需要一个非常复杂的自定义 Mapper XML 查询，
        // 它会 JOIN counseling_cases, users(学生), users(咨询师), initial_visit_records 等表
        // 并根据 queryDto 中的所有条件进行筛选。
        caseMapper.selectClosedCaseSummaries(page, queryDto);
        return page;
    }

    @Override
    public byte[] exportCounselorWorkloadAsExcel(WorkloadQueryDto queryDto) {
        // 1. 调用一个新的 Mapper 方法，使用 GROUP BY 和 COUNT/SUM 进行聚合查询
        //    这个 SQL 会按 counselor_id 分组，然后统计每个咨询师已完成的咨询次数和总时长。
        List<CounselorWorkloadVO> workloadData = caseMapper.calculateCounselorWorkload(queryDto);

        // 2. 使用一个 Excel 生成库（如 EasyExcel）将 List<CounselorWorkloadVO> 写入到字节数组中
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
             EasyExcel.write(baos, CounselorWorkloadVO.class).sheet("咨询师工作量").doWrite(workloadData);
             return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("生成Excel文件失败", e);
        }
    }

    @Override
    public byte[] exportReportsToZip(CaseStatsQueryDto queryDto) {
        // 1. 获取【所有】匹配条件的个案详情（不分页）
        List<CounselorCaseDetailVO> allCases = caseMapper.selectAllClosedCaseDetails(queryDto);

        // 2. 创建一个内存中的 ZIP 输出流
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            // 3. 遍历每一个个案，为其生成 Word 文档
            for (CounselorCaseDetailVO caseDetail : allCases) {
                // a. 准备数据模型
                Map<String, Object> dataModel = prepareDataModelForWord(caseDetail);
                // b. 调用 Word 生成工具
                byte[] wordBytes = wordGeneratorUtil.createWord(dataModel);

                // c. 将生成的 Word 文件作为一个条目写入 ZIP 流
                ZipEntry zipEntry = new ZipEntry(String.format("结案报告-%s-%s.docx", caseDetail.getStudentName(), caseDetail.getCaseId()));
                zos.putNextEntry(zipEntry);
                zos.write(wordBytes);
                zos.closeEntry();
            }

            zos.finish();
            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("生成ZIP压缩包失败", e);
        }
    }

    private Map<String, Object> prepareDataModelForWord(CounselorCaseDetailVO caseDetail) {
        // 1. 安全检查
        if (caseDetail == null) {
            return Collections.emptyMap();
        }

        // 2. 初始化最终的数据模型 Map
        Map<String, Object> dataModel = new HashMap<>();

        // 3. 首先，解析报告内容（reportContent）这个 JSON 字符串
        if (StringUtils.hasText(caseDetail.getReportContent())) {
            try {
                // 使用 TypeReference 将 JSON 字符串反序列化为一个 Map
                Map<String, Object> reportData = objectMapper.readValue(caseDetail.getReportContent(), new TypeReference<>() {
                });
                // 将解析出的所有键值对，全部放入我们的主数据模型中
                dataModel.putAll(reportData);
            } catch (JsonProcessingException e) {
                log.error("导出报告时，解析 reportContent JSON 失败, caseId: {}", caseDetail.getCaseId(), e);
                // 即使解析失败，我们依然可以继续填充其他信息
            }
        }

        // 4. 然后，将 caseDetail 中其他重要的、非报告内容的信息也加入到 Map 中
        //    这样做的好处是，保证了数据的权威性（例如学生姓名以数据库为准），
        //    并且 Word 模板可以使用更统一的占位符。
        dataModel.put("studentName", caseDetail.getStudentName());
        dataModel.put("studentUsername", caseDetail.getStudentUsername());
        dataModel.put("studentPhone", caseDetail.getStudentPhone());
        dataModel.put("problemType", caseDetail.getProblemType());
        dataModel.put("totalSessions", caseDetail.getTotalSessions());
        // ... 在这里可以添加任何其他您需要在 Word 中显示的字段，例如咨询师姓名、结案日期等 ...
        // dataModel.put("counselorName", caseDetail.getCounselorName());
        // dataModel.put("reportFinalizedAt", caseDetail.getReportFinalizedAt());

        return dataModel;
    }
}

