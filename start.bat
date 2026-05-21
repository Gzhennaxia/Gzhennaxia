@echo off
chcp 65001 >nul
echo 🚀 启动 Gzhennaxia 个人人生管理系统...

REM 检查Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java 未安装，请先安装 Java 17+
    pause
    exit /b 1
)

REM 检查Node.js
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Node.js 未安装，请先安装 Node.js 18+
    pause
    exit /b 1
)

REM 检查Maven
mvn --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven 未安装，请先安装 Maven
    pause
    exit /b 1
)

echo ✅ 环境检查通过

REM 启动后端
echo 🔧 启动后端服务...
cd backend
if not exist data mkdir data
start "后端服务" cmd /c "mvn spring-boot:run"
cd ..

REM 等待后端启动
echo ⏳ 等待后端服务启动...
timeout /t 10 /nobreak >nul

REM 启动前端
echo 🎨 启动前端服务...
cd frontend
if not exist node_modules (
    echo 📦 安装前端依赖...
    npm install
)
start "前端服务" cmd /c "npm run dev"
cd ..

echo.
echo 🎉 系统启动完成！
echo 📱 前端地址: http://localhost:3000
echo 🔌 后端 API: http://localhost:9527
echo.
echo 按任意键退出...
pause >nul