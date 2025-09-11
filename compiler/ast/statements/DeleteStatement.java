package compiler.ast.statements;

import compiler.ast.ASTNode;
import compiler.ast.ASTVisitor;
import compiler.ast.expressions.Identifier;
import compiler.ast.expressions.WhereClause;

import java.util.ArrayList;
import java.util.List;

/**
 * DELETE语句AST节点
 */
public class DeleteStatement extends Statement {
    private Identifier tableName;
    private WhereClause whereClause;
    
    public DeleteStatement(int line, int column, Identifier tableName, WhereClause whereClause) {
        super(line, column);
        this.tableName = tableName;
        this.whereClause = whereClause;
    }
    
    public Identifier getTableName() {
        return tableName;
    }
    
    public WhereClause getWhereClause() {
        return whereClause;
    }
    
    @Override
    public String getNodeType() {
        return "DeleteStatement";
    }
    
    @Override
    public List<ASTNode> getChildren() {
        List<ASTNode> children = new ArrayList<>();
        if (tableName != null) {
            children.add(tableName);
        }
        if (whereClause != null) {
            children.add(whereClause);
        }
        return children;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitDeleteStatement(this);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ").append(tableName.getName());
        if (whereClause != null) {
            sb.append(" WHERE ").append(whereClause.toString());
        }
        return sb.toString();
    }
}
