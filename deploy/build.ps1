# 开发机一键构建前后端，并汇总待上传服务器的发布目录
# 用法（仓库根目录）：
#   .\deploy\build.ps1
#   .\deploy\build.ps1 -InstallDeps             # 强制 npm install（依赖变更时）
#   .\deploy\build.ps1 -SkipNpmInstall          # 显式跳过 npm install
#   .\deploy\build.ps1 -BackendOnly             # 仅后端
#   .\deploy\build.ps1 -FrontendOnly            # 仅前端
# 说明：已有 node_modules 时默认跳过 npm install，避免 Windows 下 rollup 文件 EBUSY
# 说明：本文件须为 UTF-8 BOM，否则 Windows PowerShell 5.x 会按 GBK 解析导致中文乱码。

[CmdletBinding()]
param(
    [switch]$BackendOnly,
    [switch]$FrontendOnly,
    [switch]$SkipNpmInstall,
    [switch]$InstallDeps
)

$ErrorActionPreference = "Stop"

function Set-ConsoleUtf8 {
    try {
        chcp 65001 | Out-Null
        [Console]::OutputEncoding = [System.Text.Encoding]::UTF8
        $script:OutputEncoding = [Console]::OutputEncoding
    } catch {
        # 非交互环境忽略
    }
}

function Write-Step([string]$Message) {
    Write-Host ""
    Write-Host "==> $Message" -ForegroundColor Cyan
}

function Assert-LastExitCode([string]$StepName) {
    if ($LASTEXITCODE -ne 0) {
        $hint = ""
        if ($StepName -eq "Maven 构建") {
            $hsErr = Get-ChildItem -Path (Join-Path $BackendDir "hs_err_pid*.log") -ErrorAction SilentlyContinue |
                Sort-Object LastWriteTime -Descending |
                Select-Object -First 1
            if ($hsErr) {
                $hint = "（检测到 JVM 崩溃日志 $($hsErr.Name)，多为内存不足，请关闭其它占内存程序后重试，或增大 deploy\build.ps1 中的 MAVEN_OPTS -Xmx）"
            }
        }
        if ($StepName -eq "npm install") {
            $hint = "（Windows 常见为 rollup.node 被占用 EBUSY：先停止 `npm run dev` / 关闭占用 node_modules 的进程，再执行 `.\deploy\build.ps1 -SkipNpmInstall` 或稍后加 `-InstallDeps` 重试）"
        }
        throw "$StepName 失败，退出码: $LASTEXITCODE$hint"
    }
}

function Invoke-NpmInstallWithRetry {
    param([int]$MaxAttempts = 3)
    for ($attempt = 1; $attempt -le $MaxAttempts; $attempt++) {
        npm install
        if ($LASTEXITCODE -eq 0) {
            return
        }
        if ($attempt -lt $MaxAttempts) {
            Write-Host "npm install 失败，5 秒后重试 ($attempt/$MaxAttempts)..." -ForegroundColor Yellow
            Start-Sleep -Seconds 5
        }
    }
    Assert-LastExitCode "npm install"
}

Set-ConsoleUtf8

if ($BackendOnly -and $FrontendOnly) {
    Write-Error "不能同时指定 -BackendOnly 与 -FrontendOnly"
}

$BuildBackend = -not $FrontendOnly
$BuildFrontend = -not $BackendOnly

$RepoRoot = Split-Path $PSScriptRoot -Parent
$BackendDir = Join-Path $RepoRoot "backend"
$FrontendDir = Join-Path $RepoRoot "frontend"
$ReleaseDir = Join-Path $PSScriptRoot "release"
$JavaHome = "D:\SOFTERWARE\JDK\JDK17"

Write-Host "仓库根目录: $RepoRoot"
Write-Host "发布目录:   $ReleaseDir"

