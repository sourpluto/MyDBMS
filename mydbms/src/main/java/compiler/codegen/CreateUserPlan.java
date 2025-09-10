package compiler.codegen;

/**
 * CREATE USER执行计划
 */
public class CreateUserPlan extends ExecutionPlan {
    
    public CreateUserPlan() {
        super("CREATE USER");
    }
    
    @Override
    protected String getDetails() {
        return "";
    }
    
    @Override
    public String toString() {
        return "CreateUserPlan{}";
    }
}
