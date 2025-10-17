# IB Gateway 自动启动功能使用说明

## 功能概述

本功能实现了在Spring Boot应用启动时自动启动IB Gateway，并在应用关闭时自动停止Gateway，简化了IB API的使用流程。

## 主要特性

- ✅ 应用启动时自动启动IB Gateway
- ✅ 应用关闭时自动停止IB Gateway
- ✅ 智能启动检查：每5秒检测一次，启动成功后立即继续
- ✅ 提供REST API管理Gateway状态
- ✅ 支持手动启动/停止/重启Gateway
- ✅ 完善的错误处理和日志记录

## 配置说明

### 1. application.yml配置

```yaml
ib:
  gateway:
    enabled: true  # 是否启用自动启动功能
    path: D:/SOFTERWARE/clientportal.beta.gw  # Gateway安装路径
    startup:
      timeout: 30  # 启动超时时间（秒）
```

### 2. 配置参数说明

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `ib.gateway.enabled` | boolean | true | 是否在应用启动时自动启动Gateway |
| `ib.gateway.path` | string | D:/SOFTERWARE/clientportal.beta.gw | IB Gateway的安装路径 |
| `ib.gateway.startup.timeout` | int | 30 | Gateway启动超时时间（秒） |

## API接口

### 1. 获取Gateway状态

```http
GET /api/ib-gateway/status
```

**响应示例：**
```json
{
  "isRunning": true,
  "timestamp": 1748520328000
}
```

### 2. 启动Gateway

```http
POST /api/ib-gateway/start
```

**响应示例：**
```json
{
  "message": "IB Gateway 启动命令已发送",
  "status": "success"
}
```

### 3. 停止Gateway

```http
POST /api/ib-gateway/stop
```

**响应示例：**
```json
{
  "message": "IB Gateway 已停止",
  "status": "success"
}
```

### 4. 重启Gateway

```http
POST /api/ib-gateway/restart
```

**响应示例：**
```json
{
  "message": "IB Gateway 重启命令已发送",
  "status": "success"
}
```

## 使用流程

### 1. 自动启动模式（推荐）

1. 确保IB Gateway已正确安装在配置路径
2. 在`application.yml`中配置正确的Gateway路径  
3. 启动Spring Boot应用
4. 应用会自动启动IB Gateway并智能检测启动状态
5. 启动成功后，在浏览器中访问 https://localhost:5000 进行登录

**优势：**
- ⚡ **快速启动**：Gateway启动成功后立即继续，无需等待固定时间
- 🎯 **精确检测**：通过API健康检查确保Gateway真正可用
- 📈 **效率提升**：平均可节省10-20秒的启动等待时间

### 2. 手动管理模式

1. 设置`ib.gateway.enabled=false`禁用自动启动
2. 使用REST API手动管理Gateway状态
3. 调用`POST /api/ib-gateway/start`启动Gateway

## 日志说明

Gateway启动相关的日志会记录在应用日志中：

```
2024-01-26 10:00:00.000  INFO --- [main] IBGatewayService : 正在启动 IB Gateway...
2024-01-26 10:00:05.000  INFO --- [main] IBGatewayService : IB Gateway 进程已启动，进程ID: 12345
2024-01-26 10:00:05.100  INFO --- [main] IBGatewayService : 开始检查 IB Gateway 启动状态，最多等待 30 秒
2024-01-26 10:00:10.000  INFO --- [main] IBGatewayService : 第 1 次检查：IB Gateway 尚未就绪，继续等待...
2024-01-26 10:00:15.000  INFO --- [main] IBGatewayService : 第 2 次检查：IB Gateway 尚未就绪，继续等待...
2024-01-26 10:00:20.000  INFO --- [main] IBGatewayService : IB Gateway 健康检查通过，启动成功！总等待时间: 15 秒
2024-01-26 10:00:20.100  INFO --- [main] IBGatewayService : IB Gateway 启动成功，请在浏览器中访问 https://localhost:5000 进行登录
```

### 智能启动检查说明

- 🔍 **启动检查机制**：不再固定等待超时时间，而是每5秒检查一次Gateway状态
- ⚡ **快速响应**：一旦检测到Gateway启动成功，立即继续执行，减少不必要的等待
- 📊 **进度提示**：实时显示检查进度和等待时间
- 🛡️ **双重检查**：同时检查进程状态和API健康状态，确保Gateway真正可用
- ⏱️ **超时保护**：如果在配置的超时时间内仍未启动成功，会自动停止并报错

## 故障排除

### 1. Gateway启动失败

**可能原因：**
- IB Gateway路径配置错误
- `run.bat`文件不存在
- 权限不足
- 端口5000被占用

**解决方案：**
1. 检查`ib.gateway.path`配置是否正确
2. 确认Gateway安装目录下存在`bin/run.bat`文件
3. 以管理员权限运行应用
4. 检查5000端口是否被其他程序占用

### 2. Gateway无法停止

**可能原因：**
- Gateway进程已异常退出
- 权限不足

**解决方案：**
1. 使用任务管理器手动结束相关进程
2. 重启应用

### 3. API调用失败

**可能原因：**
- Gateway未启动
- 网络连接问题
- SSL证书问题

**解决方案：**
1. 检查Gateway状态：`GET /api/ib-gateway/status`
2. 确认Gateway已正确启动并监听5000端口
3. 检查SSL证书配置

## 安全注意事项

1. **路径安全**：确保Gateway安装路径只有授权用户可以访问
2. **API安全**：建议为Gateway管理API添加认证机制
3. **网络安全**：IB Gateway默认使用HTTPS，请确保证书配置正确

## 进阶配置

### 1. 自定义启动参数

如需修改Gateway启动参数，可以编辑`IBGatewayService.java`中的ProcessBuilder配置：

```java
ProcessBuilder processBuilder = new ProcessBuilder(
    "cmd.exe", "/c", "bin\\run.bat", "root\\conf.yaml", "--additional-param"
);
```

### 2. 监控Gateway状态

可以配合Spring Boot Actuator实现Gateway状态监控：

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info
  health:
    custom:
      enabled: true
```

## 版本兼容性

- Spring Boot: 3.2.1+
- Java: 21+
- IB Gateway: Client Portal Beta版本

## 更新日志

- v1.0.0: 初始版本，支持基本的启动/停止功能
- v1.1.0: 添加REST API管理接口
- v1.2.0: 完善错误处理和日志记录
- v1.3.0: 🚀 添加智能启动检查机制，每5秒检测一次，启动成功后立即继续，大幅提升启动效率 