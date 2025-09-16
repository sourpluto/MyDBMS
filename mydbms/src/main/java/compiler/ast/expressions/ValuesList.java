package compiler.ast.expressions;

import compiler.ast.ASTNode;
import compiler.ast.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * VALUES列表表达式（INSERT语句中的值列表）
 */
public class ValuesList extends Expression {
    private List<Expression> values;
    
    public ValuesList(int line, int column, List<Expression> values) {
        super(line, column);
        this.values = values != null ? values : new ArrayList<>();
    }
    
    public List<Expression> getValues() {
        return values;
    }
    
    @Override
    public String getNodeType() {
        return "ValuesList";
    }
    
    @Override
    public List<ASTNode> getChildren() {
        return new ArrayList<>(values);
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitValuesList(this);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(values.get(i).toString());
        }
        sb.append(")");
        return sb.toString();
    }
}
