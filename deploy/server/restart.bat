@echo off
chcp 65001 >nul 2>&1
title Gzhennaxia - 重启
echo 先停止再启动...
call "%~dp0stop.bat"
call "%~dp0start.bat"
