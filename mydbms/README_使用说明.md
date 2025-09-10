# SQL编译器实训系统使用说明

## 项目概述

这是一个完整的SQL编译器实训系统，实现了从SQL语句到执行计划的完整编译过程。支持基本的SQL语句解析、语义分析和执行计划生成。

## 已实现功能

### 1. 词法分析器 (LexicalAnalyzer)
- 输出格式：[种别码，词素值，行号，列号]
- 支持SQL关键字、标识符、数字、字符串等token识别
- 提供完整的位置信息用于错误报告

### 2. 语法分析器 (SyntaxAnalyzer)
- 支持以下SQL语句的语法分析：
  - CREATE TABLE（建表语句）
  - INSERT INTO（插入语句）
  - SELECT（查询语句）
  - DELETE FROM（删除语句）
- 生成抽象语法树(AST)

### 3. 语义分析器 (SemanticAnalyzer)
- 表存在性检查
- 列存在性检查
- 数据类型兼容性检查
- 列数匹配检查
- 主键约束检查

### 4. 执行计划生成器 (ExecutionPlanGenerator)
- 将AST转换为逻辑执行计划
- 支持表扫描、投影、选择等操作
- 优化查询执行顺序

### 5. 模式目录 (Catalog)
- 维护数据库表结构信息
- 支持表的增删改查操作
- 列信息管理

## 运行方式

### 方法1：使用批处理脚本（推荐）
1. 进入项目目录：
   ```
   cd simple-dbms-master/mydbms
   ```

2. 运行批处理脚本：
   ```
   run-compiler.bat
   ```

### 方法2：使用Maven命令
1. 编译项目：
   ```
   mvn clean compile
   ```

2. 运行主程序：
   ```
   mvn exec:java -Dexec.mainClass="Main"
   ```

### 方法3：在IDE中运行
直接运行 `src/main/java/Main.java` 文件

## 测试示例

系统启动后，可以输入以下SQL语句进行测试：

### 1. 创建表
```sql
CREATE TABLE students (
    id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    age INT,
    grade FLOAT
);
```

### 2. 插入数据
```sql
INSERT INTO students VALUES (1, 'Alice', 20, 85.5);
```

### 3. 查询数据
```sql
SELECT name, age FROM students WHERE age > 18;
```

### 4. 删除数据
```sql
DELETE FROM students WHERE id = 1;
```

## 输出说明

对于每个SQL语句，系统会输出：

1. **词法分析结果**：每个token的[种别码，词素值，行号，列号]
2. **语法分析结果**：抽象语法树结构
3. **语义分析结果**：类型检查和语义验证信息
4. **执行计划**：逻辑执行步骤

## 错误处理

系统提供完整的错误处理机制：
- 词法错误：非法字符、未闭合字符串等
- 语法错误：语法结构不正确
- 语义错误：表不存在、类型不匹配等

## 注意事项

1. SQL语句需要以分号(;)结尾
2. 输入'exit'退出程序
3. 系统区分大小写
4. 支持基本的SQL数据类型：INT, FLOAT, DOUBLE, VARCHAR, CHAR, BOOLEAN

## 技术架构

```
用户输入SQL
    ↓
词法分析器 (生成Token序列)
    ↓
语法分析器 (生成AST)
    ↓
语义分析器 (类型检查、存在性验证)
    ↓
执行计划生成器 (生成执行计划)
    ↓
输出结果
```

这个系统完整实现了SQL编译器的核心功能，适合用于数据库编译原理的教学和实训。
