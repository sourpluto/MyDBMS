package compiler.semantic;

/**
 * 语义错误类
 * 格式：[错误类型，位置，原因说明]
 */
public class SemanticError {
    public enum ErrorType {
        TABLE_NOT_EXISTS("表不存在"),
        TABLE_ALREADY_EXISTS("表已存在"),
        COLUMN_NOT_EXISTS("列不存在"),
        COLUMN_ALREADY_EXISTS("列已存在"),
        TYPE_MISMATCH("类型不匹配"),
        COLUMN_COUNT_MISMATCH("列数不匹配"),
        PRIMARY_KEY_VIOLATION("主键冲突"),
        NOT_NULL_VIOLATION("非空约束违反"),
        DUPLICATE_COLUMN("重复列名"),
        INVALID_VALUE("无效值");
        
        private final String description;
        
        ErrorType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    private ErrorType errorType;
    private int line;
    private int column;
    private String reason;
    
    public SemanticError(ErrorType errorType, int line, int column, String reason) {
        this.errorType = errorType;
        this.line = line;
        this.column = column;
        this.reason = reason;
    }
    
    public ErrorType getErrorType() {
        return errorType;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
    
    public String getReason() {
        return reason;
    }
    
    @Override
    public String toString() {
        return String.format("[%s, Line %d Column %d, %s]", 
                           errorType.getDescription(), line, column, reason);
    }
}
