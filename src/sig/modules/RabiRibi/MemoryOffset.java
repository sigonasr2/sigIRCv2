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
	ENTITY_ARRAY(0x0096DA3C), //Erina Data Pointer.
	ERINA_HP(0x4D8),
	ERINA_MAXHP(0x4E8),
	ERINA_XPOS(0xC),
	ERINA_YPOS(0x10),
	ERINA_XSPEED(0x470), //Relative to Entity Array.
	ERINA_YSPEED(0x474), //Relative to Entity Array.
	MAPID(0xA600AC),
	CAMERA_XPOS(0x991AF4),
	CAMERA_YPOS(0xABD0A4),
	//ENTITY_SIZE(0x704),
	ENTITY_ID(0x4F4),
	ENTITY_HP(0x4D8),
	ENTITY_MAXHP(0x4E8),
	ENTITY_ISACTIVE(0x674),
	ENTITY_ANIMATION(0x678),
	ENTITY_XPOS(0xC),
	ENTITY_YPOS(0x10),
	ENTITY_COLOR(0x1C),
	TRANSITION_COUNTER(0xA7661C),
	
	GAME_DIFFICULTY(0xD64338),
	GAME_LOOP(0xD6D05C),
	;
	
	long offset;
	
	MemoryOffset(long offset) {
		this.offset=offset;
	}
	
	public long getOffset() {
		return offset;
	}
}
