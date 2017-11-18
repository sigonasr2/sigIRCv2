package sig.modules.RabiRibi;

public enum MemoryValueType {
	LOCAL, //Memory offset found relative to base address.
	ABSOLUTE, //Memory found absolute in the memory table.
	POINTER; //A value that points to a memory address.
}
