package compiler.ast.statements;

import compiler.ast.ASTNode;

/**
 * SQL语句基类
 */
public abstract class Statement extends ASTNode {
    public Statement(int line, int column) {
        super(line, column);
    }
    
    @Override
    public String getNodeType() {
        return "Statement";
    }
}
