package compiler.semantic;

import java.util.List;

/**
 * 语义分析结果类
 */
public class SemanticAnalysisResult {
    private boolean success;
    private List<SemanticError> errors;
    
    public SemanticAnalysisResult(boolean success, List<SemanticError> errors) {
        this.success = success;
        this.errors = errors;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public List<SemanticError> getErrors() {
        return errors;
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    @Override
    public String toString() {
        if (success) {
            return "语义分析成功";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("语义分析失败，发现以下错误：\n");
            for (SemanticError error : errors) {
                sb.append("  ").append(error.toString()).append("\n");
            }
            return sb.toString();
        }
    }
}
