package compiler.codegen;

import compiler.ast.ASTNode;
import compiler.ast.ASTVisitor;
import compiler.ast.expressions.*;
import compiler.ast.statements.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 执行计划生成器 - 将AST转换为逻辑执行计划
 */
public class CodeGenerator implements ASTVisitor<ExecutionPlan> {
    
    /**
     * 生成执行计划
     */
    public ExecutionPlan generatePlan(ASTNode ast) {
        return ast.accept(this);
    }
    
    @Override
    public ExecutionPlan visitCreateTableStatement(CreateTableStatement stmt) {
        String tableName = stmt.getTableName().getName();
        List<String> columnDefs = new ArrayList<>();
        
        for (ColumnDefinition colDef : stmt.getColumns()) {
            StringBuilder sb = new StringBuilder();
            sb.append(colDef.getColumnName().getName());
            sb.append(" ").append(colDef.getDataType().getSymbol());
            
            if (colDef.getSize() != null) {
                sb.append("(").append(colDef.getSize()).append(")");
            }
            
            if (colDef.isNotNull()) {
                sb.append(" NOT NULL");
            }
            
            if (colDef.isPrimaryKey()) {
                sb.append(" PRIMARY KEY");
            }
            
            columnDefs.add(sb.toString());
        }
        
        return new CreateTablePlan(tableName, columnDefs);
    }
    
    @Override
    public ExecutionPlan visitInsertStatement(InsertStatement stmt) {
        String tableName = stmt.getTableName().getName();
        
        List<String> columns = null;
        if (!stmt.getColumns().isEmpty()) {
            columns = new ArrayList<>();
            for (Identifier col : stmt.getColumns()) {
                columns.add(col.getName());
            }
        }
        
        List<String> values = new ArrayList<>();
        for (Expression valueExpr : stmt.getValues().getValues()) {
            values.add(valueExpr.toString());
        }
        
        return new InsertPlan(tableName, columns, values);
    }
    
    @Override
    public ExecutionPlan visitSelectStatement(SelectStatement stmt) {
        String tableName = stmt.getTableName().getName();
        SelectList selectList = stmt.getSelectList();
        
        List<String> columns = null;
        boolean selectAll = selectList.isSelectAll();
        
        if (!selectAll) {
            columns = new ArrayList<>();
            for (Expression expr : selectList.getItems()) {
                if (expr instanceof Identifier) {
                    columns.add(((Identifier) expr).getName());
                } else {
                    columns.add(expr.toString());
                }
            }
        }
        
        SelectPlan selectPlan = new SelectPlan(tableName, columns, selectAll);
        
        // 如果有WHERE子句，添加过滤计划
        if (stmt.getWhereClause() != null) {
            FilterPlan filterPlan = new FilterPlan(stmt.getWhereClause().getCondition().toString());
            selectPlan.addChild(filterPlan);
        }
        
        return selectPlan;
    }
    
    @Override
    public ExecutionPlan visitDeleteStatement(DeleteStatement stmt) {
        String tableName = stmt.getTableName().getName();
        DeletePlan deletePlan = new DeletePlan(tableName);
        
        // 如果有WHERE子句，添加过滤计划
        if (stmt.getWhereClause() != null) {
            FilterPlan filterPlan = new FilterPlan(stmt.getWhereClause().getCondition().toString());
            deletePlan.addChild(filterPlan);
        }
        
        return deletePlan;
    }
    
    @Override
    public ExecutionPlan visitIdentifier(Identifier expr) {
        // 标识符通常不单独生成执行计划
        throw new UnsupportedOperationException("Identifier should not generate execution plan independently");
    }
    
    @Override
    public ExecutionPlan visitLiteral(Literal expr) {
        // 字面量通常不单独生成执行计划
        throw new UnsupportedOperationException("Literal should not generate execution plan independently");
    }
    
    @Override
    public ExecutionPlan visitBinaryExpression(BinaryExpression expr) {
        // 二元表达式通常作为过滤条件的一部分
        throw new UnsupportedOperationException("BinaryExpression should not generate execution plan independently");
    }
    
    @Override
    public ExecutionPlan visitColumnDefinition(ColumnDefinition expr) {
        // 列定义通常不单独生成执行计划
        throw new UnsupportedOperationException("ColumnDefinition should not generate execution plan independently");
    }
    
    @Override
    public ExecutionPlan visitValuesList(ValuesList expr) {
        // 值列表通常不单独生成执行计划
        throw new UnsupportedOperationException("ValuesList should not generate execution plan independently");
    }
    
    @Override
    public ExecutionPlan visitWhereClause(WhereClause expr) {
        // WHERE子句通常不单独生成执行计划
        throw new UnsupportedOperationException("WhereClause should not generate execution plan independently");
    }
    
    @Override
    public ExecutionPlan visitSelectList(SelectList expr) {
        // SELECT列表通常不单独生成执行计划
        throw new UnsupportedOperationException("SelectList should not generate execution plan independently");
    }
    
    // 数据库级操作的执行计划生成方法
    
    @Override
    public ExecutionPlan visitCreateDatabaseStatement(CreateDatabaseStatement stmt) {
        return new CreateDatabasePlan(stmt.getDatabaseName());
    }
    
    @Override
    public ExecutionPlan visitUseDatabaseStatement(UseDatabaseStatement stmt) {
        return new UseDatabasePlan(stmt.getDatabaseName());
    }
    
    @Override
    public ExecutionPlan visitDropDatabaseStatement(DropDatabaseStatement stmt) {
        return new DropDatabasePlan(stmt.getDatabaseName());
    }
    
    @Override
    public ExecutionPlan visitShowDatabasesStatement(ShowDatabasesStatement stmt) {
        return new ShowDatabasesPlan();
    }
    
    @Override
    public ExecutionPlan visitShowTablesStatement(ShowTablesStatement stmt) {
        return new ShowTablesPlan();
    }
    
    @Override
    public ExecutionPlan visitCreateUserStatement(CreateUserStatement stmt) {
        return new CreateUserPlan();
    }
}
