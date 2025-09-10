package compiler.codegen;

/**
 * 过滤操作执行计划（WHERE子句）
 */
public class FilterPlan extends ExecutionPlan {
    private String condition;
    
    public FilterPlan(String condition) {
        super("FILTER");
        this.condition = condition;
    }
    
    public String getCondition() {
        return condition;
    }
    
    @Override
    protected String getDetails() {
        return "condition=" + condition;
    }
}
