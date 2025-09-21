# 个人时间管理系统

一个功能完整的个人时间管理应用，支持任务录入、多种视图展示、PWA 离线使用等功能。

## 技术栈

### 后端
- **Spring Boot 3.2.0** - 主框架
- **MyBatis-Plus 3.5.4** - ORM 框架
- **SQLite** - 轻量级数据库
- **Maven** - 依赖管理

### 前端
- **React 18** - 前端框架
- **Vite 5** - 构建工具，支持热重载
- **TypeScript** - 类型安全
- **Ant Design 5** - UI 组件库
- **Day.js** - 日期处理
- **Axios** - HTTP 客户端

### 部署
- **Docker** - 容器化部署
- **Nginx** - 前端服务器
- **PWA** - 渐进式 Web 应用

## 功能特性

### 任务管理
- ✅ 任务创建、编辑、删除
- ✅ 任务状态管理（待处理、进行中、已完成、已取消）
- ✅ 优先级设置（高、中、低）
- ✅ 任务分类和标签
- ✅ 任务搜索和筛选

### 多种视图
- ✅ 日视图 - 查看单日任务
- ✅ 周视图 - 查看一周任务
- ✅ 月视图 - 查看整月任务
- ✅ 多日视图 - 查看连续多日任务
- ✅ 多周视图 - 查看连续多周任务

### 数据统计
- ✅ 任务总数统计
- ✅ 完成率计算
- ✅ 状态分布统计
- ✅ 实时数据更新

### 用户体验
- ✅ 响应式设计，支持移动端
- ✅ PWA 支持，可离线使用
- ✅ 直观的日历界面
- ✅ 快速任务操作

## 快速开始

### 环境要求
- Java 17+
- Node.js 18+
- Docker (可选)

### 本地开发

#### 1. 启动后端
```bash
cd backend
mvn spring-boot:run
```

#### 2. 启动前端
```bash
cd frontend
npm install
npm run dev
```

#### 3. 访问应用
- 前端: http://localhost:3000
- 后端 API: http://localhost:8080

### Docker 部署

#### 1. 构建并启动
```bash
# 构建后端
cd backend
mvn clean package

# 启动所有服务
docker-compose up -d
```

#### 2. 访问应用
- 应用: http://localhost:3000
- API: http://localhost:8080

## API 接口

### 任务管理
- `GET /api/tasks` - 获取所有任务
- `POST /api/tasks` - 创建任务
- `PUT /api/tasks/{id}` - 更新任务
- `DELETE /api/tasks/{id}` - 删除任务
- `GET /api/tasks/{id}` - 获取单个任务

### 任务查询
- `GET /api/tasks/date-range?startDate={start}&endDate={end}` - 按日期范围查询
- `GET /api/tasks/date?date={date}` - 按日期查询
- `GET /api/tasks/status/{status}` - 按状态查询
- `GET /api/tasks/category/{category}` - 按分类查询

## 数据库结构

### tasks 表
```sql
CREATE TABLE tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_time DATETIME,
    end_time DATETIME,
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    status VARCHAR(20) DEFAULT 'PENDING',
    category VARCHAR(100),
    tags TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);
```
## 项目结构

```
personal-manager/
├── backend/                 # 后端服务
│   ├── common/              # 公共模块
│   │   ├── src/main/java/com/gzhennaxia/common/
│   │   │   ├── controller/  # 公共控制器
│   │   │   ├── enums/       # 枚举类
│   │   │   ├── exception/   # 异常处理
│   │   │   ├── mapper/      # 公共数据访问层
│   │   │   ├── pojo/        # 数据传输对象
│   │   │   │   ├── converter/  # 数据转换器
│   │   │   │   ├── dto/        # DTO对象
│   │   │   │   ├── entity/     # 实体类
│   │   │   │   ├── request/    # 请求对象
│   │   │   │   └── vo/         # 视图对象
│   │   │   ├── service/     # 公共服务层
│   │   │   │   └── impl/    # 服务实现
│   │   │   └── utils/       # 工具类
│   │   └── src/test/java/com/gzhennaxia/common/
│   │       ├── controller/  # 控制器测试
│   │       └── service/     # 服务测试
│   ├── question-bank-service/  # 题库服务
│   │   ├── src/main/java/com/gzhennaxia/question/bank/
│   │   │   ├── config/      # 配置类
│   │   │   ├── controller/  # 控制器层
│   │   │   ├── dto/         # 数据传输对象
│   │   │   ├── entity/      # 实体类
│   │   │   ├── mapper/      # 数据访问层
│   │   │   └── service/     # 服务层
│   │   └── src/main/resources/
│   │       ├── application.yml  # 应用配置
│   │       └── data.sql         # 初始化数据
│   ├── todo-service/        # 任务清单服务
│   │   ├── src/main/java/com/gzhennaxia/todo/
│   │   │   ├── config/      # 配置类
│   │   │   ├── controller/  # 控制器层
│   │   │   ├── dto/         # 数据传输对象
│   │   │   ├── entity/      # 实体类
│   │   │   ├── mapper/      # 数据访问层
│   │   │   ├── service/     # 服务层
│   │   │   └── vo/          # 视图对象
│   │   └── src/main/resources/
│   │       └── mapper/      # MyBatis映射文件
│   ├── web/                 # Web入口模块
│   │   ├── src/main/java/com/gzhennaxia/web/
│   │   │   ├── controller/  # 控制器层
│   │   │   │   ├── question/  # 题库相关控制器
│   │   │   │   └── todo/      # 任务相关控制器
│   │   │   └── WebApplication.java  # 应用启动类
│   │   └── src/main/resources/
│   │       ├── application.yml      # 主配置文件
│   │       ├── application-prod.yml # 生产环境配置
│   │       └── application-test.yml # 测试环境配置
│   └── pom.xml              # 父级Maven配置
├── frontend/                # React 前端
│   ├── src/
│   │   ├── components/      # React 组件
│   │   ├── services/        # API 服务
│   │   ├── types/           # TypeScript 类型
│   │   └── utils/           # 工具函数
│   └── public/              # 静态资源
├── docker-compose.yml       # Docker 编排
└── README.md                # 项目文档
```

## 开发计划

### 已完成功能
- [x] 基础任务 CRUD 操作
- [x] 多种日历视图
- [x] 任务搜索和筛选
- [x] 数据统计展示
- [x] 响应式设计
- [x] Docker 部署支持

### 计划功能
- [ ] 任务提醒功能
- [ ] 数据导入导出
- [ ] 任务模板
- [ ] 时间追踪
- [ ] 报表生成
- [ ] 主题切换
- [ ] 多语言支持

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

如有问题或建议，请通过以下方式联系：
- 创建 Issue
- 发送邮件

---

**享受高效的时间管理！** ⏰✨