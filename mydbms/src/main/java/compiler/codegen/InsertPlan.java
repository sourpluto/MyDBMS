package compiler.codegen;

import java.util.List;

/**
 * INSERT执行计划
 */
public class InsertPlan extends ExecutionPlan {
    private String tableName;
    private List<String> columns;
    private List<String> values;
    
    public InsertPlan(String tableName, List<String> columns, List<String> values) {
        super("INSERT");
        this.tableName = tableName;
        this.columns = columns;
        this.values = values;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public List<String> getColumns() {
        return columns;
    }
    
    public List<String> getValues() {
        return values;
    }
    
    @Override
    protected String getDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("table=").append(tableName);
        
        if (columns != null && !columns.isEmpty()) {
            sb.append(", columns=[");
            for (int i = 0; i < columns.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(columns.get(i));
            }
            sb.append("]");
        }
        
        sb.append(", values=[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(values.get(i));
        }
        sb.append("]");
        
        return sb.toString();
    }
}
