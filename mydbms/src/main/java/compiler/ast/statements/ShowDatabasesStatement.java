package compiler.ast.statements;

import compiler.ast.ASTNode;
import compiler.ast.ASTVisitor;
import java.util.List;
import java.util.ArrayList;

/**
 * SHOW DATABASES语句的AST节点
 */
public class ShowDatabasesStatement extends Statement {
    
    public ShowDatabasesStatement(int line, int column) {
        super(line, column);
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitShowDatabasesStatement(this);
    }
    
    @Override
    public List<ASTNode> getChildren() {
        return new ArrayList<>();
    }
    
    @Override
    public String toString() {
        return "ShowDatabasesStatement{}";
    }
}
