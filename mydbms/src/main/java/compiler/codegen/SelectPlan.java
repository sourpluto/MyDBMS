package compiler.codegen;

import java.util.List;

/**
 * SELECT执行计划
 */
public class SelectPlan extends ExecutionPlan {
    private String tableName;
    private List<String> selectColumns;
    private boolean selectAll;
    
    public SelectPlan(String tableName, List<String> selectColumns, boolean selectAll) {
        super("SELECT");
        this.tableName = tableName;
        this.selectColumns = selectColumns;
        this.selectAll = selectAll;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public List<String> getSelectColumns() {
        return selectColumns;
    }
    
    public boolean isSelectAll() {
        return selectAll;
    }
    
    @Override
    protected String getDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("table=").append(tableName);
        
        if (selectAll) {
            sb.append(", columns=*");
        } else {
            sb.append(", columns=[");
            for (int i = 0; i < selectColumns.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(selectColumns.get(i));
            }
            sb.append("]");
        }
        
        return sb.toString();
    }
}
