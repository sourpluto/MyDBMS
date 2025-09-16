package compiler.codegen;

/**
 * USE DATABASE执行计划
 */
public class UseDatabasePlan extends ExecutionPlan {
    private String databaseName;
    
    public UseDatabasePlan(String databaseName) {
        super("USE DATABASE");
        this.databaseName = databaseName;
    }
    
    public String getDatabaseName() {
        return databaseName;
    }
    
    @Override
    protected String getDetails() {
        return "database=" + databaseName;
    }
    
    @Override
    public String toString() {
        return String.format("UseDatabasePlan{databaseName='%s'}", databaseName);
    }
}
