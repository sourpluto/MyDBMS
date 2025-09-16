package compiler.lexer;

/**
 * 词法单元类 - 表示一个词法标记
 * 格式：[种别码，词素值，行号，列号]
 */
public class Token {
    private TokenType type;      // 种别码
    private String value;        // 词素值
    private int line;           // 行号
    private int column;         // 列号
    
    public Token(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }
    
    public TokenType getType() {
        return type;
    }
    
    public String getValue() {
        return value;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
    
    @Override
    public String toString() {
        return String.format("[%s, %s, %d, %d]", type, value, line, column);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Token token = (Token) obj;
        return type == token.type && value.equals(token.value);
    }
    
    @Override
    public int hashCode() {
        return type.hashCode() * 31 + value.hashCode();
    }
}
