@echo off
chcp 65001 >nul 2>&1
title Gzhennaxia - 一键停止
setlocal EnableDelayedExpansion

call "%~dp0_config.bat"

echo.
echo ========================================
echo   Gzhennaxia 一键停止（后端 + Nginx）
echo ========================================
echo.

rem ---------- 停止后端（优先 PID 文件）----------
echo [1/2] 停止后端...
set "FOUND=0"
set "PID_FILE=%APP_DIR%\logs\backend.pid"

if exist "%PID_FILE%" (
    set /p "BPID=" < "%PID_FILE%"
    if not "!BPID!"=="" (
        taskkill /F /PID !BPID! >nul 2>&1
        if not errorlevel 1 (
            set "FOUND=1"
            echo       已结束 PID=!BPID! （来自 backend.pid）
        )
    )
)

for /f "skip=1 tokens=1" %%p in ('wmic process where "CommandLine like '%%%JAR_NAME%%%'" get ProcessId 2^>nul') do (
    if not "%%p"=="" (
        set "FOUND=1"
        echo       结束 Java PID=%%p
        taskkill /F /PID %%p >nul 2>&1
    )
)
rem 释放 9527 端口（有时 wmic 匹配不到进程但端口仍占用）
for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":9527" ^| findstr /I "LISTENING 监听"') do (
    if not "%%p"=="" (
        set "FOUND=1"
        echo       结束占用 9527 的 PID=%%p
        taskkill /F /PID %%p >nul 2>&1
    )
)
if "!FOUND!"=="0" (
    echo       未发现后端进程或 9527 端口占用。
)

if exist "%PID_FILE%" del /f /q "%PID_FILE%" >nul 2>&1

rem ---------- 停止 Nginx ----------
echo [2/2] 停止 Nginx...
if exist "%NGINX_HOME%\nginx.exe" (
    cd /d "%NGINX_HOME%"
    nginx.exe -s stop >nul 2>&1
    if errorlevel 1 (echo       Nginx 未运行或已停止.) else (echo       Nginx 已停止.)
) else (
    echo       未找到 Nginx，跳过。
)

echo.
echo [完成]
echo.
pause
endlocal
