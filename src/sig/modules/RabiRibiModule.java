package sig.modules;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;

import sig.Module;
import sig.sigIRC;
import sig.modules.RabiRibi.Entity;
import sig.modules.RabiRibi.MemoryOffset;
import sig.modules.RabiRibi.MemoryType;
import sig.modules.RabiRibi.Overlay;
import sig.modules.utils.PsapiTools;
import sig.utils.DrawUtils;
import sig.utils.FileUtils;
import sig.utils.TextUtils;

public class RabiRibiModule extends Module{
	final int PROCESS_PERMISSIONS = WinNT.PROCESS_QUERY_INFORMATION | WinNT.PROCESS_VM_READ;
	boolean foundRabiRibi = false;
	int rabiRibiPID = -1;
	long rabiRibiMemOffset = 0;
	public HANDLE rabiribiProcess = null;
	HashMap<Integer,Entity> entities = new HashMap<Integer,Entity>();
	final static int MAX_ENTITIES_TO_UPDATE = 500;
	final static int ENTITY_ARRAY_ELEMENT_SIZE = 0x704;
	
	public Overlay overlay;
	
	public RabiRibiModule(Rectangle2D bounds, String moduleName) {
		super(bounds, moduleName);
		//Initialize();
		Initialize();
		
		//System.out.println("Money value is: "+readIntFromMemory(MemoryOffset.MONEY));
	}

