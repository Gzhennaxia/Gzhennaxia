@echo off
rem 由 _run-hidden.vbs 无窗口调用，勿双击
call "%~dp0_config.bat"

set "LOG_DIR=%APP_DIR%\logs"
if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

echo [%date% %time%] _run-backend-cmd.bat begin>> "%LOG_DIR%\launcher.log"

cd /d "%APP_DIR%"
if errorlevel 1 (
    echo [%date% %time%] ERROR cd APP_DIR failed>> "%LOG_DIR%\launcher.log"
    exit /b 1
)

set "PATH=%JAVA_HOME%\bin;%PATH%"
set "CONFIG_DIR=%APP_DIR:\=/%/config/"

echo [%date% %time%] java.exe starting>> "%LOG_DIR%\launcher.log"
echo [%date% %time%] jar=%JAR_NAME%>> "%LOG_DIR%\launcher.log"

"%JAVA_HOME%\bin\java.exe" ^
  -Dspring.profiles.active=prod ^
  "-Dspring.config.additional-location=file:%CONFIG_DIR%" ^
  -jar "%APP_DIR%\%JAR_NAME%" ^
  >> "%LOG_DIR%\backend-stdout.log" 2>> "%LOG_DIR%\backend-stderr.log"

set "EXIT_CODE=%ERRORLEVEL%"
echo [%date% %time%] java.exe exited code=%EXIT_CODE%>> "%LOG_DIR%\launcher.log"
exit /b %EXIT_CODE%
