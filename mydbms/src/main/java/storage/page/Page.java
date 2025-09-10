package storage.page;

/**
 * 逻辑页（简化模型）：封装页号与数据字节
 */
public class Page {
	private final String pageId; // 可由 db/table/fileName#offset 构成
	private byte[] data;

	public Page(String pageId, byte[] data) {
		this.pageId = pageId;
		this.data = data;
	}

	public String getPageId() {
		return pageId;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}


