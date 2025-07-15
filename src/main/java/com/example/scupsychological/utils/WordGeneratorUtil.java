package com.example.scupsychological.utils;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Word 文档生成工具类 (示例)
 * 这是一个简化的示例，用于演示如何将数据填充到 Word 文档中。
 * 生产环境推荐使用更强大的模板引擎，如 FreeMarker 配合 XML 模板来生成复杂的 Word 文档。
 */
@Component
public class WordGeneratorUtil {

    // 【修正1】定义一个集合，存放我们希望优先处理并有特定中文翻译的字段
    private static final Set<String> PREDEFINED_FIELDS = new HashSet<>(Arrays.asList(
            "studentName", "studentUsername", "studentPhone",
            "problemType", "totalSessions"
    ));

    public byte[] createWord(Map<String, Object> dataModel) throws IOException {
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // 标题
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText("心理咨询结案报告");
            titleRun.setBold(true);
            titleRun.setFontSize(20);
            titleRun.addBreak();

            // 【修正2】分两步写入数据

            // 第一步：按预设顺序写入我们已知的、重要的字段
            List<String> fieldOrder = List.of("studentName", "studentUsername", "studentPhone", "problemType", "totalSessions");
            for (String key : fieldOrder) {
                if (dataModel.containsKey(key)) {
                    writeParagraph(document, translateKeyToChinese(key), String.valueOf(dataModel.get(key)));
                }
            }

            // 添加一个分隔段落
            document.createParagraph().createRun().addBreak();

            // 第二步：遍历 Map 中所有【剩下】的、来自 reportContent 的动态字段
            dataModel.entrySet().stream()
                    .filter(entry -> !PREDEFINED_FIELDS.contains(entry.getKey())) // 过滤掉已经处理过的字段
                    .forEach(entry -> {
                        // 对于这些动态字段，我们直接使用它们的键作为标签
                        writeParagraph(document, entry.getKey(), String.valueOf(entry.getValue()));
                    });

            document.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new IOException("生成Word文档失败", e);
        }
    }

    /**
     * 写入一个段落的辅助方法
     */
    private void writeParagraph(XWPFDocument document, String key, String value) {
        XWPFParagraph p = document.createParagraph();

        XWPFRun keyRun = p.createRun();
        keyRun.setBold(true);
        keyRun.setText(key + "：");

        XWPFRun valueRun = p.createRun();
        valueRun.setText(value);
    }

    /**
     * 一个简单的键名翻译器，用于将预设字段的英文键名翻译成中文标签
     */
    private String translateKeyToChinese(String key) {
        return switch (key) {
            case "studentName" -> "来访者姓名";
            case "studentUsername" -> "来访者学号";
            case "studentPhone" -> "来访者联系电话";
            case "problemType" -> "问题类型";
            case "totalSessions" -> "咨询总次数";
            default -> key; // 对于动态字段，直接返回原始键名
        };
    }
}
