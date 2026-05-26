# 导入标普 500 历史 CSV 到 PostgreSQL（fin_market_daily）
# 用法：
#   .\scripts\finance\import-sp500.ps1
#   .\scripts\finance\import-sp500.ps1 -Password "你的密码"
#   .\scripts\finance\import-sp500.ps1 -DryRun

param(
    [string]$Csv = "",
    [string]$DbHost = "127.0.0.1",
    [string]$Port = "5432",
    [string]$Database = "gzhennaxia",
    [string]$User = "postgres",
    [string]$Password = $env:PGPASSWORD,
    [switch]$DryRun
)

$ErrorActionPreference = "Stop"
$root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
Set-Location $root

if (-not $Password) {
    Write-Host "请设置 -Password 或环境变量 PGPASSWORD" -ForegroundColor Yellow
    exit 1
}

$env:PGHOST = $DbHost
$env:PGPORT = $Port
$env:PGDATABASE = $Database
$env:PGUSER = $User
$env:PGPASSWORD = $Password

$pyArgs = @("scripts/finance/import_sp500_csv.py", "--password", $Password)
if ($Csv) { $pyArgs += @("--csv", $Csv) }
if ($DryRun) { $pyArgs += "--dry-run" }

python @pyArgs
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
