package compiler.ast.statements;

import compiler.ast.ASTNode;
import compiler.ast.ASTVisitor;
import compiler.ast.expressions.Identifier;
import compiler.ast.expressions.ValuesList;

import java.util.ArrayList;
import java.util.List;

/**
 * INSERT语句AST节点
 */
public class InsertStatement extends Statement {
    private Identifier tableName;
    private List<Identifier> columns;
    private ValuesList values;
    
    public InsertStatement(int line, int column, Identifier tableName, List<Identifier> columns, ValuesList values) {
        super(line, column);
        this.tableName = tableName;
        this.columns = columns != null ? columns : new ArrayList<>();
        this.values = values;
    }
    
    public Identifier getTableName() {
        return tableName;
    }
    
    public List<Identifier> getColumns() {
        return columns;
    }
    
    public ValuesList getValues() {
        return values;
    }
    
    @Override
    public String getNodeType() {
        return "InsertStatement";
    }
    
    @Override
    public List<ASTNode> getChildren() {
        List<ASTNode> children = new ArrayList<>();
        if (tableName != null) {
            children.add(tableName);
        }
        children.addAll(columns);
        if (values != null) {
            children.add(values);
        }
        return children;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitInsertStatement(this);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(tableName.getName());
        
        if (!columns.isEmpty()) {
            sb.append(" (");
            for (int i = 0; i < columns.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(columns.get(i).getName());
            }
            sb.append(")");
        }
        
        sb.append(" VALUES ").append(values.toString());
        return sb.toString();
    }
}
