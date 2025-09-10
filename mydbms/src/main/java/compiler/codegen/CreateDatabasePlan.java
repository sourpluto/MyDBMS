package compiler.codegen;

/**
 * CREATE DATABASE执行计划
 */
public class CreateDatabasePlan extends ExecutionPlan {
    private String databaseName;
    
    public CreateDatabasePlan(String databaseName) {
        super("CREATE DATABASE");
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
        return String.format("CreateDatabasePlan{databaseName='%s'}", databaseName);
    }
}
