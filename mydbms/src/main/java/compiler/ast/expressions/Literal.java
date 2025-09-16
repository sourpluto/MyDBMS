package compiler.ast.expressions;

import compiler.ast.ASTNode;
import compiler.ast.ASTVisitor;
import compiler.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * 字面量表达式
 */
public class Literal extends Expression {
    private Object value;
    private TokenType type;
    
    public Literal(int line, int column, Object value, TokenType type) {
        super(line, column);
        this.value = value;
        this.type = type;
    }
    
    public Object getValue() {
        return value;
    }
    
    public TokenType getType() {
        return type;
    }
    
    @Override
    public String getNodeType() {
        return "Literal";
    }
    
    @Override
    public List<ASTNode> getChildren() {
        return new ArrayList<>();
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitLiteral(this);
    }
    
    @Override
    public String toString() {
        if (type == TokenType.STRING_LITERAL) {
            return "'" + value.toString() + "'";
        }
        return value.toString();
    }
}
