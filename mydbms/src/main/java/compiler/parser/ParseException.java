package compiler.parser;

/**
 * 语法分析异常类
 */
public class ParseException extends Exception {
    private int line;
    private int column;
    private String expectedSymbol;
    
    public ParseException(String message, int line, int column) {
        super(message);
        this.line = line;
        this.column = column;
    }
    
    public ParseException(String message, int line, int column, String expectedSymbol) {
        super(message);
        this.line = line;
        this.column = column;
        this.expectedSymbol = expectedSymbol;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
    
    public String getExpectedSymbol() {
        return expectedSymbol;
    }
    
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Parse error at line ").append(line).append(", column ").append(column);
        sb.append(": ").append(super.getMessage());
        
        if (expectedSymbol != null) {
            sb.append(". Expected: ").append(expectedSymbol);
        }
        
        return sb.toString();
    }
}
