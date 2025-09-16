package compiler.ast.statements;

import compiler.ast.ASTNode;
import compiler.ast.ASTVisitor;
import java.util.List;
import java.util.ArrayList;

/**
 * CREATE USER语句的AST节点
 */
public class CreateUserStatement extends Statement {
    
    public CreateUserStatement(int line, int column) {
        super(line, column);
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitCreateUserStatement(this);
    }
    
    @Override
    public List<ASTNode> getChildren() {
        return new ArrayList<>();
    }
    
    @Override
    public String toString() {
        return "CreateUserStatement{}";
    }
}
