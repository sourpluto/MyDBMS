package compiler.codegen;

/**
 * SHOW TABLES执行计划
 */
public class ShowTablesPlan extends ExecutionPlan {
    
    public ShowTablesPlan() {
        super("SHOW TABLES");
    }
    
    @Override
    protected String getDetails() {
        return "";
    }
    
    @Override
    public String toString() {
        return "ShowTablesPlan{}";
    }
}
