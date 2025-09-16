package compiler;

import compiler.ast.ASTNode;
import compiler.codegen.ExecutionPlan;
import compiler.lexer.Token;
import compiler.semantic.SemanticAnalysisResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 编译结果类
 */
public class CompilationResult {
    private boolean success;
    private List<Token> tokens;
    private ASTNode ast;
    private SemanticAnalysisResult semanticResult;
    private ExecutionPlan executionPlan;
    private List<CompilationError> errors;
    
    public CompilationResult() {
        this.success = false;
        this.errors = new ArrayList<>();
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public List<Token> getTokens() {
        return tokens;
    }
    
    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }
    
    public ASTNode getAst() {
        return ast;
    }
    
    public void setAst(ASTNode ast) {
        this.ast = ast;
    }
    
    public SemanticAnalysisResult getSemanticResult() {
        return semanticResult;
    }
    
    public void setSemanticResult(SemanticAnalysisResult semanticResult) {
        this.semanticResult = semanticResult;
    }
    
    public ExecutionPlan getExecutionPlan() {
        return executionPlan;
    }
    
    public void setExecutionPlan(ExecutionPlan executionPlan) {
        this.executionPlan = executionPlan;
    }
    
    public List<CompilationError> getErrors() {
        return errors;
    }
    
    public void addError(String type, int line, int column, String message) {
        errors.add(new CompilationError(type, line, column, message));
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty() || (semanticResult != null && semanticResult.hasErrors());
    }
    
    @Override
    public String toString() {
        if (success) {
            return "编译成功";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("编译失败，发现以下错误：\n");
            
            for (CompilationError error : errors) {
                sb.append("  ").append(error.toString()).append("\n");
            }
            
            if (semanticResult != null && semanticResult.hasErrors()) {
                sb.append("  ").append(semanticResult.toString());
            }
            
            return sb.toString();
        }
    }
    
    /**
     * 编译错误类
     */
    public static class CompilationError {
        private String type;
        private int line;
        private int column;
        private String message;
        
        public CompilationError(String type, int line, int column, String message) {
            this.type = type;
            this.line = line;
            this.column = column;
            this.message = message;
        }
        
        public String getType() {
            return type;
        }
        
        public int getLine() {
            return line;
        }
        
        public int getColumn() {
            return column;
        }
        
        public String getMessage() {
            return message;
        }
        
        @Override
        public String toString() {
            if (line > 0 && column > 0) {
                return String.format("[%s, Line %d Column %d, %s]", type, line, column, message);
            } else {
                return String.format("[%s, %s]", type, message);
            }
        }
    }
}
