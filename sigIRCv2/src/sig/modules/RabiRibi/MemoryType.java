package sig.modules.RabiRibi;

public enum MemoryType {
	INTEGER(4),
	FLOAT(4),
	;
	
	int bytes;
	
	MemoryType(int bytes) {
		this.bytes=bytes;
	}
	
	public int getSize() {
		return bytes;
	}
}