# ---------- 后端 ----------
if ($BuildBackend) {
    Write-Step "构建后端 (Maven, JDK 17)"

    if (-not (Test-Path "$JavaHome\bin\java.exe")) {
        Write-Error "未找到 JDK 17：$JavaHome，请修改 deploy\build.ps1 中的 `$JavaHome"
    }

    $env:JAVA_HOME = $JavaHome
    $env:Path = "$JavaHome\bin;" + $env:Path
    # 限制 Maven 进程内存，避免 C2 编译 / repackage 时 native OOM（见 backend\hs_err_pid*.log）
    $env:MAVEN_OPTS = "-Xmx1024m -Xms256m -XX:MaxMetaspaceSize=384m -XX:ReservedCodeCacheSize=256m -XX:+TieredCompilation -XX:TieredStopAtLevel=1"
    Write-Host "JAVA_HOME=$env:JAVA_HOME"
    Write-Host "MAVEN_OPTS=$env:MAVEN_OPTS"
    & java -version

    Push-Location $BackendDir
    try {
        # -T 1：串行构建多模块，降低并行编译占用
        mvn clean package -DskipTests -pl web -am -T 1
        Assert-LastExitCode "Maven 构建"
    } finally {
        Pop-Location
    }
}

# ---------- 前端 ----------
if ($BuildFrontend) {
    Write-Step "构建前端 (npm run build)"

    if (-not (Get-Command npm -ErrorAction SilentlyContinue)) {
        Write-Error "未找到 npm，请安装 Node.js 18+ 并加入 PATH"
    }

    $nodeModulesDir = Join-Path $FrontendDir "node_modules"
    $runNpmInstall = $false
    if ($InstallDeps) {
        $runNpmInstall = $true
    } elseif ($SkipNpmInstall) {
        $runNpmInstall = $false
    } elseif (-not (Test-Path $nodeModulesDir)) {
        $runNpmInstall = $true
    }

    Push-Location $FrontendDir
    try {
        if ($runNpmInstall) {
            Write-Host "执行 npm install..."
            Invoke-NpmInstallWithRetry
        } else {
            Write-Host "跳过 npm install（node_modules 已存在）。依赖有变更请使用 -InstallDeps；若仅需打包请加 -SkipNpmInstall。" -ForegroundColor Yellow
        }
        npm run build
        Assert-LastExitCode "npm run build"
    } finally {
        Pop-Location
    }

    $FrontendDist = Join-Path $FrontendDir "dist"
    if (-not (Test-Path $FrontendDist)) {
        Write-Error "前端构建失败：未找到 $FrontendDist"
    }
}

# ---------- 汇总发布目录（与服务器 E:\SOFTWARE\gzhennaxia\ 目录结构一致）----------
Write-Step "汇总发布文件到 deploy\release"

$FullRelease = $BuildBackend -and $BuildFrontend
if ($FullRelease) {
    if (Test-Path $ReleaseDir) {
        Remove-Item -Path $ReleaseDir -Recurse -Force
    }
}
New-Item -Path $ReleaseDir -ItemType Directory -Force | Out-Null

$copied = @()

if ($BuildBackend) {
    $Jar = Get-ChildItem -Path (Join-Path $BackendDir "web\target") -Filter "web-*.jar" |
        Where-Object { $_.Name -notmatch "original" } |
        Select-Object -First 1

    if (-not $Jar) {
        Write-Error "未找到 web-*.jar，请检查 Maven 构建日志"
    }

    Copy-Item -Path $Jar.FullName -Destination (Join-Path $ReleaseDir $Jar.Name) -Force
    $copied += $Jar.Name

    $ConfigDir = Join-Path $ReleaseDir "config"
    New-Item -Path $ConfigDir -ItemType Directory -Force | Out-Null
    $DeployProdYml = Join-Path $PSScriptRoot "config\application-prod.yml"
    $DeployProdExample = Join-Path $PSScriptRoot "config\application-prod.yml.example"
    $BackendProdYml = Join-Path $BackendDir "web\src\main\resources\application-prod.yml"
    if (Test-Path $DeployProdYml) {
        Copy-Item -Path $DeployProdYml -Destination (Join-Path $ConfigDir "application-prod.yml") -Force
        $copied += "config\application-prod.yml"
    } elseif (Test-Path $DeployProdExample) {
        Copy-Item -Path $DeployProdExample -Destination (Join-Path $ConfigDir "application-prod.yml") -Force
        $copied += "config\application-prod.yml (来自 .example，请填写密码)"
        Write-Host "警告: 未找到 deploy\config\application-prod.yml，已使用 .example 模板。" -ForegroundColor Yellow
        Write-Host "  请复制: copy deploy\config\application-prod.yml.example deploy\config\application-prod.yml" -ForegroundColor Yellow
    } elseif (Test-Path $BackendProdYml) {
        Copy-Item -Path $BackendProdYml -Destination (Join-Path $ConfigDir "application-prod.yml") -Force
        $copied += "config\application-prod.yml (来自 backend 模板)"
    }
}

