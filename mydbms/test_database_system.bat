@echo off
echo ========================================
echo 数据库系统功能测试
echo ========================================

echo.
echo 1. 测试XML存储引擎（默认）
echo ========================================
java -cp "target/classes;target/dependency/*" Main

echo.
echo 2. 测试页式存储引擎
echo ========================================
java -Dengine=paged -cp "target/classes;target/dependency/*" Main

echo.
echo 测试完成！
pause
