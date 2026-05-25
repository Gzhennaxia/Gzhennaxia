# 仅构建后端（兼容旧用法，推荐改用 .\deploy\build.ps1）
# 用法：在仓库根目录执行  .\deploy\build-backend.ps1

& (Join-Path $PSScriptRoot "build.ps1") -BackendOnly @args
