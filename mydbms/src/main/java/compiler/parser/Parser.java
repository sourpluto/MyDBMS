package compiler.parser;

import compiler.ast.ASTNode;
import compiler.ast.expressions.*;
import compiler.ast.statements.*;
import compiler.lexer.Token;
import compiler.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * 语法分析器 - 将词法标记序列转换为AST
 * 支持CREATE TABLE、INSERT、SELECT、DELETE四类语句
 */
public class Parser {
    private List<Token> tokens;
    private int current = 0;
    
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }
    
    /**
     * 解析SQL语句，返回AST
     */
    public ASTNode parse() throws ParseException {
        if (tokens.isEmpty()) {
            throw new ParseException("Empty input", 1, 1);
        }
        
        return parseStatement();
    }
    
    /**
     * 解析语句
     */
    private Statement parseStatement() throws ParseException {
        Token token = peek();
        
        switch (token.getType()) {
            case CREATE:
                return parseCreateStatement();
            case INSERT:
                return parseInsertStatement();
            case SELECT:
                return parseSelectStatement();
            case DELETE:
                return parseDeleteStatement();
            case USE:
                return parseUseStatement();
            case DROP:
                return parseDropStatement();
            case SHOW:
                return parseShowStatement();
            case QUIT:
                return parseQuitStatement();
            default:
                throw new ParseException("Unexpected token: " + token.getValue(), 
                                       token.getLine(), token.getColumn(), 
                                       "CREATE, INSERT, SELECT, DELETE, USE, DROP, SHOW, or QUIT");
        }
    }
    
    /**
     * 解析CREATE语句 - 根据第二个关键字决定具体类型
     */
    private Statement parseCreateStatement() throws ParseException {
        Token createToken = consume(TokenType.CREATE, "CREATE");
        Token nextToken = peek();
        
        switch (nextToken.getType()) {
            case TABLE:
                return parseCreateTableStatementInternal(createToken);
            case DATABASE:
                return parseCreateDatabaseStatement(createToken);
            case USER:
                return parseCreateUserStatement(createToken);
            default:
                throw new ParseException("Expected TABLE, DATABASE, or USER after CREATE", 
                                       nextToken.getLine(), nextToken.getColumn(), 
                                       "TABLE, DATABASE, or USER");
        }
    }
    
    /**
     * 解析CREATE TABLE语句的内部实现
     */
    private CreateTableStatement parseCreateTableStatementInternal(Token createToken) throws ParseException {
        consume(TokenType.TABLE, "TABLE");
        
        Identifier tableName = parseIdentifier();
        
        consume(TokenType.LEFT_PAREN, "(");
        List<ColumnDefinition> columns = parseColumnDefinitions();
        consume(TokenType.RIGHT_PAREN, ")");
        
        // 可选的分号
        if (match(TokenType.SEMICOLON)) {
            advance();
        }
        
        return new CreateTableStatement(createToken.getLine(), createToken.getColumn(), tableName, columns);
    }
    
    /**
     * 解析列定义列表
     */
    private List<ColumnDefinition> parseColumnDefinitions() throws ParseException {
        List<ColumnDefinition> columns = new ArrayList<>();
        
        do {
            columns.add(parseColumnDefinition());
        } while (match(TokenType.COMMA) && advance() != null);
        
        return columns;
    }
    
    /**
     * 解析单个列定义
     */
    private ColumnDefinition parseColumnDefinition() throws ParseException {
        Identifier columnName = parseIdentifier();
        TokenType dataType = parseDataType();
        
        ColumnDefinition column;
        
        // 检查是否有大小规格（如VARCHAR(50)）
        if (match(TokenType.LEFT_PAREN)) {
            advance();
            Token sizeToken = consume(TokenType.INTEGER_LITERAL, "size");
            consume(TokenType.RIGHT_PAREN, ")");
            
            Integer size = Integer.valueOf(sizeToken.getValue());
            column = new ColumnDefinition(columnName.getLine(), columnName.getColumn(), columnName, dataType, size);
        } else {
            column = new ColumnDefinition(columnName.getLine(), columnName.getColumn(), columnName, dataType);
        }
        
        // 解析约束
        while (match(TokenType.NOT, TokenType.PRIMARY)) {
            if (match(TokenType.NOT)) {
                advance();
                consume(TokenType.NULL, "NULL");
                column.setNotNull(true);
            } else if (match(TokenType.PRIMARY)) {
                advance();
                consume(TokenType.KEY, "KEY");
                column.setPrimaryKey(true);
            }
        }
        
        return column;
    }
    
    /**
     * 解析数据类型
     */
    private TokenType parseDataType() throws ParseException {
        Token token = peek();
        
        switch (token.getType()) {
            case INT:
            case VARCHAR:
            case CHAR:
            case FLOAT:
            case DOUBLE:
            case BOOLEAN:
                advance();
                return token.getType();
            default:
                throw new ParseException("Expected data type", token.getLine(), token.getColumn(), 
                                       "INT, VARCHAR, CHAR, FLOAT, DOUBLE, or BOOLEAN");
        }
    }
    
    /**
     * 解析INSERT语句
     */
    private InsertStatement parseInsertStatement() throws ParseException {
        Token insertToken = consume(TokenType.INSERT, "INSERT");
        consume(TokenType.INTO, "INTO");
        
        Identifier tableName = parseIdentifier();
        
        List<Identifier> columns = null;
        if (match(TokenType.LEFT_PAREN)) {
            advance();
            columns = parseIdentifierList();
            consume(TokenType.RIGHT_PAREN, ")");
        }
        
        consume(TokenType.VALUES, "VALUES");
        ValuesList values = parseValuesList();
        
        // 可选的分号
        if (match(TokenType.SEMICOLON)) {
            advance();
        }
        
        return new InsertStatement(insertToken.getLine(), insertToken.getColumn(), tableName, columns, values);
    }
    
    /**
     * 解析VALUES列表
     */
    private ValuesList parseValuesList() throws ParseException {
        Token leftParen = consume(TokenType.LEFT_PAREN, "(");
        List<Expression> values = new ArrayList<>();
        
        do {
            values.add(parseExpression());
        } while (match(TokenType.COMMA) && advance() != null);
        
        consume(TokenType.RIGHT_PAREN, ")");
        
        return new ValuesList(leftParen.getLine(), leftParen.getColumn(), values);
    }
    
    /**
     * 解析SELECT语句
     */
    private SelectStatement parseSelectStatement() throws ParseException {
        Token selectToken = consume(TokenType.SELECT, "SELECT");
        
        SelectList selectList = parseSelectList();
        
        consume(TokenType.FROM, "FROM");
        Identifier tableName = parseIdentifier();
        
        WhereClause whereClause = null;
        if (match(TokenType.WHERE)) {
            advance();
            whereClause = parseWhereClause();
        }
        
        // 可选的分号
        if (match(TokenType.SEMICOLON)) {
            advance();
        }
        
        return new SelectStatement(selectToken.getLine(), selectToken.getColumn(), selectList, tableName, whereClause);
    }
    
    /**
     * 解析SELECT列表
     */
    private SelectList parseSelectList() throws ParseException {
        Token token = peek();
        
        if (match(TokenType.MULTIPLY)) {
            advance();
            return new SelectList(token.getLine(), token.getColumn(), true);
        }
        
        List<Expression> items = new ArrayList<>();
        do {
            items.add(parseExpression());
        } while (match(TokenType.COMMA) && advance() != null);
        
        return new SelectList(token.getLine(), token.getColumn(), items);
    }
    
    /**
     * 解析DELETE语句
     */
    private DeleteStatement parseDeleteStatement() throws ParseException {
        Token deleteToken = consume(TokenType.DELETE, "DELETE");
        consume(TokenType.FROM, "FROM");
        
        Identifier tableName = parseIdentifier();
        
        WhereClause whereClause = null;
        if (match(TokenType.WHERE)) {
            advance();
            whereClause = parseWhereClause();
        }
        
        // 可选的分号
        if (match(TokenType.SEMICOLON)) {
            advance();
        }
        
        return new DeleteStatement(deleteToken.getLine(), deleteToken.getColumn(), tableName, whereClause);
    }
    
    /**
     * 解析WHERE子句
     */
    private WhereClause parseWhereClause() throws ParseException {
        Token token = peek();
        Expression condition = parseLogicalExpression();
        return new WhereClause(token.getLine(), token.getColumn(), condition);
    }
    
    /**
     * 解析逻辑表达式（支持AND、OR）
     */
    private Expression parseLogicalExpression() throws ParseException {
        Expression left = parseComparisonExpression();
        
        while (match(TokenType.AND, TokenType.OR)) {
            Token operator = advance();
            Expression right = parseComparisonExpression();
            left = new BinaryExpression(left.getLine(), left.getColumn(), left, operator.getType(), right);
        }
        
        return left;
    }
    
    /**
     * 解析比较表达式
     */
    private Expression parseComparisonExpression() throws ParseException {
        Expression left = parseExpression();
        
        if (match(TokenType.EQUALS, TokenType.NOT_EQUALS, TokenType.LESS_THAN, 
                  TokenType.GREATER_THAN, TokenType.LESS_EQUAL, TokenType.GREATER_EQUAL)) {
            Token operator = advance();
            Expression right = parseExpression();
            return new BinaryExpression(left.getLine(), left.getColumn(), left, operator.getType(), right);
        }
        
        return left;
    }
    
    /**
     * 解析表达式
     */
    private Expression parseExpression() throws ParseException {
        Token token = peek();
        
        switch (token.getType()) {
            case IDENTIFIER:
                return parseIdentifier();
            case INTEGER_LITERAL:
            case STRING_LITERAL:
            case FLOAT_LITERAL:
            case BOOLEAN_LITERAL:
                return parseLiteral();
            default:
                throw new ParseException("Expected expression", token.getLine(), token.getColumn(), 
                                       "identifier or literal");
        }
    }
    
    /**
     * 解析标识符
     */
    private Identifier parseIdentifier() throws ParseException {
        Token token = consume(TokenType.IDENTIFIER, "identifier");
        return new Identifier(token.getLine(), token.getColumn(), token.getValue());
    }
    
    /**
     * 解析字面量
     */
    private Literal parseLiteral() throws ParseException {
        Token token = peek();
        advance();
        
        Object value;
        switch (token.getType()) {
            case INTEGER_LITERAL:
                value = Integer.valueOf(token.getValue());
                break;
            case FLOAT_LITERAL:
                value = Double.valueOf(token.getValue());
                break;
            case STRING_LITERAL:
                // 去掉引号
                String str = token.getValue();
                value = str.substring(1, str.length() - 1);
                break;
            case BOOLEAN_LITERAL:
                value = Boolean.valueOf(token.getValue());
                break;
            default:
                throw new ParseException("Invalid literal", token.getLine(), token.getColumn());
        }
        
        return new Literal(token.getLine(), token.getColumn(), value, token.getType());
    }
    
    /**
     * 解析标识符列表
     */
    private List<Identifier> parseIdentifierList() throws ParseException {
        List<Identifier> identifiers = new ArrayList<>();
        
        do {
            identifiers.add(parseIdentifier());
        } while (match(TokenType.COMMA) && advance() != null);
        
        return identifiers;
    }
    
    /**
     * 辅助方法
     */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }
    
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }
    
    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }
    
    private Token peek() {
        return tokens.get(current);
    }
    
    private Token previous() {
        return tokens.get(current - 1);
    }
    
    private Token consume(TokenType type, String message) throws ParseException {
        if (check(type)) {
            return advance();
        }
        
        Token token = peek();
        throw new ParseException("Expected " + message + " but found " + token.getValue(), 
                               token.getLine(), token.getColumn(), message);
    }
    
    /**
     * 解析CREATE DATABASE语句
     */
    private CreateDatabaseStatement parseCreateDatabaseStatement(Token createToken) throws ParseException {
        consume(TokenType.DATABASE, "DATABASE");
        Identifier databaseName = parseIdentifier();
        
        // 可选的分号
        if (match(TokenType.SEMICOLON)) {
            advance();
        }
        
        return new CreateDatabaseStatement(createToken.getLine(), createToken.getColumn(), databaseName.getName());
    }
    
    /**
     * 解析CREATE USER语句
     */
    private CreateUserStatement parseCreateUserStatement(Token createToken) throws ParseException {
        consume(TokenType.USER, "USER");
        
        // 可选的分号
        if (match(TokenType.SEMICOLON)) {
            advance();
        }
        
        return new CreateUserStatement(createToken.getLine(), createToken.getColumn());
    }
    
    /**
     * 解析USE DATABASE语句
     */
    private UseDatabaseStatement parseUseStatement() throws ParseException {
        Token useToken = consume(TokenType.USE, "USE");
        consume(TokenType.DATABASE, "DATABASE");
        Identifier databaseName = parseIdentifier();
        
        // 可选的分号
        if (match(TokenType.SEMICOLON)) {
            advance();
        }
        
        return new UseDatabaseStatement(useToken.getLine(), useToken.getColumn(), databaseName.getName());
    }
    
    /**
     * 解析DROP语句 - 支持DROP DATABASE和DROP TABLE
     */
    private Statement parseDropStatement() throws ParseException {
        Token dropToken = consume(TokenType.DROP, "DROP");
        Token nextToken = peek();
        
        switch (nextToken.getType()) {
            case DATABASE:
                return parseDropDatabaseStatement(dropToken);
            case TABLE:
                // TODO: 实现DROP TABLE，现在先抛出异常
                throw new ParseException("DROP TABLE not implemented yet", 
                                       nextToken.getLine(), nextToken.getColumn(), 
                                       "DATABASE");
            default:
                throw new ParseException("Expected DATABASE or TABLE after DROP", 
                                       nextToken.getLine(), nextToken.getColumn(), 
                                       "DATABASE or TABLE");
        }
    }
    
    /**
     * 解析DROP DATABASE语句
     */
    private DropDatabaseStatement parseDropDatabaseStatement(Token dropToken) throws ParseException {
        consume(TokenType.DATABASE, "DATABASE");
        Identifier databaseName = parseIdentifier();
        
        // 可选的分号
        if (match(TokenType.SEMICOLON)) {
            advance();
        }
        
        return new DropDatabaseStatement(dropToken.getLine(), dropToken.getColumn(), databaseName.getName());
    }
    
    /**
     * 解析SHOW语句 - 支持SHOW DATABASES和SHOW TABLES
     */
    private Statement parseShowStatement() throws ParseException {
        Token showToken = consume(TokenType.SHOW, "SHOW");
        Token nextToken = peek();
        
        switch (nextToken.getType()) {
            case DATABASES:
                return parseShowDatabasesStatement(showToken);
            case TABLES:
                return parseShowTablesStatement(showToken);
            default:
                throw new ParseException("Expected DATABASES or TABLES after SHOW", 
                                       nextToken.getLine(), nextToken.getColumn(), 
                                       "DATABASES or TABLES");
        }
    }
    
    /**
     * 解析SHOW DATABASES语句
     */
    private ShowDatabasesStatement parseShowDatabasesStatement(Token showToken) throws ParseException {
        consume(TokenType.DATABASES, "DATABASES");
        
        // 可选的分号
        if (match(TokenType.SEMICOLON)) {
            advance();
        }
        
        return new ShowDatabasesStatement(showToken.getLine(), showToken.getColumn());
    }
    
    /**
     * 解析SHOW TABLES语句
     */
    private ShowTablesStatement parseShowTablesStatement(Token showToken) throws ParseException {
        consume(TokenType.TABLES, "TABLES");
        
        // 可选的分号
        if (match(TokenType.SEMICOLON)) {
            advance();
        }
        
        return new ShowTablesStatement(showToken.getLine(), showToken.getColumn());
    }
    
    /**
     * 解析QUIT语句
     */
    private Statement parseQuitStatement() throws ParseException {
        Token quitToken = consume(TokenType.QUIT, "QUIT");
        
        // 可选的分号
        if (match(TokenType.SEMICOLON)) {
            advance();
        }
        
        // 对于QUIT，我们可以创建一个特殊的语句或者直接返回null表示退出
        // 这里暂时返回null，实际使用时在上层处理
        return null;
    }
}
