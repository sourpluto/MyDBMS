import compiler.CompilationResult;
import compiler.SQLCompiler;

import java.util.Scanner;

/**
 * SQL编译器主程序入口
 */
public class Main {
    private static SQLCompiler compiler = new SQLCompiler();
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("       SQL编译器实训系统");
        System.out.println("===========================================");
        System.out.println("支持的SQL语句类型：");
        System.out.println("  - CREATE DATABASE / CREATE TABLE / CREATE USER");
        System.out.println("  - USE DATABASE");
        System.out.println("  - DROP DATABASE");
        System.out.println("  - SHOW DATABASES / SHOW TABLES");
        System.out.println("  - INSERT INTO");
        System.out.println("  - SELECT");
        System.out.println("  - DELETE");
        System.out.println();
        System.out.println("输入 'help' 查看帮助，输入 'quit' 退出程序");
        System.out.println("===========================================");
        
        while (true) {
            System.out.print("\nSQL> ");
            
            // 检查是否有可用输入
            if (!scanner.hasNextLine()) {
                System.out.println("\n没有更多输入，程序退出。");
                break;
            }
            
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                continue;
            }
            
            if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                System.out.println("谢谢使用！");
                break;
            }
            
            if (input.equalsIgnoreCase("help")) {
                showHelp();
                continue;
            }
            
            if (input.equalsIgnoreCase("catalog")) {
                compiler.showCatalog();
                continue;
            }
            
            if (input.startsWith("set ")) {
                handleSetCommand(input);
                continue;
            }
            
            if (input.equalsIgnoreCase("test")) {
                runTests();
                continue;
            }
            
            // 编译SQL语句
            try {
                CompilationResult result = compiler.compile(input);
                
                if (result.isSuccess()) {
                    System.out.println("\n✓ 编译成功！");
                } else {
                    System.out.println("\n✗ 编译失败！");
                    System.out.println(result.toString());
                }
                
            } catch (Exception e) {
                System.out.println("✗ 系统错误：" + e.getMessage());
                e.printStackTrace();
            }
        }
        
        scanner.close();
    }
    
    private static void showHelp() {
        System.out.println("\n=== 帮助信息 ===");
        System.out.println("命令：");
        System.out.println("  help        - 显示帮助信息");
        System.out.println("  catalog     - 显示数据库目录信息");
        System.out.println("  test        - 运行测试用例");
        System.out.println("  quit/exit   - 退出程序");
        System.out.println();
        System.out.println("调试选项：");
        System.out.println("  set tokens on/off    - 显示/隐藏词法分析结果");
        System.out.println("  set ast on/off       - 显示/隐藏语法分析结果");
        System.out.println("  set semantic on/off  - 显示/隐藏语义分析结果");
        System.out.println("  set execute on/off   - 启用/禁用SQL语句执行");
        System.out.println();
        System.out.println("SQL语句示例：");
        System.out.println("  CREATE DATABASE testdb;");
        System.out.println("  USE DATABASE testdb;");
        System.out.println("  CREATE USER;");
        System.out.println("  SHOW DATABASES;");
        System.out.println("  SHOW TABLES;");
        System.out.println("  CREATE TABLE students (id INT PRIMARY KEY, name VARCHAR(50) NOT NULL, age INT);");
        System.out.println("  INSERT INTO students (id, name, age) VALUES (1, 'Alice', 20);");
        System.out.println("  SELECT * FROM students;");
        System.out.println("  SELECT name, age FROM students WHERE age > 18;");
        System.out.println("  DELETE FROM students WHERE id = 1;");
        System.out.println("  DROP DATABASE testdb;");
    }
    
    private static void handleSetCommand(String input) {
        String[] parts = input.split("\\s+");
        
        if (parts.length != 3) {
            System.out.println("用法: set <option> <on|off>");
            return;
        }
        
        String option = parts[1].toLowerCase();
        String value = parts[2].toLowerCase();
        boolean enable = value.equals("on");
        
        switch (option) {
            case "tokens":
                compiler.setShowTokens(enable);
                System.out.println("词法分析结果显示：" + (enable ? "开启" : "关闭"));
                break;
            case "ast":
                compiler.setShowAST(enable);
                System.out.println("语法分析结果显示：" + (enable ? "开启" : "关闭"));
                break;
            case "semantic":
                compiler.setShowSemanticResult(enable);
                System.out.println("语义分析结果显示：" + (enable ? "开启" : "关闭"));
                break;
            case "execute":
                compiler.setExecuteStatements(enable);
                System.out.println("SQL语句执行：" + (enable ? "开启" : "关闭"));
                break;
            default:
                System.out.println("未知选项：" + option);
                System.out.println("可用选项：tokens, ast, semantic, execute");
        }
    }
    
    private static void runTests() {
        System.out.println("\n=== 运行测试用例 ===");
        
        String[] testCases = {
            // 数据库级操作
            "CREATE DATABASE testdb;",
            "SHOW DATABASES;",
            "USE DATABASE testdb;",
            "CREATE USER;",
            "SHOW TABLES;",
            
            // 正确的CREATE TABLE语句
            "CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(100) NOT NULL, email VARCHAR(255));",
            
            // 正确的INSERT语句
            "INSERT INTO users (id, name, email) VALUES (1, 'John Doe', 'john@example.com');",
            
            // 正确的SELECT语句
            "SELECT * FROM users;",
            "SELECT name, email FROM users WHERE id = 1;",
            
            // 正确的DELETE语句
            "DELETE FROM users WHERE id = 1;",
            
            // 清理
            "DROP DATABASE testdb;",
            
            // 错误的语句（用于测试错误处理）
            "CREATE TABLE;",  // 语法错误
            "INSERT INTO nonexistent VALUES (1);",  // 表不存在
            "SELECT invalid_column FROM users;",  // 列不存在
        };
        
        for (int i = 0; i < testCases.length; i++) {
            System.out.println("\n--- 测试用例 " + (i + 1) + " ---");
            System.out.println("SQL: " + testCases[i]);
            
            CompilationResult result = compiler.compile(testCases[i]);
            
            if (result.isSuccess()) {
                System.out.println("结果: ✓ 编译成功");
            } else {
                System.out.println("结果: ✗ 编译失败");
                // 不显示详细错误信息，保持输出简洁
            }
        }
        
        System.out.println("\n=== 测试完成 ===");
    }
}
