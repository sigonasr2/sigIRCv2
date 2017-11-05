package sig.modules.RabiRibi;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;

import sig.modules.RabiRibiModule;
import sig.utils.DebugUtils;

public class MemoryValue {
	RabiRibiModule parent;
	MemoryValueType type;
	long offset=-1;
	long pointer=-1;
	public boolean needsUpdating=false;
	int lastIntValue;
	float lastFloatValue;
	
	public MemoryValue(MemoryValueType type, long offset, RabiRibiModule parent) {
		this.type=type;
		this.offset=offset;
		this.parent=parent;
	}
	
	public MemoryValue(long offset, long pointer, RabiRibiModule parent) {
		this(MemoryValueType.POINTER,offset, parent);
		this.pointer=pointer;
	}
	
	public MemoryValue(MemoryValueType type, MemoryOffset offset, RabiRibiModule parent) {
		this(type,offset.getOffset(),parent);
	}
	
	public MemoryValue(MemoryOffset offset, MemoryOffset pointer, RabiRibiModule parent) {
		this(offset.getOffset(),pointer.getOffset(),parent);
	}
	
	public int getInt() {
		if (needsUpdating) {
			switch (type) {
			case ABSOLUTE:
					lastIntValue = readDirectIntFromMemoryLocation(offset);
				break;
			case LOCAL:
					lastIntValue = readIntFromMemory(offset);
				break;
			case POINTER:
					lastIntValue = readIntFromPointer(offset,pointer);
				break;
			}
			needsUpdating=false;
		}
		return lastIntValue;
	}
	
	public float getFloat() {
		if (needsUpdating) {
			switch (type) {
			case ABSOLUTE:
					lastFloatValue = readDirectFloatFromMemoryLocation(offset);
				break;
			case LOCAL:
					lastFloatValue = readFloatFromMemory(offset);
				break;
			case POINTER:
					lastFloatValue = readFloatFromPointer(offset,pointer);
				break;
			}
			needsUpdating=false;
		}
		return lastFloatValue;
	}
	
	int readIntFromErinaData(MemoryOffset val) {
		return readIntFromPointer(val,MemoryOffset.ENTITY_ARRAY);
	}
	
	float readFloatFromErinaData(MemoryOffset val) {
		return readFloatFromPointer(val,MemoryOffset.ENTITY_ARRAY);
	}
	
	int readIntFromMemory(long offset) {
		Memory mem = new Memory(4);
		Kernel32.INSTANCE.ReadProcessMemory(parent.rabiribiProcess, new Pointer(parent.rabiRibiMemOffset+offset), mem, 4, null);
		return mem.getInt(0);
	}
	
	float readFloatFromMemory(long offset) {
		Memory mem = new Memory(4);
		Kernel32.INSTANCE.ReadProcessMemory(parent.rabiribiProcess, new Pointer(parent.rabiRibiMemOffset+offset), mem, 4, null);
		return mem.getFloat(0);
	}
	
	float readFloatFromMemoryOffset(MemoryOffset val, long pointer) {
		Memory mem = new Memory(4);
		Kernel32.INSTANCE.ReadProcessMemory(parent.rabiribiProcess, new Pointer(pointer+val.getOffset()), mem, 4, null);
		return mem.getFloat(0);
	}
	
	int readIntFromMemoryOffset(MemoryOffset val, long pointer) {
		Memory mem = new Memory(4);
		Kernel32.INSTANCE.ReadProcessMemory(parent.rabiribiProcess, new Pointer(pointer+val.getOffset()), mem, 4, null);
		return mem.getInt(0);
	}
	
	float readDirectFloatFromMemoryLocation(long pointer) {
		Memory mem = new Memory(4);
		Kernel32.INSTANCE.ReadProcessMemory(parent.rabiribiProcess, new Pointer(pointer), mem, 4, null);
		return mem.getFloat(0);
	}
	
	int readDirectIntFromMemoryLocation(long pointer) {
		Memory mem = new Memory(4);
		Kernel32.INSTANCE.ReadProcessMemory(parent.rabiribiProcess, new Pointer(pointer), mem, 4, null);
		return mem.getInt(0);
	}
	
	int readIntFromPointer(MemoryOffset val, MemoryOffset pointer) {
		Memory mem = new Memory(4);
		Kernel32.INSTANCE.ReadProcessMemory(parent.rabiribiProcess, new Pointer(readIntFromMemory(pointer.getOffset())+val.getOffset()), mem, 4, null);
		return mem.getInt(0);
	}
	
	int readIntFromPointer(long val, long pointer) {
		Memory mem = new Memory(4);
		Kernel32.INSTANCE.ReadProcessMemory(parent.rabiribiProcess, new Pointer(readIntFromMemory(pointer)+val), mem, 4, null);
		return mem.getInt(0);
	}
	
	float readFloatFromPointer(MemoryOffset val, MemoryOffset pointer) {
		Memory mem = new Memory(4);
		Kernel32.INSTANCE.ReadProcessMemory(parent.rabiribiProcess, new Pointer(readIntFromMemory(pointer.getOffset())+val.getOffset()), mem, 4, null);
		return mem.getFloat(0);
	}
	
	float readFloatFromPointer(long val, long pointer) {
		Memory mem = new Memory(4);
		Kernel32.INSTANCE.ReadProcessMemory(parent.rabiribiProcess, new Pointer(readIntFromMemory(pointer)+val), mem, 4, null);
		return mem.getFloat(0);
	}
	
	int readIntFromMemory(MemoryOffset val) {
		return (int)readFromMemory(val,MemoryType.INTEGER);
	}
	
	float readFloatFromMemory(MemoryOffset val) {
		return (float)readFromMemory(val,MemoryType.FLOAT);
	}
	
	Object readFromMemory(MemoryOffset val, MemoryType type) {
		Memory mem = new Memory(type.getSize());
		Kernel32.INSTANCE.ReadProcessMemory(parent.rabiribiProcess, new Pointer(parent.rabiRibiMemOffset+val.getOffset()), mem, type.getSize(), null);
		switch (type) {
		case FLOAT:
			return mem.getFloat(0);
		case INTEGER:
			return mem.getInt(0);
		default:
			System.out.println("WARNING! Type "+type+" does not have a defined value.");
			return -1;
		}
	}
}
