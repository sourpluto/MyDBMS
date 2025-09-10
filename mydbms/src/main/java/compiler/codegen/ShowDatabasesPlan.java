package compiler.codegen;

/**
 * SHOW DATABASES执行计划
 */
public class ShowDatabasesPlan extends ExecutionPlan {
    
    public ShowDatabasesPlan() {
        super("SHOW DATABASES");
    }
    
    @Override
    protected String getDetails() {
        return "";
    }
    
    @Override
    public String toString() {
        return "ShowDatabasesPlan{}";
    }
}
