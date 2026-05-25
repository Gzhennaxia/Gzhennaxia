# Windows Server 部署指南（无 Hyper-V）

适用于 **Windows Server 2016** 等云主机：宿主机未开启嵌套虚拟化，**无法安装 Hyper-V**，但可安装 **Containers** 功能。

> **重要**：本仓库 `docker-compose.yml` 使用 `postgres:12-alpine`、`nginx:alpine` 等 **Linux 镜像**。在仅支持 **Windows 容器** 的 Server 2016 上，往往无法直接 `docker compose up`。  
> **推荐**：在服务器上 **原生安装 PostgreSQL 12 + JDK 17 + Nginx**，稳定、资源占用可控。

---

## 路径对照（开发机 / 服务器）

| 组件 | 开发机（本机打包） | 服务器（运行） |
|------|-------------------|----------------|
| JDK 17 | `D:\SOFTERWARE\JDK\JDK17` | `E:\SOFTWARE\Java\jdk-17` |
| 项目目录 | 仓库 `E:\CODE\Gzhennaxia` | `E:\SOFTWARE\gzhennaxia` |
| Nginx | — | `E:\SOFTWARE\nginx-1.30.1` |
| PostgreSQL 12 | 本机 `5432` | 本机 `5432` |

### 服务器目录（已安装）

**项目文件统一目录**（需自行创建并上传 JAR、`dist`）：

```text
E:\SOFTWARE\gzhennaxia\
├── web-0.0.1-SNAPSHOT.jar
├── config\application-prod.yml
├── dist\                      ← 前端静态资源
├── nginx-gzhennaxia.conf
├── start.bat                  ← 双击一键启动（后端 + Nginx）
├── stop.bat                   ← 双击一键停止
├── _config.bat                ← 路径配置（记事本修改）
└── README.txt
```

---

## 一、架构示意

```
浏览器 → :80（Nginx 静态前端 + 反向代理 /api）
              ↓
         Spring Boot :9527（web 模块 JAR）
              ↓
         PostgreSQL 12 :5432（本机）
```

---

## 二、服务器环境准备

| 组件 | 版本 | 说明 |
|------|------|------|
| JDK | **17** | `E:\SOFTWARE\Java\jdk-17` |
| PostgreSQL | **12** | 与开发机一致 |
| Nginx | **1.30.1** | `E:\SOFTWARE\nginx-1.30.1` |
| Node.js | 18+ | 仅在**开发机**打包前端时需要 |

### 1. PostgreSQL 12

本机连接信息（写入本地 `deploy/config/application-prod.yml`，**勿提交 Git**）：

| 项 | 值 |
|----|-----|
| 主机 | `127.0.0.1` |
| 端口 | `5432` |
| 数据库 | `gzhennaxia` |
| 用户 | `postgres` |
| 密码 | 见本地配置文件 |

```sql
CREATE DATABASE gzhennaxia ENCODING 'UTF8';
```

开发机先复制配置模板并填写密码：

```powershell
copy deploy\config\application-prod.yml.example deploy\config\application-prod.yml
# 编辑 deploy\config\application-prod.yml
```

按顺序执行建表脚本：

```text
backend/web/src/main/resources/db/schema.sql
backend/web/src/main/resources/db/accounting.sql
backend/web/src/main/resources/db/question-bank.sql
```

### 2. 验证 JDK

```powershell
E:\SOFTWARE\Java\jdk-17\bin\java.exe -version
```

应显示 `17.x`。

### 3. 防火墙与安全组

| 端口 | 用途 |
|------|------|
| **80** | Nginx 网站（对公网开放） |
| 443 | HTTPS（可选） |
| 9527 | 仅本机；生产由 Nginx 反代，**勿对公网开放** |
| 5432 | 仅本机，**勿对公网开放** |

---

## 三、在开发机打包并上传

### 推荐：一键构建并汇总发布目录

在仓库根目录执行（**JDK 17 + Maven + Node.js 18+**）：

```powershell
.\deploy\build.ps1
```

脚本会：

