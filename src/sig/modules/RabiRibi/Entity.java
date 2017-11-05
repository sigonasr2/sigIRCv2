package sig.modules.RabiRibi;

import java.awt.Point;
import java.lang.reflect.Field;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;

import sig.modules.RabiRibiModule;
import sig.modules.RabiRibi.SmoothObjects.EntityMarker;
import sig.utils.ReflectUtils;

public class Entity {
	int id = 0;
	int uuid = 0;
	int hp = 0;
	int maxhp = 0;
	boolean active = false;
	int animation = 0;
	float x = 0;
	float y = 0;
	RabiRibiModule parent = null;
	int lastTookDamage = 0;
	long pointer;
	int color = 0;
	public EntityMarker marker;
	
	public Entity(long memoryOffset, int uuid, RabiRibiModule parent) {
		this.parent=parent;
		pointer = memoryOffset;
		this.uuid = uuid;
		this.marker = new EntityMarker((int)x,(int)y,(int)x,(int)y,this,parent);
		UpdateValues();
		this.active = readIntFromMemoryOffset(MemoryOffset.ENTITY_ISACTIVE, pointer)==1 &&
				id!=0 && id!=1 && maxhp!=0;
		if (this.active) {
			parent.overlay.objects.add(this.marker);
		}
	}
	
	public boolean run() {
		this.active = readIntFromMemoryOffset(MemoryOffset.ENTITY_ISACTIVE, pointer)==1 &&
				id!=0 && id!=1 && maxhp!=0;
		
		if (!active) {
			return false;
		}
		
		if (readIntFromMemoryOffset(MemoryOffset.ENTITY_HP, pointer)<hp) {
			lastTookDamage = parent.readIntFromMemory(MemoryOffset.PLAYTIME);
		}
		
		UpdateValues();
		return true;
	}
	
	/*public Point getScreenPosition() {
		//The screen supports 20x11.5 tiles per map tile.
		//A map tile is 1280x720
		//float xtile = x
	}*/
	
	public int getHealth() {
		return hp;
	}

	public int getMaxHealth() {
		return maxhp;
	}
	
	public int getLastHitTime() {
		return lastTookDamage;
	}
	
	public int getID() {
		return id;
	}
	
	private void UpdateValues() {
		this.id = readIntFromMemoryOffset(MemoryOffset.ENTITY_ID, pointer);
		this.animation = readIntFromMemoryOffset(MemoryOffset.ENTITY_ANIMATION, pointer);
		if (this.animation!=-9999) {
			this.hp = readIntFromMemoryOffset(MemoryOffset.ENTITY_HP, pointer);
		} else {
			this.hp = 0;
		}
		this.maxhp = readIntFromMemoryOffset(MemoryOffset.ENTITY_MAXHP, pointer);
		this.x = readFloatFromMemoryOffset(MemoryOffset.ENTITY_XPOS, pointer);
		this.y = readFloatFromMemoryOffset(MemoryOffset.ENTITY_YPOS, pointer);
		this.color = readIntFromMemoryOffset(MemoryOffset.ENTITY_COLOR, pointer);
		this.marker.setTarget(parent.overlay.getScreenPosition(x,y));
	}
	
	public int getUniqueID() {
		return uuid;
	}
	
	public boolean isActive() {
		return active;
	}

	public float readFloatFromMemoryOffset(MemoryOffset val, long pointer) {
		Memory mem = new Memory(4);
		Kernel32.INSTANCE.ReadProcessMemory(parent.rabiribiProcess, new Pointer(pointer+val.getOffset()), mem, 4, null);
		return mem.getFloat(0);
	}
	
	public int readIntFromMemoryOffset(MemoryOffset val, long pointer) {
		Memory mem = new Memory(4);
		Kernel32.INSTANCE.ReadProcessMemory(parent.rabiribiProcess, new Pointer(pointer+val.getOffset()), mem, 4, null);
		return mem.getInt(0);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getName()+"(");
		boolean first=false;
		for (Field f : this.getClass().getDeclaredFields()) {
			//if (!ReflectUtils.isCloneable(f)) {
				if (!first) {
					try {
						sb.append(f.getName()+"="+f.get(this));
						first=true;
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				} else {
					try {
						sb.append(","+f.getName()+"="+f.get(this));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			//}
		}
		sb.append(")");
		return sb.toString();
	}
}
