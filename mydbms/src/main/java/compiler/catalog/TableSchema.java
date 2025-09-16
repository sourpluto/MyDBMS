package compiler.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 表模式 - 存储表的结构信息
 */
public class TableSchema {
    private String tableName;
    private List<ColumnInfo> columns;
    private Map<String, ColumnInfo> columnMap;
    private String primaryKey;
    
    public TableSchema(String tableName) {
        this.tableName = tableName;
        this.columns = new ArrayList<>();
        this.columnMap = new HashMap<>();
        this.primaryKey = null;
    }
    
    public void addColumn(ColumnInfo column) {
        columns.add(column);
        columnMap.put(column.getName().toUpperCase(), column);
        
        if (column.isPrimaryKey()) {
            this.primaryKey = column.getName();
        }
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public List<ColumnInfo> getColumns() {
        return new ArrayList<>(columns);
    }
    
    public ColumnInfo getColumn(String columnName) {
        return columnMap.get(columnName.toUpperCase());
    }
    
    public boolean hasColumn(String columnName) {
        return columnMap.containsKey(columnName.toUpperCase());
    }
    
    public String getPrimaryKey() {
        return primaryKey;
    }
    
    public int getColumnCount() {
        return columns.size();
    }
    
    public int getColumnIndex(String columnName) {
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getName().equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Table: ").append(tableName).append("\n");
        sb.append("Columns:\n");
        for (ColumnInfo column : columns) {
            sb.append("  ").append(column.toString()).append("\n");
        }
        if (primaryKey != null) {
            sb.append("Primary Key: ").append(primaryKey).append("\n");
        }
        return sb.toString();
    }
}

