package compiler.ast.expressions;

import compiler.ast.ASTNode;

/**
 * 表达式基类
 */
public abstract class Expression extends ASTNode {
    public Expression(int line, int column) {
        super(line, column);
    }
    
    @Override
    public String getNodeType() {
        return "Expression";
    }
}
