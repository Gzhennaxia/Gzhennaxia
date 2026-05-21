#!/bin/bash

echo "🚀 启动 Gzhennaxia 个人人生管理系统..."

# 检查是否安装了必要的工具
check_requirements() {
    echo "📋 检查环境要求..."
    
    if ! command -v java &> /dev/null; then
        echo "❌ Java 未安装，请先安装 Java 17+"
        exit 1
    fi
    
    if ! command -v node &> /dev/null; then
        echo "❌ Node.js 未安装，请先安装 Node.js 18+"
        exit 1
    fi
    
    if ! command -v mvn &> /dev/null; then
        echo "❌ Maven 未安装，请先安装 Maven"
        exit 1
    fi
    
    echo "✅ 环境检查通过"
}

# 启动后端
start_backend() {
    echo "🔧 启动后端服务..."
    cd backend
    
    # 创建数据目录
    mkdir -p data
    
    # 启动 Spring Boot 应用
    mvn spring-boot:run &
    BACKEND_PID=$!
    
    echo "✅ 后端服务已启动 (PID: $BACKEND_PID)"
    cd ..
}

# 启动前端
start_frontend() {
    echo "🎨 启动前端服务..."
    cd frontend
    
    # 安装依赖（如果需要）
    if [ ! -d "node_modules" ]; then
        echo "📦 安装前端依赖..."
        npm install
    fi
    
    # 启动开发服务器
    npm run dev &
    FRONTEND_PID=$!
    
    echo "✅ 前端服务已启动 (PID: $FRONTEND_PID)"
    cd ..
}

# 主函数
main() {
    check_requirements
    start_backend
    
    # 等待后端启动
    echo "⏳ 等待后端服务启动..."
    sleep 10
    
    start_frontend
    
    echo ""
    echo "🎉 系统启动完成！"
    echo "📱 前端地址: http://localhost:3000"
    echo "🔌 后端 API: http://localhost:9527"
    echo ""
    echo "按 Ctrl+C 停止所有服务"
    
    # 等待用户中断
    trap 'echo "🛑 正在停止服务..."; kill $BACKEND_PID $FRONTEND_PID 2>/dev/null; exit 0' INT
    wait
}

main