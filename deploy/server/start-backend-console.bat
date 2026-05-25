@echo off
chcp 65001 >nul 2>&1
title Gzhennaxia 后端（前台调试）
call "%~dp0_config.bat"

cd /d "%APP_DIR%"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "CONFIG_DIR=%APP_DIR:\=/%/config/"

echo 前台启动，关闭窗口即停止。
echo 日志: %APP_DIR%\logs\backend.log
echo.

"%JAVA_HOME%\bin\java.exe" ^
  -Dspring.profiles.active=prod ^
  "-Dspring.config.additional-location=file:%CONFIG_DIR%" ^
  "-Dlogging.file.name=%APP_DIR:\=/%/logs/backend.log" ^
  -jar "%APP_DIR%\%JAR_NAME%"

pause
