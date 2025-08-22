欢迎来到 SCU 心理咨询平台项目 Wiki！
本文档旨在为本项目的开发者、维护者和贡献者提供一个全面、清晰的指南。

目录
项目概述

核心功能

技术栈

模块详解

通用模块

学生模块

初访员模块

咨询师模块

心理助理模块

后台管理模块

数据库设计

环境搭建与配置指南

项目概述
SCU 心理咨询平台是一个专为高校设计的心理健康服务管理系统。平台旨在通过数字化的方式，连接学生、初访员、咨询师、心理助理和中心管理员，提供从在线预约、初步评估、正式咨询到结案归档的全流程管理，从而提升心理咨询服务的效率、规范性和可及性。

核心功能
多角色权限管理: 系统预设学生、初访员、咨询师、心理助理、管理员五种角色，通过 Spring Security 进行严格的权限控制。

灵活的排班与号源管理: 管理员可以创建周期性的值班模板，并一键生成未来的可预约号源（Slots）。支持手动补录、修改和删除号源。

标准化的初访流程:

学生在线填写标准化问卷并发起初访预约。

系统自动对问卷进行评分，并评估紧急状态。

管理员审核申请，并将任务指派给初访员。

初访员提交专业的初访记录，并给出处理结论。

结构化的正式咨询管理:

心理助理根据初访结论，将学生转介给合适的咨询师，并安排首次及后续多次咨询。

咨询师可以管理自己的个案，录入每次的咨询反馈。

支持咨询加时申请与审批流程。

完善的结案与报告体系:

咨询师在咨询结束后，可提交结构化的结案报告。

管理员可根据多种条件查询、统计和批量导出结案报告为 Word 文档。

全面的统计分析:

系统支持对咨询师工作量进行统计，并可导出为 Excel 文件。

支持对已结案的个案进行多维度的高级查询。

自动化与提醒:

系统包含定时任务，可自动清理过期的号源。

集成短信服务，在关键节点（如预约成功、取消）向用户发送通知。

技术栈
分类

技术

描述

后端框架

Spring Boot 3.5.3

项目基础框架。

认证与授权

Spring Security, JWT

用于实现用户认证和基于角色的权限控制。

数据库 ORM

MyBatis-Plus

提供强大的 CRUD 功能和分页插件，简化数据库操作。

数据库

MySQL

关系型数据库，用于存储所有业务数据。

缓存

Redis

用于 JWT 黑名单管理，提升性能。

API 文档

SpringDoc (Swagger UI)

自动生成交互式 API 文档，方便前后端协作。

文件处理

EasyExcel, Apache POI

用于生成和导出 Excel 工作量统计及 Word 结案报告。

文件存储

阿里云 OSS

用于存储用户头像等文件。

消息服务

阿里云短信服务

用于发送短信通知。

构建工具

Maven

项目构建和依赖管理。

模块详解
通用模块
common: 存放全局常量、枚举（性别、角色）、统一响应结果 (Result, ResultCode) 和全局异常处理器。

config: 包含项目的核心配置，如 Spring Security (SecurityConfig), MyBatis-Plus (MyBatisPlusConfig), Redis (RedisConfig), CORS 跨域 (WebMvcConfig) 和 API 文档 (OpenApiConfig) 等。

filter: JwtAuthenticationFilter 在每个请求前拦截并校验 JWT Token，将用户信息注入 Spring Security 上下文。

utils: 包含各种工具类，如 JWT 生成与解析 (JwtUtil)、Word 文档生成 (WordGeneratorUtil) 等。

