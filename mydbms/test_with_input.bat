@echo off
echo 正在使用预定义命令测试SQL编译器...
type test_commands.txt | java -cp "target\classes;target\dependency\*" Main
echo.
echo 测试完成！
pause