if ($BuildFrontend) {
    $ReleaseDist = Join-Path $ReleaseDir "dist"
    if (Test-Path $ReleaseDist) {
        Remove-Item -Path $ReleaseDist -Recurse -Force
    }
    New-Item -Path $ReleaseDist -ItemType Directory -Force | Out-Null
    Copy-Item -Path (Join-Path $FrontendDir "dist\*") -Destination $ReleaseDist -Recurse -Force
    $copied += "dist\"
}

# 部署辅助文件（Nginx 配置 + 服务器 bat 脚本，可双击运行）
$DeployFiles = @(
    @{ Src = "nginx-gzhennaxia.conf"; Dst = "nginx-gzhennaxia.conf" }
)
foreach ($item in $DeployFiles) {
    $srcPath = Join-Path $PSScriptRoot $item.Src
    if (Test-Path $srcPath) {
        Copy-Item -Path $srcPath -Destination (Join-Path $ReleaseDir $item.Dst) -Force
        $copied += $item.Dst
    }
}

$ServerBatDir = Join-Path $PSScriptRoot "server"
if (Test-Path $ServerBatDir) {
    Get-ChildItem -Path $ServerBatDir -File | ForEach-Object {
        Copy-Item -Path $_.FullName -Destination (Join-Path $ReleaseDir $_.Name) -Force
        $copied += $_.Name
    }
}

$readme = @"
# 发布包（由 deploy/build.ps1 生成）

将本目录下全部内容复制到服务器：E:\SOFTWARE\gzhennaxia\

生成时间: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')

上传后确认 config\application-prod.yml 中数据库连接与服务器 PostgreSQL 一致。
完整部署步骤见仓库 docs\deploy-windows-server.md

压缩包：将 deploy\gzhennaxia-release.zip 上传到服务器后解压到 E:\SOFTWARE\gzhennaxia\（勿多套一层 release 目录）。
"@
Set-Content -Path (Join-Path $ReleaseDir "README-upload.txt") -Value $readme -Encoding UTF8

# ---------- 打包为 ZIP（便于上传）----------
Write-Step "打包压缩 deploy\gzhennaxia-release.zip"

$ZipPath = Join-Path $PSScriptRoot "gzhennaxia-release.zip"
if (Test-Path $ZipPath) {
    Remove-Item -Path $ZipPath -Force
}

# 压缩包根目录即为服务器目录内容（jar、dist、config 等），解压后直接可用
$ItemsToZip = Get-ChildItem -Path $ReleaseDir -Force
if ($ItemsToZip.Count -eq 0) {
    Write-Error "发布目录为空，无法打包"
}
Compress-Archive -Path ($ItemsToZip | ForEach-Object { $_.FullName }) -DestinationPath $ZipPath -CompressionLevel Optimal

$ZipFile = Get-Item $ZipPath
$ZipSizeMb = [math]::Round($ZipFile.Length / 1MB, 2)

Write-Host ""
Write-Host "构建完成。发布目录:" -ForegroundColor Green
Write-Host "  $ReleaseDir"
Write-Host ""
Write-Host "已汇总文件:" -ForegroundColor Green
foreach ($name in $copied) {
    Write-Host "  - $name"
}
Write-Host "  - README-upload.txt"
Write-Host ""
Write-Host "压缩包（推荐上传此文件）:" -ForegroundColor Green
Write-Host "  $($ZipFile.FullName)  ($ZipSizeMb MB)"
Write-Host ""
Write-Host "服务器解压示例:" -ForegroundColor Yellow
Write-Host "  Expand-Archive -Path E:\path\to\gzhennaxia-release.zip -DestinationPath E:\SOFTWARE\gzhennaxia\ -Force"
Write-Host ""
Write-Host "也可将 deploy\release\ 目录内容直接复制到服务器 E:\SOFTWARE\gzhennaxia\" -ForegroundColor Yellow
