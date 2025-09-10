package compiler.ast.expressions;

import compiler.ast.ASTNode;
import compiler.ast.ASTVisitor;
import compiler.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * 二元表达式（用于WHERE子句中的条件）
 */
public class BinaryExpression extends Expression {
    private Expression left;
    private TokenType operator;
    private Expression right;
    
    public BinaryExpression(int line, int column, Expression left, TokenType operator, Expression right) {
        super(line, column);
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
    
    public Expression getLeft() {
        return left;
    }
    
    public TokenType getOperator() {
        return operator;
    }
    
    public Expression getRight() {
        return right;
    }
    
    @Override
    public String getNodeType() {
        return "BinaryExpression";
    }
    
    @Override
    public List<ASTNode> getChildren() {
        List<ASTNode> children = new ArrayList<>();
        if (left != null) {
            children.add(left);
        }
        if (right != null) {
            children.add(right);
        }
        return children;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitBinaryExpression(this);
    }
    
    @Override
    public String toString() {
        return left.toString() + " " + operator.getSymbol() + " " + right.toString();
    }
}