1. `mvn clean package` 构建后端 JAR  
2. `npm install` + `npm run build` 构建前端  
3. 将待上传文件汇总到 **`deploy\release\`**（目录结构与服务器 `E:\SOFTWARE\gzhennaxia\` 一致）

```text
deploy\release\
├── web-0.0.1-SNAPSHOT.jar
├── dist\                      ← 前端静态资源
├── config\application-prod.yml
├── nginx-gzhennaxia.conf
├── start.bat / stop.bat
├── _config.bat / _backend-window.bat
└── README-upload.txt
```

构建完成后会生成 **`deploy\gzhennaxia-release.zip`**（推荐上传此压缩包）。

在服务器解压到 **`E:\SOFTWARE\gzhennaxia\`**（解压后根目录应直接出现 `web-*.jar`、`dist\`、`config\` 等，勿多套一层 `release` 文件夹）：

```powershell
Expand-Archive -Path E:\path\to\gzhennaxia-release.zip -DestinationPath E:\SOFTWARE\gzhennaxia\ -Force
```

也可将 **`deploy\release\`** 目录内容直接复制到服务器。发布包内 `config\application-prod.yml` 来自本地 `deploy/config/application-prod.yml`（需先由 `.example` 复制并填写密码）。

可选参数：

```powershell
.\deploy\build.ps1                    # 已有 node_modules 时默认跳过 npm install
.\deploy\build.ps1 -InstallDeps       # 强制 npm install
.\deploy\build.ps1 -SkipNpmInstall    # 显式跳过 npm install（遇 EBUSY 时用）
.\deploy\build.ps1 -BackendOnly       # 仅后端（见 deploy\build-backend.ps1）
.\deploy\build.ps1 -FrontendOnly      # 仅前端，保留 release 中已有 JAR
```

### 手动分步（可选）

**后端**：必须用 JDK 17 跑 Maven（Spring Boot 3 不支持 Java 8）。

```powershell
cd E:\CODE\Gzhennaxia\backend
$env:JAVA_HOME = "D:\SOFTERWARE\JDK\JDK17"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path
mvn clean package -DskipTests -pl web -am
```

**前端**：

```powershell
cd frontend
npm install
npm run build
```

---

## 四、后端配置与启动

### 1. 配置文件

一键构建会把本地 `deploy/config/application-prod.yml` 复制到发布包（该文件已加入 `.gitignore`）。服务器上路径为 `E:\SOFTWARE\gzhennaxia\config\application-prod.yml`。

首次在开发机准备：

```powershell
copy deploy\config\application-prod.yml.example deploy\config\application-prod.yml
```

编辑 `deploy\config\application-prod.yml`，设置 `spring.datasource.password` 等项后执行 `.\deploy\build.ps1`。

若未使用构建脚本，可手动将上述文件复制到服务器 `config\` 目录。

### 2. 启动与停止

| 操作 | 脚本 |
|------|------|
| **一键启动**（后端 + Nginx） | 双击 `start.bat` |
| **一键停止** | 双击 `stop.bat` |
| 前台调试后端 | `start-backend-console.bat` |
| 查看后端日志 | `view-backend-log.bat` |

首次请用记事本编辑 `_config.bat`，确认 `APP_DIR`、`JAVA_HOME`、`NGINX_HOME`。

`start.bat` 通过 **`_run-hidden.vbs`** 无窗口运行 **`_run-backend-cmd.bat`**（不依赖 PowerShell，适配 Server 2016）。日志：

- `logs\launcher.log` — 若出现 `backend already running` 但网站打不开：旧版误判，请用新版（以 **9527 端口监听** 为准）；先 `stop.bat` 再 `start.bat` 或双击 `restart.bat`
- `logs\backend-stderr.log` — Java/Spring 报错

失败时 `start.bat` 会打印日志；或运行 `view-backend-log.bat`。

### 3. 验证后端

浏览器或 curl：

```text
http://127.0.0.1:9527/api/accounting/accounts
```

应返回 JSON，而非连接拒绝。

### 4. 注册为 Windows 服务（可选）

[NSSM](https://nssm.cc/) 示例：

- **Application**：`E:\SOFTWARE\Java\jdk-17\bin\java.exe`
- **Arguments**：`-jar E:\SOFTWARE\gzhennaxia\web-0.0.1-SNAPSHOT.jar`
- **Startup directory**：`E:\SOFTWARE\gzhennaxia`
- **Environment**：`SPRING_PROFILES_ACTIVE=prod`，`SPRING_CONFIG_ADDITIONAL_LOCATION=file:E:/SOFTWARE/gzhennaxia/config/`

---

## 五、Nginx 部署（推荐，已安装 1.20.1）

### 1. 配置站点

仓库提供模板：`deploy/nginx-gzhennaxia.conf`。

在服务器上：

1. 将仓库 `deploy/nginx-gzhennaxia.conf` 复制到 `E:\SOFTWARE\gzhennaxia\nginx-gzhennaxia.conf`（默认 `root` 已指向 `E:/SOFTWARE/gzhennaxia/dist`）。
2. 编辑主配置 `E:\SOFTWARE\nginx-1.30.1\conf\nginx.conf`，在 `http { }` 内增加一行：

```nginx
include E:/SOFTWARE/gzhennaxia/nginx-gzhennaxia.conf;
```

（路径用正斜杠 `/`，Nginx on Windows 推荐写法。）

若主配置里已有 `server { listen 80; ... }` 默认站，可注释掉默认 `server` 块，避免与 80 端口冲突。

### 2. 与一键脚本的关系

`start.bat` 已包含 `nginx.exe -t` 检查并启动 Nginx；`stop.bat` 会执行 `nginx.exe -s stop`。

若仅改 Nginx 配置需重载，在 `E:\SOFTWARE\nginx-1.30.1` 执行：

```bat
nginx.exe -t
nginx.exe -s reload
```

### 3. 访问

- 本机：`http://127.0.0.1/`
- 公网：`http://你的服务器IP/`

