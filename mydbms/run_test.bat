@echo off
echo ========================================
echo 数据库系统完整功能测试
echo ========================================

echo.
echo 编译项目...
call mvn -q -DskipTests compile
if %errorlevel% neq 0 (
    echo 编译失败！
    pause
    exit /b 1
)

echo 编译成功！

echo.
echo ========================================
echo 测试1: XML存储引擎
echo ========================================
echo 启动XML存储引擎测试...
java -cp "target/classes;target/dependency/*" Main < test_commands.txt

echo.
echo ========================================
echo 测试2: 页式存储引擎
echo ========================================
echo 启动页式存储引擎测试...
java -Dengine=paged -cp "target/classes;target/dependency/*" Main < test_commands.txt

echo.
echo ========================================
echo 测试3: 数据持久性验证
echo ========================================
echo 验证数据是否持久化存储...
if exist "mydatabase\testdb\students.dat" (
    echo 页式存储数据文件存在！
    type "mydatabase\testdb\students.dat"
) else (
    echo 页式存储数据文件不存在
)

if exist "mydatabase\testdb\students" (
    echo XML存储目录存在！
    dir "mydatabase\testdb\students"
) else (
    echo XML存储目录不存在
)

echo.
echo 所有测试完成！
pause
