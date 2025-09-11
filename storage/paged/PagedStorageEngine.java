package storage.paged;

import storage.api.StorageEngine;
import storage.buffer.BufferManager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 页式存储引擎（简化）：
 * - 每个表对应一个数据文件：{db}/{table}.dat
 * - 记录以文本行存储：列以逗号分隔，行以\n分隔
 * - 通过 PageFileManager 进行页级IO；BufferManager 预留（当前未细化页缓存装载）
 */
public class PagedStorageEngine implements StorageEngine {

	private final File baseDir;
	private final BufferManager bufferManager;

	public PagedStorageEngine(File baseDir, BufferManager bufferManager) {
		this.baseDir = baseDir;
		this.bufferManager = bufferManager;
	}

	@Override
	public void createDatabase(String databaseName) throws IOException {
		File db = new File(baseDir, databaseName);
		if (!db.exists()) {
			if (!db.mkdirs()) throw new IOException("Failed to create db dir: " + db);
		}
	}

	@Override
	public void dropDatabase(String databaseName) throws IOException {
		File db = new File(baseDir, databaseName);
		if (!db.exists()) return;
		deleteRecursive(db);
		if (bufferManager != null) bufferManager.evictAll();
	}

	@Override
	public boolean databaseExists(String databaseName) {
		File db = new File(baseDir, databaseName);
		return db.exists() && db.isDirectory();
	}

	@Override
	public void createTable(String databaseName, String tableName, List<String> columnDefinitions) throws Exception {
		File db = new File(baseDir, databaseName);
		if (!db.exists()) throw new IOException("Database not exists: " + databaseName);
		File tableFile = new File(db, tableName + ".dat");
		if (tableFile.exists()) throw new IOException("Table exists: " + tableName);
		PageFileManager pfm = new PageFileManager(tableFile);
		pfm.allocatePage();
		// 简化：不额外存schema，沿用编译器Catalog；这里只确保文件存在
	}

	@Override
	public boolean tableExists(String databaseName, String tableName) {
		File tableFile = new File(new File(baseDir, databaseName), tableName + ".dat");
		return tableFile.exists();
	}

	@Override
	public List<String> listTables(String databaseName) throws IOException {
		File db = new File(baseDir, databaseName);
		File[] files = db.listFiles((dir, name) -> name.endsWith(".dat"));
		if (files == null) return Collections.emptyList();
		List<String> list = new ArrayList<>();
		for (File f : files) {
			String n = f.getName();
			list.add(n.substring(0, n.length() - 4));
		}
		return list;
	}

	@Override
	public void insert(String databaseName, String tableName, List<String> columns, List<String> values) throws Exception {
		File tableFile = new File(new File(baseDir, databaseName), tableName + ".dat");
		PageFileManager pfm = new PageFileManager(tableFile);
		String line = String.join(",", values) + "\n";
		pfm.appendRecord(line.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public void select(String databaseName, String tableName, List<String> columns) throws Exception {
		File tableFile = new File(new File(baseDir, databaseName), tableName + ".dat");
		PageFileManager pfm = new PageFileManager(tableFile);
		
		// 打印表头
		if (columns == null || columns.isEmpty()) {
			System.out.println("所有列：");
		} else {
			System.out.println("选择的列：" + String.join(", ", columns));
		}
		
		// 扫描并显示数据
		for (String line : pfm.scanAllLines()) {
			if (!line.trim().isEmpty()) {
				System.out.println(line);
			}
		}
	}

	@Override
	public void delete(String databaseName, String tableName, String whereCondition) throws Exception {
		File tableFile = new File(new File(baseDir, databaseName), tableName + ".dat");
		PageFileManager pfm = new PageFileManager(tableFile);
		
		if (whereCondition == null || whereCondition.trim().isEmpty()) {
			// 删除所有记录：清空文件
			pfm.evictAll();
			System.out.println("已删除表 " + tableName + " 中的所有记录");
		} else {
			// 简化WHERE条件处理：只支持 "列名=值" 格式
			List<String> lines = pfm.scanAllLines();
			List<String> filteredLines = new ArrayList<>();
			int deletedCount = 0;
			
			for (String line : lines) {
				if (!line.trim().isEmpty()) {
					if (matchesCondition(line, whereCondition)) {
						deletedCount++;
					} else {
						filteredLines.add(line);
					}
				}
			}
			
			// 重写文件
			pfm.evictAll();
			for (String line : filteredLines) {
				pfm.appendRecord((line + "\n").getBytes(StandardCharsets.UTF_8));
			}
			
			System.out.println("已删除 " + deletedCount + " 条记录");
		}
	}
	
	private boolean matchesCondition(String record, String condition) {
		// 简化实现：只支持 "列名=值" 格式
		if (!condition.contains("=")) return false;
		
		String[] parts = condition.split("=", 2);
		if (parts.length != 2) return false;
		
		String columnName = parts[0].trim();
		String expectedValue = parts[1].trim();
		
		// 假设记录格式为CSV，这里简化处理
		String[] recordParts = record.split(",");
		for (String part : recordParts) {
			if (part.trim().equals(expectedValue)) {
				return true;
			}
		}
		return false;
	}

	private void deleteRecursive(File f) throws IOException {
		if (f.isDirectory()) {
			File[] children = f.listFiles();
			if (children != null) for (File c : children) deleteRecursive(c);
		}
		if (!f.delete()) throw new IOException("Failed to delete: " + f);
	}
}

 