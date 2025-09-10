package storage.xml;

import storage.api.StorageEngine;
import storage.buffer.BufferManager;

import function.*;

import java.io.File;
import java.io.IOException;
import function.DeleteDataFromTable;
import java.util.ArrayList;
import java.util.List;

/**
 * XML 存储引擎适配器：复用现有 function 包里的实现，充当执行器与存储的桥梁。
 * 保持对外接口稳定，内部直接调用原有函数，不改变原有行为。
 */
public class XmlStorageEngine implements StorageEngine {

	private final BufferManager bufferManager;

	public XmlStorageEngine(BufferManager bufferManager) {
		this.bufferManager = bufferManager;
	}

	@Override
	public void createDatabase(String databaseName) throws IOException {
		CreateDatabase.createDB(databaseName);
	}

	@Override
	public void dropDatabase(String databaseName) throws IOException {
		DropDatabase.deleteDB(databaseName);
		if (bufferManager != null) bufferManager.evictAll();
	}

	@Override
	public boolean databaseExists(String databaseName) {
		File db = new File("./mydatabase/" + databaseName);
		return db.exists() && db.isDirectory();
	}

	@Override
	public void createTable(String databaseName, String tableName, List<String> columnDefinitions) throws Exception {
		CreateTable.createTb(databaseName, tableName, columnDefinitions);
	}

	@Override
	public boolean tableExists(String databaseName, String tableName) {
		File tableDir = new File("./mydatabase/" + databaseName + "/" + tableName);
		return tableDir.exists() && tableDir.isDirectory();
	}

	@Override
	public List<String> listTables(String databaseName) throws IOException {
		List<String> tables = new ArrayList<>();
		File baseDir = new File("./mydatabase/" + databaseName);
		File[] files = baseDir.listFiles();
		if (files == null) return tables;
		for (File f : files) {
			if (f.isDirectory()) tables.add(f.getName());
		}
		return tables;
	}

	@Override
	public void insert(String databaseName, String tableName, List<String> columns, List<String> values) throws Exception {
		// 复用现有 InsertIntoTable API 习惯：两端列表各自首位为占位符
		List<String> colList = columns;
		List<String> valList = values;
		if (colList == null || colList.isEmpty() || !colList.get(0).equals("INSERT")) {
			colList = new ArrayList<>();
			colList.add("INSERT");
			if (columns != null) colList.addAll(columns);
		}
		if (valList == null || valList.isEmpty() || !valList.get(0).equals("INSERT")) {
			valList = new ArrayList<>();
			valList.add("INSERT");
			if (values != null) valList.addAll(values);
		}
		InsertIntoTable.insertIntoTable(databaseName, tableName, colList, valList);
	}

	@Override
	public void select(String databaseName, String tableName, List<String> columns) throws Exception {
		SelectDataFromTable.select(databaseName, tableName, columns, null);
	}

	@Override
	public void delete(String databaseName, String tableName, String whereCondition) throws Exception {
		// 简化实现：删除所有记录（XML引擎的DELETE功能需要WHERE条件）
		if (whereCondition == null || whereCondition.trim().isEmpty()) {
			System.out.println("警告：XML引擎的DELETE操作需要WHERE条件");
			return;
		}
		
		// 解析WHERE条件并调用现有的删除功能
		List<String> conditions = new ArrayList<>();
		conditions.add(whereCondition.trim());
		
		// 调用现有的DeleteDataFromTable功能
		DeleteDataFromTable.deleteFromTable(databaseName, tableName, conditions);
		System.out.println("DELETE操作：" + whereCondition);
	}
}


