package sig.modules;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;

import sig.FileManager;
import sig.Module;
import sig.sigIRC;
import sig.modules.Controller.ClickableButton;
import sig.modules.RabiRace.ColorCycler;
import sig.modules.RabiRace.CreateButton;
import sig.modules.RabiRace.JoinButton;
import sig.modules.RabiRace.MemoryData;
import sig.modules.RabiRace.Profile;
import sig.modules.RabiRace.SessionListData;
import sig.modules.RabiRace.SessionListWindow;
import sig.modules.RabiRibi.MemoryOffset;
import sig.modules.RabiRibi.MemoryType;
import sig.modules.utils.PsapiTools;
import sig.utils.DrawUtils;
import sig.utils.FileUtils;

public class RabiRaceModule extends Module{
	final static String ITEMS_DIRECTORY = sigIRC.BASEDIR+"sigIRC/rabi-ribi/items/";
	final int PROCESS_PERMISSIONS = WinNT.PROCESS_QUERY_INFORMATION | WinNT.PROCESS_VM_READ;
	boolean foundRabiRibi = false;
	int rabiRibiPID = -1;
	long rabiRibiMemOffset = 0;
	public HANDLE rabiribiProcess = null;
	public static HashMap<String,Image> image_map = new HashMap<String,Image>();
	public static ColorCycler rainbowcycler = new ColorCycler(new Color(255,0,0,96),8);
	Profile myProfile = new Profile(this);
	public static RabiRaceModule module;
	public static SessionListWindow window;
	
	public SessionListData session_listing = new SessionListData();
	
	ClickableButton join_button,create_button;
	
	public static List<MemoryData> key_items_list = new ArrayList<MemoryData>();  
	public static List<MemoryData> badges_list = new ArrayList<MemoryData>(); 
	
	public RabiRaceModule(Rectangle2D bounds, String moduleName) {
		super(bounds, moduleName);
		//Initialize();
		Initialize();
		module = this;
		window = new SessionListWindow();
		window.setVisible(false);
		//System.out.println("Money value is: "+readIntFromMemory(MemoryOffset.MONEY));
	}

