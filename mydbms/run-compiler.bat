@echo off
echo =======================================
echo     SQL编译器实训系统
echo =======================================
echo 正在编译项目...

cd /d "%~dp0"
mvn clean compile -q

if %ERRORLEVEL% EQU 0 (
    echo 编译完成，启动SQL编译器...
    echo.
    mvn exec:java -Dexec.mainClass="Main" -q
) else (
    echo 编译失败，请检查代码错误！
    pause
)
