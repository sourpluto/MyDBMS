package compiler.ast.expressions;

import compiler.ast.ASTNode;
import compiler.ast.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * 标识符表达式
 */
public class Identifier extends Expression {
    private String name;
    
    public Identifier(int line, int column, String name) {
        super(line, column);
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String getNodeType() {
        return "Identifier";
    }
    
    @Override
    public List<ASTNode> getChildren() {
        return new ArrayList<>();
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitIdentifier(this);
    }
    
    @Override
    public String toString() {
        return name;
    }
}
