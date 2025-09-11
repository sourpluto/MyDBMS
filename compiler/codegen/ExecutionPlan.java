package compiler.codegen;

import java.util.ArrayList;
import java.util.List;

/**
 * 执行计划基类
 */
public abstract class ExecutionPlan {
    protected String planType;
    protected List<ExecutionPlan> children;
    
    public ExecutionPlan(String planType) {
        this.planType = planType;
        this.children = new ArrayList<>();
    }
    
    public String getPlanType() {
        return planType;
    }
    
    public List<ExecutionPlan> getChildren() {
        return children;
    }
    
    public void addChild(ExecutionPlan child) {
        children.add(child);
    }
    
    /**
     * 转换为树形结构字符串
     */
    public String toTreeString() {
        return toTreeString(0);
    }
    
    protected String toTreeString(int depth) {
        StringBuilder sb = new StringBuilder();
        String indent = "  ".repeat(depth);
        sb.append(indent).append(planType);
        
        String details = getDetails();
        if (details != null && !details.isEmpty()) {
            sb.append(" (").append(details).append(")");
        }
        sb.append("\n");
        
        for (ExecutionPlan child : children) {
            sb.append(child.toTreeString(depth + 1));
        }
        
        return sb.toString();
    }
    
    /**
     * 转换为JSON格式
     */
    public String toJSON() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\": \"").append(planType).append("\"");
        
        String details = getDetails();
        if (details != null && !details.isEmpty()) {
            sb.append(", \"details\": \"").append(details).append("\"");
        }
        
        if (!children.isEmpty()) {
            sb.append(", \"children\": [");
            for (int i = 0; i < children.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(children.get(i).toJSON());
            }
            sb.append("]");
        }
        
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * 转换为S表达式格式
     */
    public String toSExpression() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(planType);
        
        String details = getDetails();
        if (details != null && !details.isEmpty()) {
            sb.append(" ").append(details);
        }
        
        for (ExecutionPlan child : children) {
            sb.append(" ").append(child.toSExpression());
        }
        
        sb.append(")");
        return sb.toString();
    }
    
    /**
     * 获取计划的详细信息（子类实现）
     */
    protected abstract String getDetails();
}
