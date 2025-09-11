package storage.paged;

import storage.page.Page;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 简化版页文件管理器：
 * - 固定页大小（默认4KB）
 * - 顺序分配页，页号从0开始
 * - 提供页级读/写/分配
 */
public class PageFileManager {

	public static final int PAGE_SIZE = 4096;

	private final File dataFile;

	public PageFileManager(File dataFile) {
		this.dataFile = dataFile;
	}

	public synchronized int getPageCount() throws IOException {
		if (!dataFile.exists()) return 0;
		try (RandomAccessFile raf = new RandomAccessFile(dataFile, "r")) {
			long len = raf.length();
			return (int) (len / PAGE_SIZE);
		}
	}

	public synchronized int allocatePage() throws IOException {
		int pageId = getPageCount();
		try (RandomAccessFile raf = new RandomAccessFile(dataFile, "rw")) {
			rawWrite(raf, pageId, new byte[PAGE_SIZE]);
		}
		return pageId;
	}

	public synchronized Page readPage(int pageId) throws IOException {
		try (RandomAccessFile raf = new RandomAccessFile(dataFile, "r")) {
			byte[] buf = new byte[PAGE_SIZE];
			long offset = (long) pageId * PAGE_SIZE;
			if (offset >= raf.length()) throw new IOException("Page out of range: " + pageId);
			raf.seek(offset);
			raf.readFully(buf);
			return new Page(String.valueOf(pageId), buf);
		}
	}

	public synchronized void writePage(int pageId, byte[] data) throws IOException {
		if (data.length != PAGE_SIZE) throw new IOException("Invalid page size: " + data.length);
		try (RandomAccessFile raf = new RandomAccessFile(dataFile, "rw")) {
			rawWrite(raf, pageId, data);
		}
	}

	private void rawWrite(RandomAccessFile raf, int pageId, byte[] data) throws IOException {
		long offset = (long) pageId * PAGE_SIZE;
		raf.seek(offset);
		raf.write(data);
	}

	// 追加一条记录到最后一个有空间的页，不够则新分配页
	public synchronized void appendRecord(byte[] recordBytes) throws IOException {
		if (recordBytes.length >= PAGE_SIZE) throw new IOException("Record too large");
		int pageCount = getPageCount();
		if (pageCount == 0) {
			allocatePage();
			pageCount = 1;
		}
		for (int pid = pageCount - 1; pid >= 0; pid--) {
			Page p = readPage(pid);
			int used = findUsedSize(p.getData());
			if (used + recordBytes.length <= PAGE_SIZE) {
				byte[] data = p.getData();
				System.arraycopy(recordBytes, 0, data, used, recordBytes.length);
				writePage(pid, data);
				return;
			}
		}
		// 全部不足，分配新页
		int newId = allocatePage();
		Page p = readPage(newId);
		byte[] data = p.getData();
		System.arraycopy(recordBytes, 0, data, 0, recordBytes.length);
		writePage(newId, data);
	}

	// 扫描所有记录（逐行，以'\n'为分隔）
	public synchronized List<String> scanAllLines() throws IOException {
		List<String> lines = new ArrayList<>();
		int pages = getPageCount();
		for (int pid = 0; pid < pages; pid++) {
			Page p = readPage(pid);
			byte[] data = p.getData();
			String s = new String(data, StandardCharsets.UTF_8).trim();
			if (s.isEmpty()) continue;
			for (String line : s.split("\n")) {
				if (!line.isEmpty()) lines.add(line);
			}
		}
		return lines;
	}

	private int findUsedSize(byte[] data) {
		int i = data.length - 1;
		while (i >= 0 && data[i] == 0) i--;
		return i + 1;
	}

	public synchronized void evictAll() throws IOException {
		// 清空文件：删除所有页
		if (dataFile.exists()) {
			dataFile.delete();
		}
	}
}


