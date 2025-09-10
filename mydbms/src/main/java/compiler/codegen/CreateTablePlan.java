package compiler.codegen;

import java.util.List;

/**
 * CREATE TABLE执行计划
 */
public class CreateTablePlan extends ExecutionPlan {
    private String tableName;
    private List<String> columnDefinitions;
    
    public CreateTablePlan(String tableName, List<String> columnDefinitions) {
        super("CREATE_TABLE");
        this.tableName = tableName;
        this.columnDefinitions = columnDefinitions;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public List<String> getColumnDefinitions() {
        return columnDefinitions;
    }
    
    @Override
    protected String getDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("table=").append(tableName);
        sb.append(", columns=[");
        for (int i = 0; i < columnDefinitions.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(columnDefinitions.get(i));
        }
        sb.append("]");
        return sb.toString();
    }
}
