package compiler.codegen;

/**
 * DROP DATABASE执行计划
 */
public class DropDatabasePlan extends ExecutionPlan {
    private String databaseName;
    
    public DropDatabasePlan(String databaseName) {
        super("DROP DATABASE");
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
        return String.format("DropDatabasePlan{databaseName='%s'}", databaseName);
    }
}
