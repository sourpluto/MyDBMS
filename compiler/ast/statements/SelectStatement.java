package compiler.ast.statements;

import compiler.ast.ASTNode;
import compiler.ast.ASTVisitor;
import compiler.ast.expressions.Identifier;
import compiler.ast.expressions.SelectList;
import compiler.ast.expressions.WhereClause;

import java.util.ArrayList;
import java.util.List;

/**
 * SELECT语句AST节点
 */
public class SelectStatement extends Statement {
    private SelectList selectList;
    private Identifier tableName;
    private WhereClause whereClause;
    
    public SelectStatement(int line, int column, SelectList selectList, Identifier tableName, WhereClause whereClause) {
        super(line, column);
        this.selectList = selectList;
        this.tableName = tableName;
        this.whereClause = whereClause;
    }
    
    public SelectList getSelectList() {
        return selectList;
    }
    
    public Identifier getTableName() {
        return tableName;
    }
    
    public WhereClause getWhereClause() {
        return whereClause;
    }
    
    @Override
    public String getNodeType() {
        return "SelectStatement";
    }
    
    @Override
    public List<ASTNode> getChildren() {
        List<ASTNode> children = new ArrayList<>();
        if (selectList != null) {
            children.add(selectList);
        }
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
        return visitor.visitSelectStatement(this);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ").append(selectList.toString());
        sb.append(" FROM ").append(tableName.getName());
        if (whereClause != null) {
            sb.append(" WHERE ").append(whereClause.toString());
        }
        return sb.toString();
    }
}
