@echo off
chcp 65001 >nul
echo 🚀 启动 Vite 版本的个人时间管理系统...

echo 📋 检查环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 需要安装 Java 17+
    pause
    exit /b 1
)

node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 需要安装 Node.js 18+
    pause
    exit /b 1
)

echo ✅ 环境检查通过

echo 🔧 启动后端服务...
cd backend
if not exist data mkdir data

echo 📦 编译后端项目...
call mvn clean compile
if %errorlevel% neq 0 (
    echo ❌ 后端编译失败
    pause
    exit /b 1
)

echo 🚀 启动后端服务...
start "后端服务" cmd /c "mvn spring-boot:run"
cd ..

echo ⏳ 等待后端启动...
timeout /t 15 /nobreak >nul

echo 🎨 检查前端依赖...
cd frontend

echo 📦 清理旧依赖并重新安装...
if exist node_modules rmdir /s /q node_modules
if exist package-lock.json del package-lock.json

npm config set registry https://registry.npmmirror.com
npm install
if %errorlevel% neq 0 (
    echo ❌ 前端依赖安装失败
    pause
    exit /b 1
)

echo 🚀 启动 Vite 开发服务器...
start "Vite 前端服务" cmd /c "npm run dev"
cd ..

echo.
echo 🎉 系统启动完成！
echo 📱 前端地址: http://localhost:3000
echo 🔌 后端 API: http://localhost:9527
echo.
echo 使用 Vite 构建工具，支持热重载和快速构建
echo.
pause