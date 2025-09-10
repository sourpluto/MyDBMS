package compiler.semantic;

import compiler.ast.ASTNode;
import compiler.ast.ASTVisitor;
import compiler.ast.expressions.*;
import compiler.ast.statements.*;
import compiler.catalog.Catalog;
import compiler.catalog.CatalogException;
import compiler.catalog.ColumnInfo;
import compiler.catalog.TableSchema;
import compiler.lexer.TokenType;

import java.util.*;

/**
 * 语义分析器 - 对AST进行语义检查
 * 包括存在性检查、类型一致性检查、列数/列序检查
 */
public class SemanticAnalyzer implements ASTVisitor<Void> {
    private Catalog catalog;
    private List<SemanticError> errors;
    
    public SemanticAnalyzer(Catalog catalog) {
        this.catalog = catalog;
        this.errors = new ArrayList<>();
    }
    
    /**
     * 分析AST节点，返回语义分析结果
     */
    public SemanticAnalysisResult analyze(ASTNode node) {
        errors.clear();
        node.accept(this);
        
        return new SemanticAnalysisResult(errors.isEmpty(), new ArrayList<>(errors));
    }
    
    @Override
    public Void visitCreateTableStatement(CreateTableStatement stmt) {
        String tableName = stmt.getTableName().getName();
        
        // 检查表是否已存在
        if (catalog.tableExists(tableName)) {
            addError(SemanticError.ErrorType.TABLE_ALREADY_EXISTS,
                    stmt.getLine(), stmt.getColumn(),
                    "Table '" + tableName + "' already exists");
            return null;
        }
        
        // 检查列定义
        Set<String> columnNames = new HashSet<>();
        int primaryKeyCount = 0;
        
        for (ColumnDefinition colDef : stmt.getColumns()) {
            String columnName = colDef.getColumnName().getName();
            
            // 检查重复列名
            if (columnNames.contains(columnName.toUpperCase())) {
                addError(SemanticError.ErrorType.DUPLICATE_COLUMN,
                        colDef.getLine(), colDef.getColumn(),
                        "Duplicate column name '" + columnName + "'");
            } else {
                columnNames.add(columnName.toUpperCase());
            }
            
            // 检查主键数量
            if (colDef.isPrimaryKey()) {
                primaryKeyCount++;
                if (primaryKeyCount > 1) {
                    addError(SemanticError.ErrorType.PRIMARY_KEY_VIOLATION,
                            colDef.getLine(), colDef.getColumn(),
                            "Multiple primary keys defined");
                }
            }
            
            // 检查数据类型有效性
            if (!isValidDataType(colDef.getDataType())) {
                addError(SemanticError.ErrorType.TYPE_MISMATCH,
                        colDef.getLine(), colDef.getColumn(),
                        "Invalid data type: " + colDef.getDataType());
            }
            
            // 检查VARCHAR/CHAR的长度
            if ((colDef.getDataType() == TokenType.VARCHAR || colDef.getDataType() == TokenType.CHAR) 
                && colDef.getSize() != null && colDef.getSize() <= 0) {
                addError(SemanticError.ErrorType.INVALID_VALUE,
                        colDef.getLine(), colDef.getColumn(),
                        "Invalid size for " + colDef.getDataType() + ": " + colDef.getSize());
            }
        }
        
        // 如果没有错误，更新目录
        if (errors.isEmpty()) {
            try {
                catalog.createTable(stmt);
            } catch (CatalogException e) {
                addError(SemanticError.ErrorType.TABLE_ALREADY_EXISTS,
                        stmt.getLine(), stmt.getColumn(), e.getMessage());
            }
        }
        
        return null;
    }
    
