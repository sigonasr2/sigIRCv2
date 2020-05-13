package sig.modules;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;

import sig.Module;
import sig.sigIRC;
import sig.modules.RabiRibi.Entity;
import sig.modules.RabiRibi.EntityLookupData;
import sig.modules.RabiRibi.MemoryOffset;
import sig.modules.RabiRibi.MemoryType;
import sig.modules.RabiRibi.Overlay;
import sig.modules.RabiRibi.RabiUtils;
import sig.modules.RabiRibi.SmoothObject;
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
	public HashMap<String,EntityLookupData> lookup_table = new HashMap<String,EntityLookupData>();
	int mapx = 0, mapy = 0;
	public String statustext = "";
	public int statustime = 0;
	public int moneyearned = 0;
	public int moneytime = 0;
	public int lastmoney = -1;
	
	public Overlay overlay;
	
	public SmoothObject en_counter = new SmoothObject(0,0,0,0,this){
		public void draw(Graphics g) {
			int playtime = readIntFromMemory(MemoryOffset.PLAYTIME);
			if (moneyearned>0 && moneytime>playtime) {
				setTarget(overlay.getScreenPosition(readFloatFromErinaData(MemoryOffset.ERINA_XPOS), readFloatFromErinaData(MemoryOffset.ERINA_YPOS)));
				//System.out.println(x+","+y);
				DrawUtils.drawCenteredOutlineText(g, sigIRC.panel.rabiRibiMoneyDisplayFont, (int)x, (int)y-96, 2, Color.ORANGE, Color.BLACK, "+"+moneyearned+"EN");
			}
		}
	};
	
	public RabiRibiModule(Rectangle2D bounds, String moduleName) {
		super(bounds, moduleName);
		//Initialize();
		Initialize();
		
		//System.out.println("Money value is: "+readIntFromMemory(MemoryOffset.MONEY));
	}
	
	public void SaveConfig() {
		super.SaveConfig();
		EntityLookupData.saveEntityLookupData(lookup_table);
	}

	private void Initialize() {
		
		RabiUtils.module = this;
		
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

		EntityLookupData.parent=this;
		EntityLookupData.loadEntityLookupData(lookup_table);
		
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleWithFixedDelay(()->{
			UpdateEntities();
			//System.out.println("Called Entity creation "+callcount+" times.");
		}, 1000, 1000, TimeUnit.MILLISECONDS);
	}
	
	public static void loadModule() {
		sigIRC.modules.add(new RabiRibiModule(
				new Rectangle(sigIRC.rabiribimodule_X,sigIRC.rabiribimodule_Y,sigIRC.rabiribimodule_width,sigIRC.rabiribimodule_height),
				"Rabi Ribi"
				));
		sigIRC.rabiribimodule_enabled=true;
		sigIRC.config.saveProperties();
	}
	public static void unloadModule() {
		for (int i=0;i<sigIRC.modules.size();i++) {
			if (sigIRC.modules.get(i) instanceof RabiRibiModule) {
				sigIRC.modules.remove(sigIRC.modules.get(i));
			}
		}
		sigIRC.rabiribimodule_enabled=false;
		sigIRC.config.saveProperties();
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
		
		if (lastmoney==-1) {
			lastmoney = readIntFromMemory(MemoryOffset.MONEY);
		} else 
		{
			int currentmoney = readIntFromMemory(MemoryOffset.MONEY);
			if (currentmoney>lastmoney) {
				if (moneyearned==0) {
					en_counter.setPosition(overlay.getScreenPosition(readFloatFromErinaData(MemoryOffset.ERINA_XPOS), readFloatFromErinaData(MemoryOffset.ERINA_YPOS)));
				}
				moneyearned+=currentmoney-lastmoney;
				moneytime = readIntFromMemory(MemoryOffset.PLAYTIME)+60;
			}
			lastmoney = currentmoney;
		}
		if (moneyearned>0 && moneytime<readIntFromMemory(MemoryOffset.PLAYTIME)) {
			moneyearned=0;
		}
		en_counter.run();
	}
	
	public HashMap<Integer,Entity> getEntities() {
		return entities;
	}
	
	private void updateEntities() {
		
		//System.out.println("Size is "+size);
		List<Integer> idsToRemove = new ArrayList<Integer>();
		try {
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
				//System.out.println("Removed entity "+i+". Entities remaining: "+entities.size());
			}
		} catch (ConcurrentModificationException e) {
			
		}
		
		//System.out.println("Starting address is 0x"+Long.toHexString(readIntFromMemory(MemoryOffset.ENTITY_ARRAY)));
		//System.out.println("Array Pointer starts at 0x"+Long.toHexString(arrayPtr));
		int currentx = (int)(readFloatFromErinaData(MemoryOffset.ERINA_XPOS)/1280);
		int currenty = (int)(readFloatFromErinaData(MemoryOffset.ERINA_YPOS)/720); 
		if (mapx!=(int)(readFloatFromErinaData(MemoryOffset.ERINA_XPOS)/1280) ||
				mapy!=(int)(readFloatFromErinaData(MemoryOffset.ERINA_YPOS)/720)) {
			//System.out.println("Update Entities.");
			mapx=currentx;
			mapy=currenty;
			ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
			scheduler.schedule(()->{
				UpdateEntities();
				//System.out.println("Called Entity creation "+callcount+" times.");
			}, 200, TimeUnit.MILLISECONDS);
		}
	}

	private void UpdateEntities() {
		int callcount=0;
		long arrayPtr  = readIntFromMemory(MemoryOffset.ENTITY_ARRAY);
		for (int i=0;i<MAX_ENTITIES_TO_UPDATE;i++) {
			if (!entities.containsKey(i)) {
				callcount++;
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
		if (!RabiUtils.isGamePaused()) {
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
			DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, "MONEY: ("+(readIntFromMemory(MemoryOffset.MONEY))+")");
			DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, "CAMERA ("+(readIntFromMemory(MemoryOffset.CAMERA_XPOS))+","+(readIntFromMemory(MemoryOffset.CAMERA_YPOS))+")");
			//DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, "SCREENPOS ("+overlay.getScreenPosition(readFloatFromErinaData(MemoryOffset.ERINA_XPOS), readFloatFromErinaData(MemoryOffset.ERINA_YPOS))+")");
			overlay.draw(g);
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
			try {
				for (Integer numb : entities.keySet()) {
					Entity ent = entities.get(numb);
					if (ent.getLastHitTime()>readIntFromMemory(MemoryOffset.PLAYTIME)-180) {
						for (String s : TextUtils.WrapText("Entity "+ent.getID()+": "+ent.getHealth()+"/"+ent.getMaxHealth()+" HP   "+((ent.getHealth()/(float)ent.getMaxHealth())*100)+"%".replaceAll(",", ", "), sigIRC.panel.programFont, position.getWidth()-20)) { 
							DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX()+20, position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, 
									s);
						}
					}
				}
			} catch (ConcurrentModificationException e) {
				
			}
			
			i+=24;
			
			int playtime = readIntFromMemory(MemoryOffset.PLAYTIME);
			
			if (statustext.length()>0 && statustime>playtime-300) {
				DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=48), 3, Color.GREEN, Color.LIGHT_GRAY, statustext);
			}
			
			en_counter.draw(g);
		}
	}
	
	public void setStatusMessage(String msg) {
		statustime = readIntFromMemory(MemoryOffset.PLAYTIME);
		statustext = msg;
	}

	public void keypressed(KeyEvent ev) {
		super.keypressed(ev);
		if (ev.getKeyCode()==KeyEvent.VK_HOME) {
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
		} /*else
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