学生模块
路径: /api/student/**

主要控制器: InitialVisitController, QuestionnaireController, StudentCounselingController

核心职责:

查询可用的初访时间段和初访员。

获取随机问卷题目并提交初访申请。

查询和管理自己的初访申请记录（包括撤销）。

查看自己被转介后的正式咨询个案详情及所有咨询安排。

初访员模块
路径: /api/interviewer/**

主要控制器: InterviewerController

核心职责:

查看由管理员指派给自己的初访任务列表。

获取单个初访任务的详细信息，包括学生信息和问卷快照。

在初访结束后，提交专业的初访评估记录。

查询由自己处理过的所有历史初访记录。

查看自己的日程安排。

咨询师模块
路径: /api/counselor/**

主要控制器: CounselorController

核心职责:

管理和查询由心理助理分配给自己的正式咨询个案。

查看个案详情，包括学生信息、初访评估结论和所有咨询安排。

录入和更新每一次单次咨询的状态和备注。

当认为需要更多咨询次数时，向中心管理员发起加时申请。

在咨询周期结束后，提交结构化的结案报告。

将结案报告导出为 Word 文档。

查看自己的日程安排。

心理助理模块
路径: /api/assistant/**

主要控制器: AssistantController

核心职责:

查看所有已完成初访且结论为“建议转介”的学生列表（待处理池）。

查询咨询师的可用排班，为学生创建新的咨询个案。

为新个案一次性安排好后续所有（如8次）的咨询日程。

管理和查询由自己经手安排的所有个案。

后台管理模块
路径: /api/admin/**

主要控制器: AdminApplicationController, AdminCaseController, AdminQuestionnaireController, AdminRecordController, AdminScheduleController, AdminStatisticsController, AdminUserController

核心职责:

用户管理: 对系统内所有角色（管理员、咨询师、学生等）进行增、删、改、查。

排班管理: 创建和管理值班模板，根据模板批量生成号源，并对号源进行手动调整。

申请管理: 审核学生提交的初访申请，进行批准、拒绝或指派操作。支持为学生补录、改约或取消预约。

记录管理: 查询和筛选系统内所有的初访记录。

个案管理: 审批咨询师提交的加时申请。

问卷管理: 维护心理测评问卷的题库。

统计分析: 多维度查询已结案的个案，批量导出结案报告，以及导出咨询师工作量统计报表。

数据库设计
项目采用了基于角色的实体关系模型，核心实体包括：

users: 用户表，通过 role 字段区分不同角色。

schedule_templates: 值班模板表，用于定义周期性的排班规则。

schedule_slots: 可预约时间段表（号源），由模板生成或手动创建。

questions: 问卷题库表。

initial_visit_applications: 初访预约申请表，记录了学生从提交申请到完成初访的全过程。

initial_visit_records: 初访记录表，由初访员填写，是后续咨询的重要依据。

counseling_cases: 正式咨询个案表，管理一个学生与一个咨询师的完整咨询周期。

counseling_sessions: 单次咨询记录表，详细记录每一次咨询的安排和状态。

case_extension_requests: 咨询加时申请表，记录了咨询师的加时申请及其审批状态。

环境搭建与配置指南
数据库配置:

确保你已安装并运行 MySQL 数据库。

在 src/main/resources/application.yml 文件中，修改 spring.datasource 下的 url, username, 和 password 以匹配你的本地数据库设置。

创建一个名为 psychological 的数据库。项目启动时，MyBatis-Plus 会根据实体类自动创建或更新表结构。

Redis 配置:

确保本地或服务器上已安装并运行 Redis。

在 application.yml 中，根据需要修改 spring.data.redis 下的 host 和 port。

云服务配置 (可选):

阿里云 OSS (用于头像上传): 在 application.yml 的 aliyun.oss 部分填入你自己的 endpoint, access-key-id, access-key-secret, 和 bucket-name。

阿里云短信服务: 在 aliyun.sms 部分填入相应的 Key 和模板 Code。

JWT 密钥:

为了安全，强烈建议在 application.yml 的 jwt.secret-key 中设置一个你自己定义的、足够长且复杂的密钥字符串。

运行项目:

配置完成后，可以直接运行主启动类 ScupsychologicalApplication.java 来启动后端服务。

API 文档将默认在 http://localhost:8080/swagger-ui/index.html (端口号以你的配置为准) 路径下可用。
