package compiler.ast.statements;

import compiler.ast.ASTNode;
import compiler.ast.ASTVisitor;
import java.util.List;
import java.util.ArrayList;

/**
 * DROP DATABASE语句的AST节点
 */
public class DropDatabaseStatement extends Statement {
    private String databaseName;
    
    public DropDatabaseStatement(int line, int column, String databaseName) {
        super(line, column);
        this.databaseName = databaseName;
    }
    
    public String getDatabaseName() {
        return databaseName;
    }
    
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitDropDatabaseStatement(this);
    }
    
    @Override
    public List<ASTNode> getChildren() {
        return new ArrayList<>();
    }
    
    @Override
    public String toString() {
        return String.format("DropDatabaseStatement{databaseName='%s'}", databaseName);
    }
}
