package compiler.codegen;

/**
 * DELETE执行计划
 */
public class DeletePlan extends ExecutionPlan {
    private String tableName;
    
    public DeletePlan(String tableName) {
        super("DELETE");
        this.tableName = tableName;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    @Override
    protected String getDetails() {
        return "table=" + tableName;
    }
}
