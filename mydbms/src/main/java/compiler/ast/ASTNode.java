package compiler.ast;

import java.util.List;

/**
 * 抽象语法树节点基类
 */
public abstract class ASTNode {
    private int line;
    private int column;
    
    public ASTNode(int line, int column) {
        this.line = line;
        this.column = column;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
    
    /**
     * 获取节点类型
     */
    public abstract String getNodeType();
    
    /**
     * 获取子节点
     */
    public abstract List<ASTNode> getChildren();
    
    /**
     * 接受访问者模式
     */
    public abstract <T> T accept(ASTVisitor<T> visitor);
    
    /**
     * 转换为字符串表示（用于调试）
     */
    public abstract String toString();
    
    /**
     * 转换为树形结构字符串
     */
    public String toTreeString() {
        return toTreeString(0);
    }
    
    protected String toTreeString(int depth) {
        StringBuilder sb = new StringBuilder();
        String indent = "  ".repeat(depth);
        sb.append(indent).append(getNodeType()).append("\n");
        
        for (ASTNode child : getChildren()) {
            if (child != null) {
                sb.append(child.toTreeString(depth + 1));
            }
        }
        
        return sb.toString();
    }
}
