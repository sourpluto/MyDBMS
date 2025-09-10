package compiler.ast.expressions;

import compiler.ast.ASTNode;
import compiler.ast.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * SELECT列表表达式
 */
public class SelectList extends Expression {
    private List<Expression> items;
    private boolean isSelectAll;
    
    public SelectList(int line, int column, List<Expression> items) {
        super(line, column);
        this.items = items != null ? items : new ArrayList<>();
        this.isSelectAll = false;
    }
    
    public SelectList(int line, int column, boolean isSelectAll) {
        super(line, column);
        this.items = new ArrayList<>();
        this.isSelectAll = isSelectAll;
    }
    
    public List<Expression> getItems() {
        return items;
    }
    
    public boolean isSelectAll() {
        return isSelectAll;
    }
    
    @Override
    public String getNodeType() {
        return "SelectList";
    }
    
    @Override
    public List<ASTNode> getChildren() {
        return new ArrayList<>(items);
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitSelectList(this);
    }
    
    @Override
    public String toString() {
        if (isSelectAll) {
            return "*";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(items.get(i).toString());
        }
        return sb.toString();
    }
}
