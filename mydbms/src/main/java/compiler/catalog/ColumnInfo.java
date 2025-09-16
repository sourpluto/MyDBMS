package compiler.catalog;

import compiler.lexer.TokenType;

/**
 * 列信息类
 */
public class ColumnInfo {
    private String name;
    private TokenType dataType;
    private Integer size;
    private boolean isPrimaryKey;
    private boolean isNotNull;
    
    public ColumnInfo(String name, TokenType dataType) {
        this.name = name;
        this.dataType = dataType;
        this.size = null;
        this.isPrimaryKey = false;
        this.isNotNull = false;
    }
    
    public ColumnInfo(String name, TokenType dataType, Integer size) {
        this(name, dataType);
        this.size = size;
    }
    
    public String getName() {
        return name;
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
    
    /**
     * 检查值是否与列的数据类型兼容
     */
    public boolean isValueCompatible(Object value) {
        if (value == null && !isNotNull) {
            return true;
        }
        
        if (value == null && isNotNull) {
            return false;
        }
        
        switch (dataType) {
            case INT:
                return value instanceof Integer;
            case FLOAT:
            case DOUBLE:
                return value instanceof Double || value instanceof Float;
            case VARCHAR:
            case CHAR:
                if (value instanceof String) {
                    String str = (String) value;
                    return size == null || str.length() <= size;
                }
                return false;
            case BOOLEAN:
                return value instanceof Boolean;
            default:
                return false;
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" ").append(dataType.getSymbol());
        
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
