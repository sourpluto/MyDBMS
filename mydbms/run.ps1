# PowerShell脚本启动SQL编译器
Write-Host "正在启动SQL编译器..." -ForegroundColor Green

# 检查是否编译过
if (!(Test-Path "target\classes")) {
    Write-Host "正在编译项目..." -ForegroundColor Yellow
    mvn clean compile dependency:copy-dependencies
}

# 启动程序
java -cp "target\classes;target\dependency\*" Main
