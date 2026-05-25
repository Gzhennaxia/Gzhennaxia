@echo off
chcp 65001 >nul 2>&1
call "%~dp0_config.bat"

set "LOG_DIR=%APP_DIR%\logs"
if not exist "%LOG_DIR%" (
    echo 目录不存在: %LOG_DIR%
    pause
    exit /b 1
)

echo ===== launcher.log =====
if exist "%LOG_DIR%\launcher.log" (type "%LOG_DIR%\launcher.log") else (echo （无）)
echo.
echo ===== backend-stderr.log =====
if exist "%LOG_DIR%\backend-stderr.log" (type "%LOG_DIR%\backend-stderr.log") else (echo （无）)
echo.
echo ===== backend-stdout.log（最后 40 行）=====
if exist "%LOG_DIR%\backend-stdout.log" (
    powershell -NoProfile -Command "Get-Content '%LOG_DIR%\backend-stdout.log' -Tail 40"
) else (
    echo （无）
)
echo.
pause
