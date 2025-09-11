package compiler.ast.statements;

import compiler.ast.ASTNode;
import compiler.ast.ASTVisitor;
import compiler.ast.expressions.ColumnDefinition;
import compiler.ast.expressions.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * CREATE TABLE语句AST节点
 */
public class CreateTableStatement extends Statement {
    private Identifier tableName;
    private List<ColumnDefinition> columns;
    
    public CreateTableStatement(int line, int column, Identifier tableName, List<ColumnDefinition> columns) {
        super(line, column);
        this.tableName = tableName;
        this.columns = columns != null ? columns : new ArrayList<>();
    }
    
    public Identifier getTableName() {
        return tableName;
    }
    
    public List<ColumnDefinition> getColumns() {
        return columns;
    }
    
    @Override
    public String getNodeType() {
        return "CreateTableStatement";
    }
    
    @Override
    public List<ASTNode> getChildren() {
        List<ASTNode> children = new ArrayList<>();
        if (tableName != null) {
            children.add(tableName);
        }
        children.addAll(columns);
        return children;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitCreateTableStatement(this);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(tableName.getName()).append(" (");
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(columns.get(i).toString());
        }
        sb.append(")");
        return sb.toString();
    }
}
