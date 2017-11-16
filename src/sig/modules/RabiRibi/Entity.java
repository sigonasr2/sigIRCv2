package sig.modules.RabiRibi;

import java.awt.Point;
import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
	public static int taskDone=1; //1 = Free, 0 = In Progress, -1 = Stop all current processes
	
	boolean killed=false;
	
	public Entity(long memoryOffset, int uuid, RabiRibiModule parent) {
		this.parent=parent;
		pointer = memoryOffset;
		this.uuid = uuid;
		this.marker = new EntityMarker((int)x,(int)y,(int)x,(int)y,this,parent);
		UpdateValues();
		this.active = readIntFromMemoryOffset(MemoryOffset.ENTITY_ISACTIVE, pointer)==1 &&
				/*id!=0 && id!=1 &&*/ maxhp!=0;
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
			if (active && !killed) {
				killed=true;
				if (taskDone==1 && !RabiUtils.isGamePaused()) {
					RetrieveMoneyValueForLookupData(id,color);
				}
				EntityLookupData data = EntityLookupData.getEntityLookupData(id, color);
				data.increaseKills(1);
			}
			this.hp = 0;
		}
		this.maxhp = readIntFromMemoryOffset(MemoryOffset.ENTITY_MAXHP, pointer);
		this.x = readFloatFromMemoryOffset(MemoryOffset.ENTITY_XPOS, pointer);
		this.y = readFloatFromMemoryOffset(MemoryOffset.ENTITY_YPOS, pointer);
		this.color = readIntFromMemoryOffset(MemoryOffset.ENTITY_COLOR, pointer);
		this.marker.setTarget(parent.overlay.getScreenPosition(x,y));
	}
	
	private void RetrieveMoneyValueForLookupData(int id, int color) {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleWithFixedDelay(new MoneyUpdateTask(scheduler,id,color,parent),500,500,TimeUnit.MILLISECONDS);
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
	
	class MoneyUpdateTask implements Runnable{
		ScheduledExecutorService scheduler;
		RabiRibiModule parent;
		int prev_money_val = -1;
		int starting_money_val = 0;
		int id, col;
		int checkcount=0;
		
		MoneyUpdateTask(ScheduledExecutorService scheduler, int id, int col, RabiRibiModule parent) {
			this.scheduler=scheduler;
			this.parent=parent;
			UpdateMoney();
			starting_money_val = prev_money_val;
			this.id=id;
			this.col=col;
			//System.out.println("Starting Money Value: "+starting_money_val);
		}

		private void UpdateMoney() {
			prev_money_val = parent.readIntFromMemory(MemoryOffset.MONEY);
		}

		@Override
		public void run() {
			if (Entity.taskDone==-1) {
				System.out.println("Quitting early, killed an extra enemy.");
				Entity.taskDone=1;
				scheduler.shutdownNow();
				return;
			}
			if (RabiUtils.isGamePaused()) {
				return;
			}
			int current_money = parent.readIntFromMemory(MemoryOffset.MONEY);
			if (current_money==prev_money_val && (current_money!=starting_money_val || checkcount>5)) {
				//System.out.println("Money Value matches, adding "+(current_money-starting_money_val)+" to "+lookup_data+" with ID "+id+","+color);
				String hashcode = EntityLookupData.getHashCode(id, color);
				if (parent.lookup_table.containsKey(hashcode)) {
					EntityLookupData lookup_data = parent.lookup_table.get(hashcode);
					lookup_data.setMoney(lookup_data.getMoney()+(current_money-starting_money_val));
				} else {
					EntityLookupData lookup_data = new EntityLookupData(current_money-starting_money_val);
					parent.lookup_table.put(hashcode,lookup_data);
					parent.setStatusMessage("Adding "+(current_money-starting_money_val)+" to hash ID "+id+","+color);
				}
				Entity.taskDone=1;
				scheduler.shutdownNow();
			} else {
				checkcount++;
				UpdateMoney();
			}
		}
		
	}
}