	private void Initialize() {
		CheckRabiRibiClient();

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleWithFixedDelay(()->{
			CheckRabiRibiClient();
			if (foundRabiRibi) {
				myProfile.uploadProfile();
				getSessionList();
				//trimeadProfile.downloadProfile();
			}
		}, 5000, 5000, TimeUnit.MILLISECONDS);
		scheduler.scheduleWithFixedDelay(()->{
			if (foundRabiRibi) {
				UpdateMyProfile();
			}
		}, 250, 250, TimeUnit.MILLISECONDS);
		
		File dir = new File(ITEMS_DIRECTORY);
		
		for (MemoryData data : MemoryData.values()) {
			//Attempt to fetch from server.
			new FileManager("sigIRC/rabi-ribi/items/"+data.img_path).verifyAndFetchFileFromServer();
		}
		new FileManager("sigIRC/rabi-ribi/items/easter_egg.png").verifyAndFetchFileFromServer();
		new FileManager("sigIRC/rabi-ribi/items/health_up.png").verifyAndFetchFileFromServer();
		new FileManager("sigIRC/rabi-ribi/items/mana_up.png").verifyAndFetchFileFromServer();
		new FileManager("sigIRC/rabi-ribi/items/regen_up.png").verifyAndFetchFileFromServer();
		new FileManager("sigIRC/rabi-ribi/items/pack_up.png").verifyAndFetchFileFromServer();
		new FileManager("sigIRC/rabi-ribi/items/attack_up.png").verifyAndFetchFileFromServer();
		
		String[] images = dir.list();
		List<String> filtered_images = new ArrayList<String>();
		for (String file : images) {
			File f = new File(ITEMS_DIRECTORY+file);
			if (!f.isDirectory()) {
				filtered_images.add(file);
			}
		}
		images = filtered_images.toArray(new String[filtered_images.size()]);
		for (String image : images) {
			try {
				//System.out.println("Loaded "+image);
				image_map.put(image, ImageIO.read(new File(ITEMS_DIRECTORY+image)));
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		
		for (MemoryData md : MemoryData.values()) {
			if (md.key_item) {
				key_items_list.add(md);
			} else {
				badges_list.add(md);
			}
		}
		
		//trimeadProfile.username = "trimead";
		
		join_button = new JoinButton(new Rectangle(2,(int)(position.getHeight()-18),120,18),"Join Session (0)",this);
		create_button = new CreateButton(new Rectangle(122,(int)(position.getHeight()-18),120,18),"Create Session",this);
	}
	
	private void getSessionList() {
		File file = new File(sigIRC.BASEDIR+"sessions");
		try {
			org.apache.commons.io.FileUtils.copyURLToFile(new URL("http://45.33.13.215/rabirace/send.php?key=getsessions"),file);
			String[] data = FileUtils.readFromFile(sigIRC.BASEDIR+"sessions");
			//System.out.println("Data is "+Arrays.toString(data));
			session_listing.UpdateData(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		join_button.setButtonLabel("Join Session ("+session_listing.getSessions().size()+")");
		window.UpdateSessionList();
	}

	public void mousePressed(MouseEvent ev) {
		if (join_button.mouseInsideBounds(ev)) {
			join_button.onClickEvent(ev);
		}
		if (create_button.mouseInsideBounds(ev)) {
			create_button.onClickEvent(ev);
		}
	}

	private void CheckRabiRibiClient() {
		List<Integer> pids;
		try {
			pids = PsapiTools.getInstance().enumProcesses();	
			boolean found=false;	
			for (Integer pid : pids) {
				HANDLE process = Kernel32.INSTANCE.OpenProcess(PROCESS_PERMISSIONS, true, pid);
		        List<sig.modules.utils.Module> hModules;
				try {
					hModules = PsapiTools.getInstance().EnumProcessModules(process);
					for(sig.modules.utils.Module m: hModules){
						//System.out.println(m.getFileName()+":"+m.getEntryPoint());
						if (m.getFileName().contains("rabiribi")) {
							found=true;
							if (!foundRabiRibi) {
								rabiRibiMemOffset = Pointer.nativeValue(m.getLpBaseOfDll().getPointer());
								System.out.println("Found an instance of Rabi-Ribi at 0x"+Long.toHexString(rabiRibiMemOffset)+" | File:"+m.getFileName()+","+m.getBaseName());
								rabiRibiPID=pid;
								foundRabiRibi=true;
								rabiribiProcess=process;
								break;
							}
							break;
						}
			        }
					if (found) {
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (process!=null) {
					Kernel32.INSTANCE.CloseHandle(process);
				}
			}
			if (!found && foundRabiRibi) {
				foundRabiRibi=false;
				System.out.println("Rabi-Ribi process lost.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		if (foundRabiRibi) {
			rainbowcycler.run();
			if (window!=null) {
				window.run();
			}
		}
	}
	
	private void UpdateMyProfile() {
		if (foundRabiRibi) {
			//System.out.println("Called.");
			int warp_counter = readIntFromMemory(MemoryOffset.WARP_TRANSITION_COUNTER);
			if (warp_counter==203 || warp_counter==141) {
				myProfile.rainbowEggCount = readIntFromMemory(MemoryOffset.RAINBOW_EGG_COUNT);
				myProfile.attackUps = readItemCountFromMemory(MemoryOffset.ATTACKUP_START,MemoryOffset.ATTACKUP_END);
				myProfile.healthUps = readItemCountFromMemory(MemoryOffset.HEALTHUP_START,MemoryOffset.HEALTHUP_END);
				myProfile.manaUps = readItemCountFromMemory(MemoryOffset.MANAUP_START,MemoryOffset.MANAUP_END);
				myProfile.regenUps = readItemCountFromMemory(MemoryOffset.REGENUP_START,MemoryOffset.REGENUP_END);
				myProfile.packUps = readItemCountFromMemory(MemoryOffset.PACKUP_START,MemoryOffset.PACKUP_END);
				myProfile.isPaused = readIntFromMemory(MemoryOffset.WARP_TRANSITION_COUNTER)==141;
				myProfile.itempct = readFloatFromMemory(MemoryOffset.ITEM_PERCENT);
				myProfile.mappct = readFloatFromMemory(MemoryOffset.MAP_PERCENT);
				myProfile.playtime = readIntFromMemory(MemoryOffset.PLAYTIME);
				myProfile.updateClientValues();
			}
		}
	}
	
	public void ApplyConfigWindowProperties() {
		sigIRC.rabiracemodule_X=(int)position.getX();
		sigIRC.rabiracemodule_Y=(int)position.getY();
		sigIRC.config.setInteger("RABIRACE_module_X", sigIRC.rabiracemodule_X);
		sigIRC.config.setInteger("RABIRACE_module_Y", sigIRC.rabiracemodule_Y);
	}

	/*public int readIntFromErinaData(MemoryOffset val) {
		return readIntFromPointer(val,MemoryOffset.ENTITY_ARRAY);
	}
	
	public float readFloatFromErinaData(MemoryOffset val) {
		return readFloatFromPointer(val,MemoryOffset.ENTITY_ARRAY);
	}*/
	
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
	
	public void draw(Graphics g) {
		super.draw(g);
		
		if (!foundRabiRibi) {
			DrawUtils.drawTextFont(g, sigIRC.panel.userFont, position.getX(), position.getY()+26, Color.BLACK, "Rabi-Ribi not found! Please start it.");
		} else {
			//myProfile.draw(g);
			Image panel = myProfile.getStatPanel((int)position.getWidth());
			g.drawImage(panel, (int)position.getX(), (int)position.getY(), sigIRC.panel);
			g.drawImage(myProfile.getStatText((int)position.getWidth()), (int)position.getX(), (int)position.getY(), sigIRC.panel);
			
			//Profile.DrawMultiPanel(g, (int)(position.getX()), (int)(position.getY())+panel.getHeight(sigIRC.panel), (int)position.getWidth(), testing);
			
			join_button.draw(g);
			create_button.draw(g);
		}
	}
}
