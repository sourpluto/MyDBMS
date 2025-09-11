package storage.buffer;

import storage.page.Page;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 缓冲池管理器：LRU策略，支持命中统计、替换日志、页刷新
 */
public class BufferManager {

	private final int capacity;
	private final LinkedHashMap<String, Page> lru;
	private long hits = 0;
	private long misses = 0;
	private long evictions = 0;
	private boolean enableLogging = true;

	public BufferManager(int capacity) {
		this.capacity = Math.max(8, capacity);
		this.lru = new LinkedHashMap<>(16, 0.75f, true) {
			@Override
			protected boolean removeEldestEntry(Map.Entry<String, Page> eldest) {
				boolean evict = size() > BufferManager.this.capacity;
				if (evict && enableLogging) {
					evictions++;
					System.out.println("[Buffer] EVICT page=" + eldest.getKey());
				}
				return evict;
			}
		};
	}

	public synchronized Page get(String pageId) {
		Page page = lru.get(pageId);
		if (page == null) {
			misses++;
			if (enableLogging) {
				System.out.println("[Buffer] MISS page=" + pageId);
			}
		} else {
			hits++;
			if (enableLogging) {
				System.out.println("[Buffer] HIT page=" + pageId);
			}
		}
		return page;
	}

	public synchronized void put(Page page) {
		lru.put(page.getPageId(), page);
		if (enableLogging) {
			System.out.println("[Buffer] PUT page=" + page.getPageId());
		}
	}

	public synchronized void evictAll() {
		int count = lru.size();
		lru.clear();
		if (enableLogging) {
			System.out.println("[Buffer] EVICT_ALL " + count + " pages");
		}
	}

	public synchronized void flushPage(String pageId) {
		Page page = lru.get(pageId);
		if (page != null) {
			// 在实际实现中，这里应该将页写回磁盘
			if (enableLogging) {
				System.out.println("[Buffer] FLUSH page=" + pageId);
			}
		}
	}

	public synchronized void flushAll() {
		for (String pageId : lru.keySet()) {
			flushPage(pageId);
		}
		if (enableLogging) {
			System.out.println("[Buffer] FLUSH_ALL " + lru.size() + " pages");
		}
	}

	public synchronized long getHits() {
		return hits;
	}

	public synchronized long getMisses() {
		return misses;
	}

	public synchronized long getEvictions() {
		return evictions;
	}

	public synchronized double getHitRate() {
		long total = hits + misses;
		return total == 0 ? 0.0 : (double) hits / total;
	}

	public synchronized void setLogging(boolean enable) {
		this.enableLogging = enable;
	}

	public synchronized void printStats() {
		System.out.println("[Buffer] Stats - Hits: " + hits + ", Misses: " + misses + 
						   ", Evictions: " + evictions + ", Hit Rate: " + 
						   String.format("%.2f%%", getHitRate() * 100));
	}
}


