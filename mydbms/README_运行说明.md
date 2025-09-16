# 简单数据库管理系统 - 运行说明

## 项目概述
这是一个基于Java的简单数据库管理系统，使用XML文件作为数据存储，B+树作为索引结构。

## 系统要求
- Java 11 或更高版本
- Maven 3.6 或更高版本

## 运行方式

### 方式1：使用批处理文件（推荐）
1. 双击 `run.bat` 文件
2. 系统会自动编译并运行数据库管理系统

### 方式2：使用Maven命令
```bash
# 编译项目
mvn clean compile

# 运行项目
mvn exec:java
```

### 方式3：直接运行Java
```bash
# 编译项目
mvn clean compile

# 运行项目
java -cp "target/classes;target/dependency/*" function.Login
```

## 项目升级说明
本项目已从Java 1.6升级到Java 11，主要改进包括：

1. **Java版本升级**：从Java 1.6升级到Java 11
2. **依赖更新**：
   - dom4j: 1.6.1 → 2.1.4
   - jaxen: 1.1.1 → 1.2.0
   - JUnit: 4.12 → 4.13.2 + JUnit 5.9.2
3. **代码现代化**：
   - 使用钻石操作符简化泛型声明
   - 移除不兼容的导入（javax.smartcardio.ATR, org.hamcrest.core.Is）
   - 修复Scanner资源泄漏问题（使用try-with-resources）
   - 更新Maven插件版本
4. **兼容性修复**：
   - 修复所有Java 11兼容性问题
   - 确保项目在现代Java环境下正常运行

## 功能特性
- SQL语句解析
- XML文件作为微型数据库
- B+树索引提升查询速度
- 支持基本的CRUD操作

## 使用说明
1. 启动程序后，系统会提示输入SQL语句
2. 输入 `help` 查看支持的SQL语句
3. 输入 `quit` 退出系统

## 注意事项
- 确保Java和Maven已正确安装
- 项目默认使用"test"数据库
- 数据文件存储在 `mydatabase` 目录下
