package sig.modules.RabiRibi;

public enum MemoryOffset {
	MONEY(0xD654CC),
	PLAYTIME(0xD642D8), //In frames (Rabi-Ribi runs at 60FPS).
	UNKNOWN1(0xD65BDC), //???? Originally assumed to be "Health Ups".
	HEALTHUP_START(0xD6342C),
	HEALTHUP_END(0xD63528),
	ATTACKUP_START(0xD6352C),
	ATTACKUP_END(0xD63628),
	MANAUP_START(0xD6362C),
	MANAUP_END(0xD63728),
	REGENUP_START(0xD6372C),
	REGENUP_END(0xD63828),
	PACKUP_START(0xD6382C),
	PACKUP_END(0xD63928),
	;
	
	long offset;
	
	MemoryOffset(long offset) {
		this.offset=offset;
	}
	
	public long getOffset() {
		return offset;
	}
}
