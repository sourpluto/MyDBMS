package compiler.lexer;

/**
 * 词法标记类型枚举 - 定义所有SQL词法单元的种别码
 */
public enum TokenType {
    // 关键字
    CREATE("CREATE"),
    TABLE("TABLE"),
    DATABASE("DATABASE"),
    USER("USER"),
    USE("USE"),
    DROP("DROP"),
    SHOW("SHOW"),
    DATABASES("DATABASES"),
    TABLES("TABLES"),
    INSERT("INSERT"),
    INTO("INTO"),
    VALUES("VALUES"),
    SELECT("SELECT"),
    FROM("FROM"),
    WHERE("WHERE"),
    DELETE("DELETE"),
    UPDATE("UPDATE"),
    SET("SET"),
    INDEX("INDEX"),
    ON("ON"),
    AND("AND"),
    OR("OR"),
    NOT("NOT"),
    NULL("NULL"),
    QUIT("QUIT"),
    
    // 数据类型关键字
    INT("INT"),
    VARCHAR("VARCHAR"),
    CHAR("CHAR"),
    FLOAT("FLOAT"),
    DOUBLE("DOUBLE"),
    BOOLEAN("BOOLEAN"),
    
    // 约束关键字
    PRIMARY("PRIMARY"),
    KEY("KEY"),
    FOREIGN("FOREIGN"),
    UNIQUE("UNIQUE"),
    
    // 标识符
    IDENTIFIER("IDENTIFIER"),
    
    // 常量
    INTEGER_LITERAL("INTEGER_LITERAL"),
    STRING_LITERAL("STRING_LITERAL"),
    FLOAT_LITERAL("FLOAT_LITERAL"),
    BOOLEAN_LITERAL("BOOLEAN_LITERAL"),
    
    // 运算符
    EQUALS("="),
    NOT_EQUALS("!="),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    LESS_EQUAL("<="),
    GREATER_EQUAL(">="),
    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    
    // 分隔符
    LEFT_PAREN("("),
    RIGHT_PAREN(")"),
    COMMA(","),
    SEMICOLON(";"),
    DOT("."),
    
    // 特殊标记
    EOF("EOF"),
    NEWLINE("NEWLINE"),
    WHITESPACE("WHITESPACE"),
    
    // 错误标记
    ERROR("ERROR");
    
    private final String symbol;
    
    TokenType(String symbol) {
        this.symbol = symbol;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    @Override
    public String toString() {
        return symbol;
    }
}
