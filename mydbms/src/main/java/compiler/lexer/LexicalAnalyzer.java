package compiler.lexer;

import java.util.*;

/**
 * 词法分析器 - 将SQL源程序转换为词法标记序列
 * 输出格式：[种别码，词素值，行号，列号]
 */
public class LexicalAnalyzer {
    private String input;
    private int position;
    private int line;
    private int column;
    private int lineStartPosition;
    
    // SQL关键字映射表
    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
    
    static {
        // 初始化关键字映射
        KEYWORDS.put("CREATE", TokenType.CREATE);
        KEYWORDS.put("TABLE", TokenType.TABLE);
        KEYWORDS.put("DATABASE", TokenType.DATABASE);
        KEYWORDS.put("USER", TokenType.USER);
        KEYWORDS.put("USE", TokenType.USE);
        KEYWORDS.put("DROP", TokenType.DROP);
        KEYWORDS.put("SHOW", TokenType.SHOW);
        KEYWORDS.put("DATABASES", TokenType.DATABASES);
        KEYWORDS.put("TABLES", TokenType.TABLES);
        KEYWORDS.put("INSERT", TokenType.INSERT);
        KEYWORDS.put("INTO", TokenType.INTO);
        KEYWORDS.put("VALUES", TokenType.VALUES);
        KEYWORDS.put("SELECT", TokenType.SELECT);
        KEYWORDS.put("FROM", TokenType.FROM);
        KEYWORDS.put("WHERE", TokenType.WHERE);
        KEYWORDS.put("DELETE", TokenType.DELETE);
        KEYWORDS.put("UPDATE", TokenType.UPDATE);
        KEYWORDS.put("SET", TokenType.SET);
        KEYWORDS.put("INDEX", TokenType.INDEX);
        KEYWORDS.put("ON", TokenType.ON);
        KEYWORDS.put("AND", TokenType.AND);
        KEYWORDS.put("OR", TokenType.OR);
        KEYWORDS.put("NOT", TokenType.NOT);
        KEYWORDS.put("NULL", TokenType.NULL);
        KEYWORDS.put("QUIT", TokenType.QUIT);
        
        // 数据类型关键字
        KEYWORDS.put("INT", TokenType.INT);
        KEYWORDS.put("VARCHAR", TokenType.VARCHAR);
        KEYWORDS.put("CHAR", TokenType.CHAR);
        KEYWORDS.put("FLOAT", TokenType.FLOAT);
        KEYWORDS.put("DOUBLE", TokenType.DOUBLE);
        KEYWORDS.put("BOOLEAN", TokenType.BOOLEAN);
        
        // 约束关键字
        KEYWORDS.put("PRIMARY", TokenType.PRIMARY);
        KEYWORDS.put("KEY", TokenType.KEY);
        KEYWORDS.put("FOREIGN", TokenType.FOREIGN);
        KEYWORDS.put("UNIQUE", TokenType.UNIQUE);
        
        // 布尔常量
        KEYWORDS.put("TRUE", TokenType.BOOLEAN_LITERAL);
        KEYWORDS.put("FALSE", TokenType.BOOLEAN_LITERAL);
    }
    
    public LexicalAnalyzer(String input) {
        this.input = input;
        this.position = 0;
        this.line = 1;
        this.column = 1;
        this.lineStartPosition = 0;
    }
    
    /**
     * 进行词法分析，返回所有词法标记
     */
    public List<Token> analyze() {
        List<Token> tokens = new ArrayList<>();
        
        while (!isAtEnd()) {
            Token token = nextToken();
            if (token != null && token.getType() != TokenType.WHITESPACE) {
                tokens.add(token);
            }
        }
        
        // 添加EOF标记
        tokens.add(new Token(TokenType.EOF, "", line, column));
        
        return tokens;
    }
    
