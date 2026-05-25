@echo off
chcp 65001 >nul 2>&1
title Gzhennaxia - 一键启动
setlocal EnableDelayedExpansion

call "%~dp0_config.bat"

set "LOG_DIR=%APP_DIR%\logs"
if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

echo.
echo ========================================
echo   Gzhennaxia 一键启动（后端 + Nginx）
echo ========================================
echo.

echo [%date% %time%] start.bat begin>> "%LOG_DIR%\launcher.log"

rem ---------- 检查 ----------
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo [错误] 未找到 JDK: %JAVA_HOME%\bin\java.exe
    echo [%date% %time%] ERROR no java.exe>> "%LOG_DIR%\launcher.log"
    goto :fail
)
if not exist "%APP_DIR%\%JAR_NAME%" (
    echo [错误] 未找到 JAR: %APP_DIR%\%JAR_NAME%
    goto :fail
)
if not exist "%APP_DIR%\config\application-prod.yml" (
    echo [错误] 未找到: %APP_DIR%\config\application-prod.yml
    goto :fail
)
if not exist "%NGINX_HOME%\nginx.exe" (
    echo [错误] 未找到 Nginx: %NGINX_HOME%\nginx.exe
    goto :fail
)
if not exist "%~dp0_run-hidden.vbs" (
    echo [错误] 缺少 _run-hidden.vbs
    goto :fail
)
if not exist "%~dp0_run-backend-cmd.bat" (
    echo [错误] 缺少 _run-backend-cmd.bat
    goto :fail
)

rem ---------- [1/2] 后端 ----------
echo [1/2] 启动后端（后台 VBS）...

call :CheckBackendPort
if "!BACKEND_PORT_OK!"=="1" (
    echo       端口 9527 已在监听，后端正常，跳过启动。
    echo [%date% %time%] skip: port 9527 listening>> "%LOG_DIR%\launcher.log"
    goto :start_nginx
)

rem 有 java 进程但端口未监听 = 僵尸进程，先清理
call :KillBackendJarProcesses
if defined BACKEND_JAR_PID (
    echo       发现残留 Java 进程已清理，重新启动...
    echo [%date% %time%] cleaned stale java before start>> "%LOG_DIR%\launcher.log"
    timeout /t 2 /nobreak >nul
)

echo [%date% %time%] wscript launch _run-backend-cmd.bat>> "%LOG_DIR%\launcher.log"
wscript //nologo "%~dp0_run-hidden.vbs" "%~dp0_run-backend-cmd.bat"
echo [%date% %time%] wscript done err=!ERRORLEVEL!>> "%LOG_DIR%\launcher.log"

echo       等待启动（约 20 秒）...
timeout /t 20 /nobreak >nul

call :CheckBackendPort
if "!BACKEND_PORT_OK!"=="1" (
    echo       后端已就绪（9527 端口监听）。
    echo [%date% %time%] backend ok port 9527>> "%LOG_DIR%\launcher.log"
    goto :start_nginx
)

echo [错误] 20 秒后 9527 仍未监听，启动失败。
call :KillBackendJarProcesses
goto :show_logs

:start_nginx
rem ---------- [2/2] Nginx ----------
echo [2/2] 启动 Nginx...
cd /d "%NGINX_HOME%"
nginx.exe -t
if errorlevel 1 (
    echo [错误] Nginx 配置检查失败。
    goto :fail
)
start "" nginx.exe
timeout /t 2 /nobreak >nul

echo [%date% %time%] start.bat done>> "%LOG_DIR%\launcher.log"
echo.
echo [完成] 后端: http://127.0.0.1:9527/  网站: http://127.0.0.1/
echo   日志: %LOG_DIR%\launcher.log  backend-stderr.log
echo   仍失败请先 stop.bat 再 start.bat，或用 start-backend-console.bat 前台排错
echo.
pause
exit /b 0

rem ========== 子程序 ==========
:CheckBackendPort
set "BACKEND_PORT_OK=0"
netstat -ano | findstr ":9527" | findstr /I "LISTENING 监听" >nul 2>&1
if not errorlevel 1 set "BACKEND_PORT_OK=1"
exit /b 0

:KillBackendJarProcesses
set "BACKEND_JAR_PID="
for /f "skip=1 tokens=1" %%p in ('wmic process where "CommandLine like '%%%JAR_NAME%%%'" get ProcessId 2^>nul') do (
    if not "%%p"=="" (
        set "BACKEND_JAR_PID=%%p"
        echo [%date% %time%] taskkill PID=%%p>> "%LOG_DIR%\launcher.log"
        taskkill /F /PID %%p >nul 2>&1
    )
)
exit /b 0

:show_logs
echo.
echo ===== launcher.log =====
type "%LOG_DIR%\launcher.log"
echo.
echo ===== backend-stderr.log =====
if exist "%LOG_DIR%\backend-stderr.log" (type "%LOG_DIR%\backend-stderr.log") else (echo （无 stderr，可能未执行到 java）
echo.
goto :fail

:fail
pause
exit /b 1
