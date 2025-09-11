package compiler.ast;

import compiler.ast.statements.*;
import compiler.ast.expressions.*;

/**
 * AST访问者接口 - 用于访问者模式遍历AST
 */
public interface ASTVisitor<T> {
    // 语句访问方法
    T visitCreateTableStatement(CreateTableStatement stmt);
    T visitInsertStatement(InsertStatement stmt);
    T visitSelectStatement(SelectStatement stmt);
    T visitDeleteStatement(DeleteStatement stmt);
    
    // 数据库级操作语句访问方法
    T visitCreateDatabaseStatement(CreateDatabaseStatement stmt);
    T visitUseDatabaseStatement(UseDatabaseStatement stmt);
    T visitDropDatabaseStatement(DropDatabaseStatement stmt);
    T visitShowDatabasesStatement(ShowDatabasesStatement stmt);
    T visitShowTablesStatement(ShowTablesStatement stmt);
    T visitCreateUserStatement(CreateUserStatement stmt);
    
    // 表达式访问方法
    T visitIdentifier(Identifier expr);
    T visitLiteral(Literal expr);
    T visitBinaryExpression(BinaryExpression expr);
    T visitColumnDefinition(ColumnDefinition expr);
    T visitValuesList(ValuesList expr);
    T visitWhereClause(WhereClause expr);
    T visitSelectList(SelectList expr);
}