    /**
     * 获取下一个词法标记
     */
    private Token nextToken() {
        if (isAtEnd()) {
            return new Token(TokenType.EOF, "", line, column);
        }
        
        char c = advance();
        int tokenLine = line;
        int tokenColumn = column - 1;
        
        // 处理空白字符
        if (isWhitespace(c)) {
            while (!isAtEnd() && isWhitespace(peek())) {
                if (peek() == '\n') {
                    line++;
                    lineStartPosition = position + 1;
                    column = 1;
                } else {
                    column++;
                }
                advance();
            }
            return new Token(TokenType.WHITESPACE, " ", tokenLine, tokenColumn);
        }
        
        // 处理换行
        if (c == '\n') {
            line++;
            lineStartPosition = position;
            column = 1;
            return new Token(TokenType.NEWLINE, "\\n", tokenLine, tokenColumn);
        }
        
        // 处理字符串字面量
        if (c == '\'' || c == '"') {
            return scanStringLiteral(c, tokenLine, tokenColumn);
        }
        
        // 处理数字字面量
        if (isDigit(c)) {
            return scanNumberLiteral(tokenLine, tokenColumn);
        }
        
        // 处理标识符和关键字
        if (isAlpha(c)) {
            return scanIdentifier(tokenLine, tokenColumn);
        }
        
        // 处理运算符和分隔符
        switch (c) {
            case '(':
                return new Token(TokenType.LEFT_PAREN, "(", tokenLine, tokenColumn);
            case ')':
                return new Token(TokenType.RIGHT_PAREN, ")", tokenLine, tokenColumn);
            case ',':
                return new Token(TokenType.COMMA, ",", tokenLine, tokenColumn);
            case ';':
                return new Token(TokenType.SEMICOLON, ";", tokenLine, tokenColumn);
            case '.':
                return new Token(TokenType.DOT, ".", tokenLine, tokenColumn);
            case '+':
                return new Token(TokenType.PLUS, "+", tokenLine, tokenColumn);
            case '-':
                return new Token(TokenType.MINUS, "-", tokenLine, tokenColumn);
            case '*':
                return new Token(TokenType.MULTIPLY, "*", tokenLine, tokenColumn);
            case '/':
                return new Token(TokenType.DIVIDE, "/", tokenLine, tokenColumn);
            case '=':
                return new Token(TokenType.EQUALS, "=", tokenLine, tokenColumn);
            case '!':
                if (match('=')) {
                    return new Token(TokenType.NOT_EQUALS, "!=", tokenLine, tokenColumn);
                }
                break;
            case '<':
                if (match('=')) {
                    return new Token(TokenType.LESS_EQUAL, "<=", tokenLine, tokenColumn);
                }
                return new Token(TokenType.LESS_THAN, "<", tokenLine, tokenColumn);
            case '>':
                if (match('=')) {
                    return new Token(TokenType.GREATER_EQUAL, ">=", tokenLine, tokenColumn);
                }
                return new Token(TokenType.GREATER_THAN, ">", tokenLine, tokenColumn);
        }
        
        // 非法字符
        return new Token(TokenType.ERROR, String.valueOf(c), tokenLine, tokenColumn);
    }
    
    /**
     * 扫描字符串字面量
     */
    private Token scanStringLiteral(char quote, int tokenLine, int tokenColumn) {
        StringBuilder value = new StringBuilder();
        value.append(quote);
        
        while (!isAtEnd() && peek() != quote) {
            if (peek() == '\n') {
                line++;
                lineStartPosition = position + 1;
                column = 1;
            } else {
                column++;
            }
            value.append(advance());
        }
        
        if (isAtEnd()) {
            // 未闭合的字符串
            return new Token(TokenType.ERROR, "Unterminated string", tokenLine, tokenColumn);
        }
        
        // 消费闭合引号
        value.append(advance());
        
        return new Token(TokenType.STRING_LITERAL, value.toString(), tokenLine, tokenColumn);
    }
    
    /**
     * 扫描数字字面量
     */
    private Token scanNumberLiteral(int tokenLine, int tokenColumn) {
        StringBuilder value = new StringBuilder();
        value.append(input.charAt(position - 1));
        
        // 扫描整数部分
        while (!isAtEnd() && isDigit(peek())) {
            value.append(advance());
        }
        
        // 检查是否有小数点
        if (!isAtEnd() && peek() == '.' && isDigit(peekNext())) {
            // 消费小数点
            value.append(advance());
            
            // 扫描小数部分
            while (!isAtEnd() && isDigit(peek())) {
                value.append(advance());
            }
            
            return new Token(TokenType.FLOAT_LITERAL, value.toString(), tokenLine, tokenColumn);
        }
        
        return new Token(TokenType.INTEGER_LITERAL, value.toString(), tokenLine, tokenColumn);
    }
    
    /**
     * 扫描标识符或关键字
     */
    private Token scanIdentifier(int tokenLine, int tokenColumn) {
        StringBuilder value = new StringBuilder();
        value.append(input.charAt(position - 1));
        
        while (!isAtEnd() && (isAlphaNumeric(peek()) || peek() == '_')) {
            value.append(advance());
        }
        
        String text = value.toString().toUpperCase();
        TokenType type = KEYWORDS.getOrDefault(text, TokenType.IDENTIFIER);
        
        return new Token(type, value.toString(), tokenLine, tokenColumn);
    }
    
    /**
     * 辅助方法
     */
    private boolean isAtEnd() {
        return position >= input.length();
    }
    
    private char advance() {
        column++;
        return input.charAt(position++);
    }
    
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (input.charAt(position) != expected) return false;
        
        position++;
        column++;
        return true;
    }
    
    private char peek() {
        if (isAtEnd()) return '\0';
        return input.charAt(position);
    }
    
    private char peekNext() {
        if (position + 1 >= input.length()) return '\0';
        return input.charAt(position + 1);
    }
    
    private boolean isWhitespace(char c) {
        return c == ' ' || c == '\r' || c == '\t';
    }
    
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
    
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }
    
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
    
    /**
     * 获取错误位置信息
     */
    public String getErrorPosition(int line, int column) {
        return String.format("Line %d, Column %d", line, column);
    }
}
