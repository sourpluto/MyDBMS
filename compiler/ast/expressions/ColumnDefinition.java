package compiler.ast.expressions;

import compiler.ast.ASTNode;
import compiler.ast.ASTVisitor;
import compiler.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * 列定义表达式（CREATE TABLE中的列定义）
 */
public class ColumnDefinition extends Expression {
    private Identifier columnName;
    private TokenType dataType;
    private Integer size;  // 对于VARCHAR(n)等类型
    private boolean isPrimaryKey;
    private boolean isNotNull;
    
    public ColumnDefinition(int line, int column, Identifier columnName, TokenType dataType) {
        super(line, column);
        this.columnName = columnName;
        this.dataType = dataType;
        this.size = null;
        this.isPrimaryKey = false;
        this.isNotNull = false;
    }
    
    public ColumnDefinition(int line, int column, Identifier columnName, TokenType dataType, Integer size) {
        this(line, column, columnName, dataType);
        this.size = size;
    }
    
    public Identifier getColumnName() {
        return columnName;
    }
    
    public TokenType getDataType() {
        return dataType;
    }
    
    public Integer getSize() {
        return size;
    }
    
    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }
    
    public void setPrimaryKey(boolean primaryKey) {
        this.isPrimaryKey = primaryKey;
    }
    
    public boolean isNotNull() {
        return isNotNull;
    }
    
    public void setNotNull(boolean notNull) {
        this.isNotNull = notNull;
    }
    
    @Override
    public String getNodeType() {
        return "ColumnDefinition";
    }
    
    @Override
    public List<ASTNode> getChildren() {
        List<ASTNode> children = new ArrayList<>();
        if (columnName != null) {
            children.add(columnName);
        }
        return children;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitColumnDefinition(this);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(columnName.getName()).append(" ").append(dataType.getSymbol());
        
        if (size != null) {
            sb.append("(").append(size).append(")");
        }
        
        if (isNotNull) {
            sb.append(" NOT NULL");
        }
        
        if (isPrimaryKey) {
            sb.append(" PRIMARY KEY");
        }
        
        return sb.toString();
    }
}
