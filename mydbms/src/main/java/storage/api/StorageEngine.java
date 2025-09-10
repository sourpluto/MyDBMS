package storage.api;

import java.io.IOException;
import java.util.List;

/**
 * 存储引擎抽象：屏蔽底层XML/页式存储细节，供执行层统一调用
 */
public interface StorageEngine {

	// 数据库相关
	void createDatabase(String databaseName) throws IOException;
	void dropDatabase(String databaseName) throws IOException;
	boolean databaseExists(String databaseName);

	// 表相关
	void createTable(String databaseName, String tableName, List<String> columnDefinitions) throws Exception;
	boolean tableExists(String databaseName, String tableName);
	List<String> listTables(String databaseName) throws IOException;

	// 写入/更新/删除
	void insert(String databaseName, String tableName, List<String> columns, List<String> values) throws Exception;
	void delete(String databaseName, String tableName, String whereCondition) throws Exception;

	// 读取（简化：仅支持列投影与无条件/预留条件）
	void select(String databaseName, String tableName, List<String> columns) throws Exception;
}


