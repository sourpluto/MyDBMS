package compiler.catalog;

import compiler.ast.expressions.ColumnDefinition;
import compiler.ast.statements.CreateTableStatement;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 模式目录 - 维护数据库中所有表的模式信息
 */
public class Catalog {
    private Map<String, TableSchema> tables;
    
    public Catalog() {
        this.tables = new HashMap<>();
    }
    
    /**
     * 创建表
     */
    public void createTable(CreateTableStatement createStmt) throws CatalogException {
        String tableName = createStmt.getTableName().getName();
        
        if (tables.containsKey(tableName.toUpperCase())) {
            throw new CatalogException("Table '" + tableName + "' already exists");
        }
        
        TableSchema schema = new TableSchema(tableName);
        
        // 添加列定义
        for (ColumnDefinition colDef : createStmt.getColumns()) {
            ColumnInfo column = new ColumnInfo(
                colDef.getColumnName().getName(),
                colDef.getDataType(),
                colDef.getSize()
            );
            
            column.setPrimaryKey(colDef.isPrimaryKey());
            column.setNotNull(colDef.isNotNull());
            
            schema.addColumn(column);
        }
        
        tables.put(tableName.toUpperCase(), schema);
    }
    
    /**
     * 删除表
     */
    public void dropTable(String tableName) throws CatalogException {
        if (!tables.containsKey(tableName.toUpperCase())) {
            throw new CatalogException("Table '" + tableName + "' does not exist");
        }
        
        tables.remove(tableName.toUpperCase());
    }
    
    /**
     * 获取表模式
     */
    public TableSchema getTableSchema(String tableName) {
        // 首先从内存中获取
        TableSchema schema = tables.get(tableName.toUpperCase());
        if (schema != null) {
            return schema;
        }
        
        // 如果内存中没有，尝试从XML文件加载
        if (checkTableExistsInXML(tableName)) {
            return tables.get(tableName.toUpperCase());
        }
        
        return null;
    }
    
    /**
     * 检查表是否存在
     */
    public boolean tableExists(String tableName) {
        // 首先检查内存中的表
        if (tables.containsKey(tableName.toUpperCase())) {
            return true;
        }
        
        // 如果内存中没有，检查XML文件是否存在
        return checkTableExistsInXML(tableName);
    }
    
    /**
     * 检查XML文件中表是否存在
     */
    private boolean checkTableExistsInXML(String tableName) {
        try {
            // 检查当前数据库目录
            String currentDb = function.UseDatabase.dbName;
            if (currentDb == null) {
                return false;
            }
            
            File tableDir = new File("./mydatabase/" + currentDb + "/" + tableName);
            if (!tableDir.exists() || !tableDir.isDirectory()) {
                return false;
            }
            
            // 检查配置文件是否存在
            File configFile = new File(tableDir, tableName + "-config.xml");
            if (!configFile.exists()) {
                return false;
            }
            
            // 从XML文件加载表信息到内存
            loadTableFromXML(tableName, configFile);
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 从XML文件加载表信息
     */
    private void loadTableFromXML(String tableName, File configFile) {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(configFile);
            Element root = document.getRootElement();
            
            // 创建表模式
            TableSchema schema = new TableSchema(tableName);
            
            // 从根元素的属性中获取列定义
            // 例如：<q1s id="INT"> 表示有一个名为id的INT列
            for (Object attr : root.attributes()) {
                org.dom4j.Attribute attribute = (org.dom4j.Attribute) attr;
                String columnName = attribute.getName();
                String dataTypeStr = attribute.getValue();
                
                // 根据数据类型字符串确定TokenType
                compiler.lexer.TokenType dataType = compiler.lexer.TokenType.INT; // 默认
                if ("VARCHAR".equalsIgnoreCase(dataTypeStr)) {
                    dataType = compiler.lexer.TokenType.VARCHAR;
                } else if ("CHAR".equalsIgnoreCase(dataTypeStr)) {
                    dataType = compiler.lexer.TokenType.CHAR;
                }
                
                ColumnInfo column = new ColumnInfo(columnName, dataType, null);
                schema.addColumn(column);
            }
            
            // 添加到内存中
            tables.put(tableName.toUpperCase(), schema);
            
        } catch (Exception e) {
            // 加载失败，忽略
            System.err.println("Failed to load table from XML: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有表名
     */
    public Set<String> getTableNames() {
        return tables.keySet();
    }
    
    /**
     * 检查列是否存在
     */
    public boolean columnExists(String tableName, String columnName) {
        TableSchema schema = getTableSchema(tableName);
        return schema != null && schema.hasColumn(columnName);
    }
    
    /**
     * 获取列信息
     */
    public ColumnInfo getColumnInfo(String tableName, String columnName) {
        TableSchema schema = getTableSchema(tableName);
        return schema != null ? schema.getColumn(columnName) : null;
    }
    
    /**
     * 清空目录
     */
    public void clear() {
        tables.clear();
    }
    
    /**
     * 获取表的数量
     */
    public int getTableCount() {
        return tables.size();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Database Catalog:\n");
        sb.append("================\n");
        
        if (tables.isEmpty()) {
            sb.append("No tables defined.\n");
        } else {
            for (TableSchema schema : tables.values()) {
                sb.append(schema.toString()).append("\n");
            }
        }
        
        return sb.toString();
    }
}
