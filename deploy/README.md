# 部署辅助文件



| 文件/目录 | 说明 |

|-----------|------|

| **`build.ps1`** | **开发机**一键构建（PowerShell） |

| `build-backend.ps1` | 仅构建后端 |

| **`server/`** | **服务器** bat 脚本（打入发布包） |

| `nginx-gzhennaxia.conf` | Nginx 站点配置 |

| `config/application-prod.yml.example` | 生产配置模板（**不提交 Git**） |



## 服务器一键启停（`deploy/server/`）



| 脚本 | 说明 |

|------|------|

| **`start.bat`** | 一键启动（后端 javaw 后台无窗口 + Nginx） |

| **`stop.bat`** | 一键停止 |

| `start-backend-console.bat` | 前台窗口启动（排错） |

| `view-backend-log.bat` | 查看 `logs\backend.log` |

| `_config.bat` | 路径配置 |



## 开发机构建



```powershell

.\deploy\build.ps1

```



发布包含 `start.bat`、`stop.bat`、JAR、`dist`、`config` 等，解压到 `E:\SOFTWARE\gzhennaxia\` 后双击 `start.bat` 即可。



```powershell

copy deploy\config\application-prod.yml.example deploy\config\application-prod.yml

```



详见 [docs/deploy-windows-server.md](../docs/deploy-windows-server.md)。