前端请求 `/api/...` 会由 Nginx 转发到 `127.0.0.1:9527`。

### 4. 开机自启（可选）

将 `nginx.exe` 加入任务计划程序「系统启动时」运行，或使用 NSSM 注册 Nginx 服务。

---

## 六、方式 B：IIS（未装 Nginx 时）

需安装 IIS、URL Rewrite、ARR。`dist` 根目录放 `web.config` 反代 `/api` → `127.0.0.1:9527`。详见历史版本或自行搜索 IIS 反向代理配置。

---

## 七、关于 Docker / 容器

| 场景 | 说明 |
|------|------|
| Server 2016 + 无 Hyper-V | Linux 容器镜像**通常无法运行** |
| 已安装 Containers | 多为 Windows 容器，与 Linux 镜像不匹配 |
| 当前方案 | **PG12 本机 + JDK17 JAR + Nginx** 即可 |

---

## 八、上线检查清单

- [ ] PostgreSQL 12 已启动，`gzhennaxia` 已建表
- [ ] `java -jar` 正常，本机 `9527` 可访问 API
- [ ] Nginx `nginx.exe -t` 通过，`80` 可打开首页
- [ ] 记账/流水等接口无 502（F12 看 `/api` 请求）
- [ ] 云安全组已放行 **80**
- [ ] `5432`、`9527` 未对公网开放
- [ ] `application-prod.yml` 密码未提交 Git

---

## 九、常见问题

**Q：Hyper-V 装不上？**  
A：云厂商限制嵌套虚拟化；用本指南原生部署即可。

**Q：页面能开，接口 502？**  
A：先确认后端 JAR 在跑；再查 Nginx `proxy_pass` 是否为 `http://127.0.0.1:9527/api/`（注意末尾 `/` 与 `location /api/` 配对）。

**Q：Nginx 启动报端口占用？**  
A：`netstat -ano | findstr :80` 查占用进程；关闭 IIS 默认站或其它占 80 的程序。

**Q：后端数据库连接失败？**  
A：核对 `application-prod.yml` 密码、库名、PostgreSQL 服务是否运行。

**Q：想用系统环境变量 JAVA_HOME？**  
A：服务器「环境变量」里设 `JAVA_HOME=E:\SOFTWARE\Java\jdk-17`，`Path` 增加 `%JAVA_HOME%\bin`，之后可直接 `java -jar`。

更多数据库说明见 [postgresql-migration.md](./postgresql-migration.md)。
