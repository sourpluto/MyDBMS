 package compiler.execution;

import compiler.codegen.*;
import function.*;
import storage.api.StorageEngine;
import storage.buffer.BufferManager;
import storage.xml.XmlStorageEngine;
import storage.paged.PagedStorageEngine;
import java.io.File;

import java.util.List;

/**
 * 执行计划执行器 - 将执行计划转换为实际的数据库操作
 */
public class PlanExecutor {

	private final StorageEngine storage;

	public PlanExecutor() {
		// 通过 -Dengine=paged 可切换到页式引擎；默认XML
		String engine = System.getProperty("engine", "xml");
		if ("paged".equalsIgnoreCase(engine)) {
			this.storage = new PagedStorageEngine(new File("./mydatabase"), new BufferManager(128));
			System.out.println("[Engine] Using PagedStorageEngine");
		} else {
			this.storage = new XmlStorageEngine(new BufferManager(64));
			System.out.println("[Engine] Using XmlStorageEngine");
		}
	}

	public PlanExecutor(StorageEngine storage) {
		this.storage = storage;
	}
    
    /**
     * 执行给定的执行计划
     */
    public void execute(ExecutionPlan plan) {
        if (plan == null) {
            return;
        }
        
        System.out.println("\n=== 执行SQL语句 ===");
        
        try {
            executeInternal(plan);
        } catch (Exception e) {
            System.out.println("执行失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void executeInternal(ExecutionPlan plan) {
        // 根据执行计划类型分发到具体的执行方法
        if (plan instanceof CreateDatabasePlan) {
            executeCreateDatabase((CreateDatabasePlan) plan);
        } else if (plan instanceof UseDatabasePlan) {
            executeUseDatabase((UseDatabasePlan) plan);
        } else if (plan instanceof DropDatabasePlan) {
            executeDropDatabase((DropDatabasePlan) plan);
        } else if (plan instanceof ShowDatabasesPlan) {
            executeShowDatabases((ShowDatabasesPlan) plan);
        } else if (plan instanceof ShowTablesPlan) {
            executeShowTables((ShowTablesPlan) plan);
        } else if (plan instanceof CreateUserPlan) {
            executeCreateUser((CreateUserPlan) plan);
        } else if (plan instanceof CreateTablePlan) {
            executeCreateTable((CreateTablePlan) plan);
        } else if (plan instanceof InsertPlan) {
            executeInsert((InsertPlan) plan);
        } else if (plan instanceof SelectPlan) {
            executeSelect((SelectPlan) plan);
        } else if (plan instanceof DeletePlan) {
            executeDelete((DeletePlan) plan);
        } else {
            System.out.println("未知的执行计划类型：" + plan.getClass().getSimpleName());
        }
        
        // 递归执行子计划（跳过FilterPlan，它们由父计划处理）
        for (ExecutionPlan child : plan.getChildren()) {
            if (!(child instanceof FilterPlan)) {
                executeInternal(child);
            }
        }
    }
    
    private void executeCreateDatabase(CreateDatabasePlan plan) {
        System.out.println("执行：创建数据库 " + plan.getDatabaseName());
        try {
            storage.createDatabase(plan.getDatabaseName());
        } catch (Exception e) {
            System.out.println("创建数据库失败：" + e.getMessage());
        }
    }
    
    private void executeUseDatabase(UseDatabasePlan plan) {
        System.out.println("执行：使用数据库 " + plan.getDatabaseName());
        UseDatabase.dbName = plan.getDatabaseName();
        
        // 检查数据库是否存在
        if (!IsLegal.isDatabase()) {
            System.out.println("错误：数据库 " + plan.getDatabaseName() + " 不存在");
            UseDatabase.dbName = null;
        } else {
            System.out.println("已切换到数据库：" + plan.getDatabaseName());
        }
    }
    
    private void executeDropDatabase(DropDatabasePlan plan) {
        System.out.println("执行：删除数据库 " + plan.getDatabaseName());
        try {
            DropDatabase.deleteDB(plan.getDatabaseName());
        } catch (Exception e) {
            System.out.println("删除数据库失败：" + e.getMessage());
        }
    }
    
    private void executeShowDatabases(ShowDatabasesPlan plan) {
        System.out.println("执行：显示所有数据库");
        System.out.println("数据库列表：");
        ShowDatabases.showDatabase();
    }
    
    private void executeShowTables(ShowTablesPlan plan) {
        System.out.println("执行：显示所有表");
        if (UseDatabase.dbName == null) {
            System.out.println("错误：请先使用 USE DATABASE 选择数据库");
            return;
        }
        System.out.println("数据库 " + UseDatabase.dbName + " 中的表：");
        try {
            for (String t : storage.listTables(UseDatabase.dbName)) {
                System.out.println(t);
            }
        } catch (Exception e) {
            System.out.println("显示表失败：" + e.getMessage());
        }
    }
    
    private void executeCreateUser(CreateUserPlan plan) {
        System.out.println("执行：创建用户");
        try {
            CreateUser.createUser();
        } catch (Exception e) {
            System.out.println("创建用户失败：" + e.getMessage());
        }
    }
    
    private void executeCreateTable(CreateTablePlan plan) {
        System.out.println("执行：创建表 " + plan.getTableName());
        
        // 检查是否已选择数据库
        if (UseDatabase.dbName == null) {
            System.out.println("错误：请先使用 USE DATABASE 选择数据库");
            return;
        }
        
        try {
            storage.createTable(UseDatabase.dbName, plan.getTableName(), plan.getColumnDefinitions());
        } catch (Exception e) {
            System.out.println("创建表失败：" + e.getMessage());
        }
    }
    
    private void executeInsert(InsertPlan plan) {
        System.out.println("执行：插入数据到表 " + plan.getTableName());
        
        // 检查是否已选择数据库
        if (UseDatabase.dbName == null) {
            System.out.println("错误：请先使用 USE DATABASE 选择数据库");
            return;
        }
        
        try {
            List<String> columns = plan.getColumns();
            List<String> values = plan.getValues();
            storage.insert(UseDatabase.dbName, plan.getTableName(), columns, values);
        } catch (Exception e) {
            System.out.println("插入数据失败：" + e.getMessage());
        }
    }
    
    private void executeSelect(SelectPlan plan) {
        System.out.println("执行：查询数据从表 " + plan.getTableName());
        
        // 检查是否已选择数据库
        if (UseDatabase.dbName == null) {
            System.out.println("错误：请先使用 USE DATABASE 选择数据库");
            return;
        }
        
        try {
            // 准备查询参数
            List<String> columns = null;
            if (!plan.isSelectAll()) {
                columns = plan.getSelectColumns();
            }
            
            storage.select(UseDatabase.dbName, plan.getTableName(), columns);
        } catch (Exception e) {
            System.out.println("查询数据失败：" + e.getMessage());
        }
    }
    
    private void executeDelete(DeletePlan plan) {
        System.out.println("执行：删除数据从表 " + plan.getTableName());
        
        // 检查是否已选择数据库
        if (UseDatabase.dbName == null) {
            System.out.println("错误：请先使用 USE DATABASE 选择数据库");
            return;
        }
        
        try {
            // 获取WHERE条件（如果有子计划）
            String whereCondition = null;
            for (ExecutionPlan child : plan.getChildren()) {
                if (child instanceof FilterPlan) {
                    whereCondition = ((FilterPlan) child).getCondition();
                    break;
                }
            }
            
            storage.delete(UseDatabase.dbName, plan.getTableName(), whereCondition);
        } catch (Exception e) {
            System.out.println("删除数据失败：" + e.getMessage());
        }
    }
}