	private void Initialize() {
		List<Integer> pids;
		try {
			pids = PsapiTools.getInstance().enumProcesses();		
			for (Integer pid : pids) {
				HANDLE process = Kernel32.INSTANCE.OpenProcess(PROCESS_PERMISSIONS, true, pid);
		        List<sig.modules.utils.Module> hModules;
				try {
					hModules = PsapiTools.getInstance().EnumProcessModules(process);
					for(sig.modules.utils.Module m: hModules){
						//System.out.println(m.getFileName()+":"+m.getEntryPoint());
						if (m.getFileName().contains("rabiribi")) {
							rabiRibiMemOffset = Pointer.nativeValue(m.getLpBaseOfDll().getPointer());
							System.out.println("Found an instance of Rabi-Ribi at 0x"+Long.toHexString(rabiRibiMemOffset));
							rabiRibiPID=pid;
							foundRabiRibi=true;
							rabiribiProcess=process;
							break;
						}
			        }
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (foundRabiRibi) {
					break;
				}
				if (process!=null) {
					Kernel32.INSTANCE.CloseHandle(process);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.overlay = new Overlay(this);
	}
	
	public void ApplyConfigWindowProperties() {
		sigIRC.rabiribimodule_X=(int)position.getX();
		sigIRC.rabiribimodule_Y=(int)position.getY();
		sigIRC.config.setInteger("RABIRIBI_module_X", sigIRC.rabiribimodule_X);
		sigIRC.config.setInteger("RABIRIBI_module_Y", sigIRC.rabiribimodule_Y);
	}
	
	public void run() {
		super.run();
		updateEntities();
		overlay.run();
	}
	
	public HashMap<Integer,Entity> getEntities() {
		return entities;
	}
	
	private void updateEntities() {
		
		//System.out.println("Size is "+size);
		List<Integer> idsToRemove = new ArrayList<Integer>();
		for (Integer i : entities.keySet()) {
			Entity ent = entities.get(i);
			if (!ent.run()) {
				idsToRemove.add(i);
			}
		}
		for (Integer i : idsToRemove) {
			if (!overlay.objects.remove(entities.get(i).marker)) {
				System.out.println("WARNING! Could not remove overlay EntityMarker. Possible memory leak occurring!");
			}
			entities.remove(i);
		}
		
		//System.out.println("Starting address is 0x"+Long.toHexString(readIntFromMemory(MemoryOffset.ENTITY_ARRAY)));
		long arrayPtr  = readIntFromMemory(MemoryOffset.ENTITY_ARRAY);
		//System.out.println("Array Pointer starts at 0x"+Long.toHexString(arrayPtr));
		for (int i=0;i<MAX_ENTITIES_TO_UPDATE;i++) {
			if (!entities.containsKey(i)) {
				Entity ent = new Entity(arrayPtr,i,this);
				if (ent.isActive()) {
					//System.out.println("Found entity at index "+i);
					//entities.add(ent);
					entities.put(i, ent);
				}
			}
			arrayPtr += ENTITY_ARRAY_ELEMENT_SIZE;
		}
	}

	public void draw(Graphics g) {
		super.draw(g);
		if (readIntFromMemory(MemoryOffset.TRANSITION_COUNTER)<300) {
			int i=32;
			/*DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, "Money: "+readIntFromMemory(MemoryOffset.MONEY));
			DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, "H-Ups: "+readItemCountFromMemory(MemoryOffset.HEALTHUP_START,MemoryOffset.HEALTHUP_END));
			DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, "A-Ups: "+readItemCountFromMemory(MemoryOffset.ATTACKUP_START,MemoryOffset.ATTACKUP_END));
			DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, "M-Ups: "+readItemCountFromMemory(MemoryOffset.MANAUP_START,MemoryOffset.MANAUP_END));
			DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, "R-Ups: "+readItemCountFromMemory(MemoryOffset.REGENUP_START,MemoryOffset.REGENUP_END));
			DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, "P-Ups: "+readItemCountFromMemory(MemoryOffset.PACKUP_START,MemoryOffset.PACKUP_END));
			DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, "HP: "+readIntFromErinaData(MemoryOffset.ERINA_HP)+"/"+readIntFromErinaData(MemoryOffset.ERINA_MAXHP));
			DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, "POS ("+(int)readFloatFromErinaData(MemoryOffset.ERINA_XPOS)/1280+","+(int)readFloatFromErinaData(MemoryOffset.ERINA_YPOS)/720+")");
			DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, "MAP: "+readIntFromMemory(MemoryOffset.MAPID));*/
			DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, "POS ("+(readFloatFromErinaData(MemoryOffset.ERINA_XPOS))+","+(readFloatFromErinaData(MemoryOffset.ERINA_YPOS))+")");
			DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, "POS ("+(readFloatFromErinaData(MemoryOffset.ERINA_XPOS)/1280)+","+(readFloatFromErinaData(MemoryOffset.ERINA_YPOS)/720)+")");
			DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, "CAMERA ("+(readIntFromMemory(MemoryOffset.CAMERA_XPOS))+","+(readIntFromMemory(MemoryOffset.CAMERA_YPOS))+")");
			//DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, "SCREENPOS ("+overlay.getScreenPosition(readFloatFromErinaData(MemoryOffset.ERINA_XPOS), readFloatFromErinaData(MemoryOffset.ERINA_YPOS))+")");
			overlay.draw(g);
			if (Math.abs(readFloatFromErinaData(MemoryOffset.ERINA_XSPEED))>0.5f) {
				g.setColor(Color.RED);
			}
			DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=24), 3, g.getColor(), Color.WHITE, "XSPD "+readFloatFromErinaData(MemoryOffset.ERINA_XSPEED));
			g.setColor(Color.BLACK);
			/*DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, 
					"Sunny Beam: "+Arrays.toString(
					new int[]{
							readIntFromMemory(0xD63304),
							readIntFromMemory(0xD63D0C),
					}));
					*/
			/*
			for (String s : TextUtils.WrapText(("Entities: "+entities).replaceAll(",", ", "), sigIRC.panel.programFont, position.getWidth()-20)) { 
				DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX()+20, position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, 
					s);
			}*/
			for (Integer numb : entities.keySet()) {
				Entity ent = entities.get(numb);
				if (ent.getLastHitTime()>readIntFromMemory(MemoryOffset.PLAYTIME)-180) {
					for (String s : TextUtils.WrapText("Entity "+ent.getID()+": "+ent.getHealth()+"/"+ent.getMaxHealth()+" HP   "+((ent.getHealth()/(float)ent.getMaxHealth())*100)+"%".replaceAll(",", ", "), sigIRC.panel.programFont, position.getWidth()-20)) { 
						DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX()+20, position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, 
								s);
					}
				}
			}
		}
	}

	public void keypressed(KeyEvent ev) {
		super.keypressed(ev);
		/*if (ev.getKeyCode()==KeyEvent.VK_HOME) {
			String memFile = sigIRC.BASEDIR+"memoryDump.txt";
			FileUtils.logToFile("Memory Dump of All Loaded Entities:", memFile);
			for (Integer numb : entities.keySet()) {
				Entity ent = entities.get(numb);
				FileUtils.logToFile(ent.toString(), memFile);
				for (int i=0;i<ENTITY_ARRAY_ELEMENT_SIZE/4;i++) {
					Long ptrArray = (long)(readIntFromMemory(MemoryOffset.ENTITY_ARRAY)+(numb*ENTITY_ARRAY_ELEMENT_SIZE)+i*4);
					FileUtils.logToFile("  +"+Integer.toHexString(i*4)+": "+readDirectIntFromMemoryLocation(ptrArray)+" / "+readDirectFloatFromMemoryLocation(ptrArray)+"f", memFile);
				}
			}
		} else
		if (ev.getKeyCode()==KeyEvent.VK_END) {
			String memFile = sigIRC.BASEDIR+"memoryDump.txt";
			FileUtils.logToFile("Memory Dump of All Erina Values:", memFile);
			for (int i=0;i<ENTITY_ARRAY_ELEMENT_SIZE/4;i++) {
				Long ptrArray = (long)(MemoryOffset.CAMERA_XPOS.getOffset()+i*4);
				FileUtils.logToFile("  +"+Integer.toHexString(i*4)+": "+readIntFromMemory(ptrArray)+" / "+readFloatFromMemory(ptrArray)+"f", memFile);
			}
		}*/
	}
	
	public int readIntFromErinaData(MemoryOffset val) {
		return readIntFromPointer(val,MemoryOffset.ENTITY_ARRAY);
	}
	
	public float readFloatFromErinaData(MemoryOffset val) {
		return readFloatFromPointer(val,MemoryOffset.ENTITY_ARRAY);
	}
	
	public int readIntFromMemory(long offset) {
		Memory mem = new Memory(4);
		Kernel32.INSTANCE.ReadProcessMemory(rabiribiProcess, new Pointer(rabiRibiMemOffset+offset), mem, 4, null);
		return mem.getInt(0);
	}
	
	public float readFloatFromMemory(long offset) {
		Memory mem = new Memory(4);
		Kernel32.INSTANCE.ReadProcessMemory(rabiribiProcess, new Pointer(rabiRibiMemOffset+offset), mem, 4, null);
		return mem.getFloat(0);
	}
	
	public float readFloatFromMemoryOffset(MemoryOffset val, long pointer) {
		Memory mem = new Memory(4);
		Kernel32.INSTANCE.ReadProcessMemory(rabiribiProcess, new Pointer(pointer+val.getOffset()), mem, 4, null);
		return mem.getFloat(0);
	}
	
	public int readIntFromMemoryOffset(MemoryOffset val, long pointer) {
		Memory mem = new Memory(4);
		Kernel32.INSTANCE.ReadProcessMemory(rabiribiProcess, new Pointer(pointer+val.getOffset()), mem, 4, null);
		return mem.getInt(0);
	}
	
	public float readDirectFloatFromMemoryLocation(long pointer) {
		Memory mem = new Memory(4);
		Kernel32.INSTANCE.ReadProcessMemory(rabiribiProcess, new Pointer(pointer), mem, 4, null);
		return mem.getFloat(0);
	}
	
	public int readDirectIntFromMemoryLocation(long pointer) {
		Memory mem = new Memory(4);
		Kernel32.INSTANCE.ReadProcessMemory(rabiribiProcess, new Pointer(pointer), mem, 4, null);
		return mem.getInt(0);
	}
	
	public int readIntFromPointer(MemoryOffset val, MemoryOffset pointer) {
		Memory mem = new Memory(4);
		Kernel32.INSTANCE.ReadProcessMemory(rabiribiProcess, new Pointer(readIntFromMemory(pointer.getOffset())+val.getOffset()), mem, 4, null);
		return mem.getInt(0);
	}
	
	public float readFloatFromPointer(MemoryOffset val, MemoryOffset pointer) {
		Memory mem = new Memory(4);
		Kernel32.INSTANCE.ReadProcessMemory(rabiribiProcess, new Pointer(readIntFromMemory(pointer.getOffset())+val.getOffset()), mem, 4, null);
		return mem.getFloat(0);
	}
	
	public int readIntFromMemory(MemoryOffset val) {
		return (int)readFromMemory(val,MemoryType.INTEGER);
	}
	
	public float readFloatFromMemory(MemoryOffset val) {
		return (float)readFromMemory(val,MemoryType.FLOAT);
	}
	
	Object readFromMemory(MemoryOffset val, MemoryType type) {
		Memory mem = new Memory(type.getSize());
		Kernel32.INSTANCE.ReadProcessMemory(rabiribiProcess, new Pointer(rabiRibiMemOffset+val.getOffset()), mem, type.getSize(), null);
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
	
	int readItemCountFromMemory(MemoryOffset start_range,
			MemoryOffset end_range) {
		int count=0;
		for (long i=start_range.getOffset();i<=end_range.getOffset();i++) {
			if (readIntFromMemory(i)==1) {
				count++;
			}
		}
		return count;
	}
}