    @Override
    public Void visitInsertStatement(InsertStatement stmt) {
        String tableName = stmt.getTableName().getName();
        
        // 检查表是否存在
        if (!catalog.tableExists(tableName)) {
            addError(SemanticError.ErrorType.TABLE_NOT_EXISTS,
                    stmt.getTableName().getLine(), stmt.getTableName().getColumn(),
                    "Table '" + tableName + "' does not exist");
            return null;
        }
        
        TableSchema schema = catalog.getTableSchema(tableName);
        List<Identifier> columns = stmt.getColumns();
        ValuesList values = stmt.getValues();
        
        // 如果没有指定列名，默认为所有列
        if (columns.isEmpty()) {
            // 检查值的数量是否与表的列数匹配
            if (values.getValues().size() != schema.getColumnCount()) {
                addError(SemanticError.ErrorType.COLUMN_COUNT_MISMATCH,
                        values.getLine(), values.getColumn(),
                        "Expected " + schema.getColumnCount() + " values, but got " + values.getValues().size());
                return null;
            }
            
            // 检查每个值的类型
            for (int i = 0; i < values.getValues().size(); i++) {
                Expression valueExpr = values.getValues().get(i);
                ColumnInfo column = schema.getColumns().get(i);
                
                if (valueExpr instanceof Literal) {
                    Literal literal = (Literal) valueExpr;
                    if (!column.isValueCompatible(literal.getValue())) {
                        addError(SemanticError.ErrorType.TYPE_MISMATCH,
                                literal.getLine(), literal.getColumn(),
                                "Value type mismatch for column '" + column.getName() + "'");
                    }
                }
            }
        } else {
            // 检查指定的列是否存在
            for (Identifier column : columns) {
                if (!schema.hasColumn(column.getName())) {
                    addError(SemanticError.ErrorType.COLUMN_NOT_EXISTS,
                            column.getLine(), column.getColumn(),
                            "Column '" + column.getName() + "' does not exist in table '" + tableName + "'");
                }
            }
            
            // 检查列数与值数是否匹配
            if (columns.size() != values.getValues().size()) {
                addError(SemanticError.ErrorType.COLUMN_COUNT_MISMATCH,
                        values.getLine(), values.getColumn(),
                        "Expected " + columns.size() + " values, but got " + values.getValues().size());
            } else {
                // 检查每个值的类型
                for (int i = 0; i < columns.size(); i++) {
                    String columnName = columns.get(i).getName();
                    ColumnInfo column = schema.getColumn(columnName);
                    Expression valueExpr = values.getValues().get(i);
                    
                    if (column != null && valueExpr instanceof Literal) {
                        Literal literal = (Literal) valueExpr;
                        if (!column.isValueCompatible(literal.getValue())) {
                            addError(SemanticError.ErrorType.TYPE_MISMATCH,
                                    literal.getLine(), literal.getColumn(),
                                    "Value type mismatch for column '" + columnName + "'");
                        }
                    }
                }
            }
            
            // 检查NOT NULL约束
            for (ColumnInfo column : schema.getColumns()) {
                if (column.isNotNull()) {
                    boolean found = false;
                    for (Identifier col : columns) {
                        if (col.getName().equalsIgnoreCase(column.getName())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        addError(SemanticError.ErrorType.NOT_NULL_VIOLATION,
                                stmt.getLine(), stmt.getColumn(),
                                "NOT NULL column '" + column.getName() + "' requires a value");
                    }
                }
            }
        }
        
        return null;
    }
    
    @Override
    public Void visitSelectStatement(SelectStatement stmt) {
        String tableName = stmt.getTableName().getName();
        
        // 检查表是否存在
        if (!catalog.tableExists(tableName)) {
            addError(SemanticError.ErrorType.TABLE_NOT_EXISTS,
                    stmt.getTableName().getLine(), stmt.getTableName().getColumn(),
                    "Table '" + tableName + "' does not exist");
            return null;
        }
        
        TableSchema schema = catalog.getTableSchema(tableName);
        SelectList selectList = stmt.getSelectList();
        
        // 检查选择列表
        if (!selectList.isSelectAll()) {
            for (Expression expr : selectList.getItems()) {
                if (expr instanceof Identifier) {
                    Identifier column = (Identifier) expr;
                    if (!schema.hasColumn(column.getName())) {
                        addError(SemanticError.ErrorType.COLUMN_NOT_EXISTS,
                                column.getLine(), column.getColumn(),
                                "Column '" + column.getName() + "' does not exist in table '" + tableName + "'");
                    }
                }
            }
        }
        
        // 检查WHERE子句
        if (stmt.getWhereClause() != null) {
            checkWhereClause(stmt.getWhereClause(), schema);
        }
        
        return null;
    }
    
    @Override
    public Void visitDeleteStatement(DeleteStatement stmt) {
        String tableName = stmt.getTableName().getName();
        
        // 检查表是否存在
        if (!catalog.tableExists(tableName)) {
            addError(SemanticError.ErrorType.TABLE_NOT_EXISTS,
                    stmt.getTableName().getLine(), stmt.getTableName().getColumn(),
                    "Table '" + tableName + "' does not exist");
            return null;
        }
        
        TableSchema schema = catalog.getTableSchema(tableName);
        
        // 检查WHERE子句
        if (stmt.getWhereClause() != null) {
            checkWhereClause(stmt.getWhereClause(), schema);
        }
        
        return null;
    }
    
    @Override
    public Void visitIdentifier(Identifier expr) {
        return null;
    }
    
    @Override
    public Void visitLiteral(Literal expr) {
        return null;
    }
    
    @Override
    public Void visitBinaryExpression(BinaryExpression expr) {
        expr.getLeft().accept(this);
        expr.getRight().accept(this);
        return null;
    }
    
    @Override
    public Void visitColumnDefinition(ColumnDefinition expr) {
        return null;
    }
    
    @Override
    public Void visitValuesList(ValuesList expr) {
        for (Expression value : expr.getValues()) {
            value.accept(this);
        }
        return null;
    }
    
    @Override
    public Void visitWhereClause(WhereClause expr) {
        expr.getCondition().accept(this);
        return null;
    }
    
    @Override
    public Void visitSelectList(SelectList expr) {
        for (Expression item : expr.getItems()) {
            item.accept(this);
        }
        return null;
    }
    
    /**
     * 检查WHERE子句的语义
     */
    private void checkWhereClause(WhereClause whereClause, TableSchema schema) {
        Expression condition = whereClause.getCondition();
        checkConditionExpression(condition, schema);
    }
    
    /**
     * 检查条件表达式
     */
    private void checkConditionExpression(Expression expr, TableSchema schema) {
        if (expr instanceof BinaryExpression) {
            BinaryExpression binExpr = (BinaryExpression) expr;
            checkConditionExpression(binExpr.getLeft(), schema);
            checkConditionExpression(binExpr.getRight(), schema);
            
            // 检查比较操作的类型兼容性
            if (isComparisonOperator(binExpr.getOperator())) {
                // 简化的类型检查：确保至少一边是标识符且存在于表中
                Expression left = binExpr.getLeft();
                Expression right = binExpr.getRight();
                
                if (left instanceof Identifier) {
                    Identifier column = (Identifier) left;
                    if (!schema.hasColumn(column.getName())) {
                        addError(SemanticError.ErrorType.COLUMN_NOT_EXISTS,
                                column.getLine(), column.getColumn(),
                                "Column '" + column.getName() + "' does not exist");
                    }
                }
                
                if (right instanceof Identifier) {
                    Identifier column = (Identifier) right;
                    if (!schema.hasColumn(column.getName())) {
                        addError(SemanticError.ErrorType.COLUMN_NOT_EXISTS,
                                column.getLine(), column.getColumn(),
                                "Column '" + column.getName() + "' does not exist");
                    }
                }
            }
        } else if (expr instanceof Identifier) {
            Identifier column = (Identifier) expr;
            if (!schema.hasColumn(column.getName())) {
                addError(SemanticError.ErrorType.COLUMN_NOT_EXISTS,
                        column.getLine(), column.getColumn(),
                        "Column '" + column.getName() + "' does not exist");
            }
        }
    }
    
    /**
     * 检查是否为比较操作符
     */
    private boolean isComparisonOperator(TokenType operator) {
        return operator == TokenType.EQUALS || operator == TokenType.NOT_EQUALS ||
               operator == TokenType.LESS_THAN || operator == TokenType.GREATER_THAN ||
               operator == TokenType.LESS_EQUAL || operator == TokenType.GREATER_EQUAL;
    }
    
    /**
     * 检查数据类型是否有效
     */
    private boolean isValidDataType(TokenType dataType) {
        return dataType == TokenType.INT || dataType == TokenType.VARCHAR ||
               dataType == TokenType.CHAR || dataType == TokenType.FLOAT ||
               dataType == TokenType.DOUBLE || dataType == TokenType.BOOLEAN;
    }
    
    /**
     * 添加语义错误
     */
    private void addError(SemanticError.ErrorType errorType, int line, int column, String reason) {
        errors.add(new SemanticError(errorType, line, column, reason));
    }
    
    // 数据库级操作的语义分析方法
    
    @Override
    public Void visitCreateDatabaseStatement(CreateDatabaseStatement stmt) {
        // 数据库创建语句的语义分析
        // 暂时无需特殊检查，数据库名称在语法分析时已验证
        return null;
    }
    
    @Override
    public Void visitUseDatabaseStatement(UseDatabaseStatement stmt) {
        // USE DATABASE语句的语义分析
        // 可以在这里检查数据库是否存在
        return null;
    }
    
    @Override
    public Void visitDropDatabaseStatement(DropDatabaseStatement stmt) {
        // DROP DATABASE语句的语义分析
        // 可以在这里检查数据库是否存在
        return null;
    }
    
    @Override
    public Void visitShowDatabasesStatement(ShowDatabasesStatement stmt) {
        // SHOW DATABASES语句无需语义检查
        return null;
    }
    
    @Override
    public Void visitShowTablesStatement(ShowTablesStatement stmt) {
        // SHOW TABLES语句无需语义检查
        return null;
    }
    
    @Override
    public Void visitCreateUserStatement(CreateUserStatement stmt) {
        // CREATE USER语句无需语义检查
        return null;
    }
}
