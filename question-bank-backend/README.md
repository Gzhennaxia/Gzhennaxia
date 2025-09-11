# 题库系统后端

这是一个基于Spring Boot的题库管理系统后端，支持PDF文件解析和图片提取功能。

## 功能特性

- **PDF解析**: 支持上传PDF文件并提取其中的文本内容
- **图片提取**: 自动提取PDF中的图片并保存到本地
- **题目管理**: 完整的CRUD操作，支持多种题目类型
- **分页查询**: 支持分页获取题目列表
- **搜索功能**: 支持按关键字搜索题目
- **统计功能**: 提供题目数量统计
- **RESTful API**: 标准的REST接口设计

## 技术栈

- **框架**: Spring Boot 3.2.0
- **数据库**: H2 (开发环境) / MySQL (生产环境)
- **ORM**: Spring Data JPA
- **PDF处理**: Apache PDFBox 3.0.1
- **构建工具**: Maven
- **Java版本**: 17

## 项目结构

```
src/main/java/com/questionbank/
├── QuestionBankApplication.java    # 主启动类
├── controller/                     # 控制器层
│   ├── PdfController.java         # PDF处理接口
│   └── QuestionController.java    # 题目管理接口
├── service/                       # 服务层
│   ├── PdfParserService.java      # PDF解析服务
│   ├── ImageStorageService.java   # 图片存储服务
│   └── QuestionService.java       # 题目管理服务
├── repository/                    # 数据访问层
│   ├── QuestionRepository.java    # 题目数据访问
│   └── QuestionImageRepository.java # 图片数据访问
├── entity/                        # 实体类
│   ├── Question.java             # 题目实体
│   ├── QuestionType.java         # 题目类型枚举
│   └── QuestionImage.java        # 题目图片实体
└── dto/                          # 数据传输对象
    └── PdfParseResult.java       # PDF解析结果
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+

### 运行步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd question-bank-backend
   ```

2. **编译项目**
   ```bash
   mvn clean compile
   ```

3. **运行应用**
   ```bash
   mvn spring-boot:run
   ```

4. **访问应用**
   - 应用地址: http://localhost:8080
   - H2控制台: http://localhost:8080/h2-console
   - API文档: http://localhost:8080/swagger-ui.html (如果集成了Swagger)

## API接口

### PDF处理接口

- `POST /api/pdf/upload` - 上传并解析PDF文件
- `POST /api/pdf/parse-by-pages` - 按页解析PDF文件
- `GET /api/pdf/supported-formats` - 获取支持的文件格式

### 题目管理接口

- `GET /api/questions` - 获取题目列表（分页）
- `GET /api/questions/{id}` - 根据ID获取题目
- `POST /api/questions` - 创建题目
- `PUT /api/questions/{id}` - 更新题目
- `DELETE /api/questions/{id}` - 删除题目
- `GET /api/questions/search?keyword=xxx` - 搜索题目
- `GET /api/questions/type/{type}` - 根据类型获取题目
- `GET /api/questions/subject/{subject}` - 根据科目获取题目
- `GET /api/questions/stats` - 获取题目统计信息

## 题目类型

系统支持以下题目类型：

- `SINGLE_CHOICE` - 单选题
- `MULTIPLE_CHOICE` - 多选题
- `TRUE_FALSE` - 判断题
- `FILL_BLANK` - 填空题
- `SHORT_ANSWER` - 简答题
- `ESSAY` - 论述题

## 配置说明

### 数据库配置

默认使用H2内存数据库，生产环境可切换到MySQL：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/question_bank
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 文件上传配置

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 50MB
```

## 开发指南

### 添加新的题目类型

1. 在 `QuestionType` 枚举中添加新类型
2. 更新相关的服务和控制器逻辑
3. 如需要，更新数据库迁移脚本

### 扩展PDF解析功能

1. 在 `PdfParserService` 中添加新的解析方法
2. 更新 `PdfParseResult` DTO
3. 在 `PdfController` 中添加对应的接口

## 测试

运行单元测试：
```bash
mvn test
```

## 部署

### 打包应用
```bash
mvn clean package
```

### 运行JAR包
```bash
java -jar target/question-bank-backend-1.0.0.jar
```

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

如有问题或建议，请通过以下方式联系：

- 邮箱: your-email@example.com
- 项目地址: https://github.com/your-username/question-bank-backend