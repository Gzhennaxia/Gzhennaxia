Gzhennaxia 服务器一键启停
========================

  start.bat    一键启动（以 9527 端口是否监听为准）
  stop.bat     一键停止
  restart.bat  先停后启（推荐排错时用）

排错：
  start-backend-console.bat   前台启动（看控制台报错）
  view-backend-log.bat          查看 logs 下日志

首次编辑 _config.bat 中的路径。

后台启动日志（启动失败必看）：
  logs\launcher.log         每一步记录（应有 wscript、java starting 等行）
  logs\backend-stderr.log   Java 报错
  logs\backend-stdout.log   标准输出

内部文件勿删：_run-hidden.vbs、_run-backend-cmd.bat
