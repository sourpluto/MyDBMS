package compiler.ast.expressions;

import compiler.ast.ASTNode;
import compiler.ast.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * WHERE子句表达式
 */
public class WhereClause extends Expression {
    private Expression condition;
    
    public WhereClause(int line, int column, Expression condition) {
        super(line, column);
        this.condition = condition;
    }
    
    public Expression getCondition() {
        return condition;
    }
    
    @Override
    public String getNodeType() {
        return "WhereClause";
    }
    
    @Override
    public List<ASTNode> getChildren() {
        List<ASTNode> children = new ArrayList<>();
        if (condition != null) {
            children.add(condition);
        }
        return children;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitWhereClause(this);
    }
    
    @Override
    public String toString() {
        return condition.toString();
    }
}
