package com.example.scupsychological.pojo.vo;


import lombok.Data;


@Data
public class QuestionVO {
    private Long id;       // 题目ID
    private String questionText; // 题目内容
    // 注意：这里不需要返回选项，因为前端UI可以写死 A/B/C/D 四个固定选项
}