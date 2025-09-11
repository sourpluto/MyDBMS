package compiler.ast.statements;

import compiler.ast.ASTNode;
import compiler.ast.ASTVisitor;
import java.util.List;
import java.util.ArrayList;

/**
 * CREATE DATABASE语句的AST节点
 */
public class CreateDatabaseStatement extends Statement {
    private String databaseName;
    
    public CreateDatabaseStatement(int line, int column, String databaseName) {
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
        return visitor.visitCreateDatabaseStatement(this);
    }
    
    @Override
    public List<ASTNode> getChildren() {
        return new ArrayList<>();
    }
    
    @Override
    public String toString() {
        return String.format("CreateDatabaseStatement{databaseName='%s'}", databaseName);
    }
}
