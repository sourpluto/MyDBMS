package compiler;

import compiler.ast.ASTNode;
import compiler.catalog.Catalog;
import compiler.codegen.CodeGenerator;
import compiler.codegen.ExecutionPlan;
import compiler.execution.PlanExecutor;
import compiler.lexer.LexicalAnalyzer;
import compiler.lexer.Token;
import compiler.lexer.TokenType;
import compiler.parser.ParseException;
import compiler.parser.Parser;
import compiler.semantic.SemanticAnalysisResult;
import compiler.semantic.SemanticAnalyzer;

import java.util.List;

/**
 * SQL编译器主类 - 整合词法分析、语法分析、语义分析和代码生成
 */
public class SQLCompiler {
    private Catalog catalog;
    private PlanExecutor executor;
    private boolean showTokens;
    private boolean showAST;
    private boolean showSemanticResult;
    private boolean executeStatements;
    
    public SQLCompiler() {
        this.catalog = new Catalog();
        this.executor = new PlanExecutor();
        this.showTokens = false;
        this.showAST = false;
        this.showSemanticResult = false;
        this.executeStatements = true;
    }
    
    /**
     * 编译SQL语句
     */
    public CompilationResult compile(String sql) {
        CompilationResult result = new CompilationResult();
        
        try {
            // 1. 词法分析
            System.out.println("=== 词法分析 ===");
            LexicalAnalyzer lexer = new LexicalAnalyzer(sql);
            List<Token> tokens = lexer.analyze();
            
            // 检查词法错误
            for (Token token : tokens) {
                if (token.getType() == TokenType.ERROR) {
                    result.addError("词法错误", token.getLine(), token.getColumn(), 
                                  "非法字符: " + token.getValue());
                    return result;
                }
            }
            
            if (showTokens) {
                System.out.println("词法分析结果：");
                for (Token token : tokens) {
                    if (token.getType() != TokenType.EOF) {
                        System.out.println("  " + token.toString());
                    }
                }
                System.out.println();
            }
            
            result.setTokens(tokens);
            
            // 2. 语法分析
            System.out.println("=== 语法分析 ===");
            Parser parser = new Parser(tokens);
            ASTNode ast = parser.parse();
            
            if (showAST) {
                System.out.println("抽象语法树（AST）：");
                System.out.println(ast.toTreeString());
            }
            
            result.setAst(ast);
            
            // 3. 语义分析
            System.out.println("=== 语义分析 ===");
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(catalog);
            SemanticAnalysisResult semanticResult = semanticAnalyzer.analyze(ast);
            
            if (showSemanticResult || semanticResult.hasErrors()) {
                System.out.println(semanticResult.toString());
            }
            
            result.setSemanticResult(semanticResult);
            
            if (semanticResult.hasErrors()) {
                return result;
            }
            
            // 4. 执行计划生成
            System.out.println("=== 执行计划生成 ===");
            CodeGenerator codeGenerator = new CodeGenerator();
            ExecutionPlan executionPlan = codeGenerator.generatePlan(ast);
            
            System.out.println("执行计划（树形结构）：");
            System.out.println(executionPlan.toTreeString());
            
            System.out.println("执行计划（JSON格式）：");
            System.out.println(executionPlan.toJSON());
            
            System.out.println("执行计划（S表达式格式）：");
            System.out.println(executionPlan.toSExpression());
            
            result.setExecutionPlan(executionPlan);
            
            // 5. 执行SQL语句（如果启用）
            if (executeStatements) {
                executor.execute(executionPlan);
            }
            
            result.setSuccess(true);
            
        } catch (ParseException e) {
            result.addError("语法错误", e.getLine(), e.getColumn(), e.getMessage());
        } catch (Exception e) {
            result.addError("编译错误", 0, 0, e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取目录信息
     */
    public Catalog getCatalog() {
        return catalog;
    }
    
    /**
     * 设置调试选项
     */
    public void setShowTokens(boolean showTokens) {
        this.showTokens = showTokens;
    }
    
    public void setShowAST(boolean showAST) {
        this.showAST = showAST;
    }
    
    public void setShowSemanticResult(boolean showSemanticResult) {
        this.showSemanticResult = showSemanticResult;
    }
    
    /**
     * 显示目录信息
     */
    public void showCatalog() {
        System.out.println("=== 数据库目录 ===");
        System.out.println(catalog.toString());
    }
    
    /**
     * 设置是否执行SQL语句
     */
    public void setExecuteStatements(boolean executeStatements) {
        this.executeStatements = executeStatements;
    }
    
    public boolean isExecuteStatements() {
        return executeStatements;
    }
}
