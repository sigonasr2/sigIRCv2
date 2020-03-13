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
import java.net.MalformedURLException;
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
import sig.modules.RabiRace.Avatar;
import sig.modules.RabiRace.AvatarSelectionWindow;
import sig.modules.RabiRace.ClickableButton;
import sig.modules.RabiRace.ColorCycler;
import sig.modules.RabiRace.CreateButton;
import sig.modules.RabiRace.JoinButton;
import sig.modules.RabiRace.MemoryData;
import sig.modules.RabiRace.Profile;
import sig.modules.RabiRace.Session;
import sig.modules.RabiRace.SessionCreateWindow;
import sig.modules.RabiRace.SessionListData;
import sig.modules.RabiRace.SessionListWindow;
import sig.modules.RabiRibi.MemoryOffset;
import sig.modules.RabiRibi.MemoryType;
import sig.modules.utils.PsapiTools;
import sig.utils.DrawUtils;
import sig.utils.FileUtils;
import sig.utils.SoundUtils;
import sig.utils.TextUtils;

public class RabiRaceModule extends Module{
	final static String ITEMS_DIRECTORY = sigIRC.BASEDIR+"sigIRC/rabi-ribi/items/";
	final static String AVATAR_DIRECTORY = sigIRC.BASEDIR+"sigIRC/rabi-ribi/characters/";
	final int PROCESS_PERMISSIONS = WinNT.PROCESS_QUERY_INFORMATION | WinNT.PROCESS_VM_READ | WinNT.PROCESS_VM_WRITE;
	boolean foundRabiRibi = false;
	int rabiRibiPID = -1;
	long rabiRibiMemOffset = 0;
	public HANDLE rabiribiProcess = null;
	public static HashMap<String,Image> image_map = new HashMap<String,Image>();
	public static Image UNKNOWN_ITEM;
	public static ColorCycler rainbowcycler = new ColorCycler(new Color(255,0,0,96),16);
	public static RabiRaceModule module;
	public Profile myProfile = new Profile(this,false);
	public static SessionListWindow window;
	public static SessionCreateWindow createwindow;
	public static AvatarSelectionWindow avatarwindow;
	public static Session mySession;
	boolean firstCheck=false;
	public List<ScrollingText> messages = new ArrayList<ScrollingText>();
	public static int lastScrollX = 0;
	boolean firstUpdate=true;
	boolean mouseoverAvatar=false;
	public static boolean avatarRetrieved=false;
	public static int CLIENT_SERVER_READTIME = -1;
	public static boolean syncItems = false;
	
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
		createwindow = new SessionCreateWindow();
		createwindow.setVisible(false);
		avatarwindow = new AvatarSelectionWindow();
		avatarwindow.setVisible(false);
		//System.out.println("Money value is: "+readIntFromMemory(MemoryOffset.MONEY));
	}

	private void Initialize() {
		CheckRabiRibiClient();
		
		VerifyClientIsValid(); //If the client is not allowed to send data, we need to know that.

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleWithFixedDelay(()->{
			CheckRabiRibiClient();
			if (foundRabiRibi) {
				myProfile.uploadProfile();
				getSessionList();
				getMessageUpdates();
				//trimeadProfile.downloadProfile();
				firstCheck=true;
				if (mySession!=null) {
					RequestData("tmp.data","key=keepalivesession&session="+mySession.getID());
				}
			}
		}, 5000, 5000, TimeUnit.MILLISECONDS);
		
		myProfile.downloadProfile(); //Synchronize our profile at the beginning.
		//System.out.println(myProfile.avatar.displayName);
		
		ScheduledExecutorService scheduler2 = Executors.newScheduledThreadPool(1);
		scheduler2.scheduleWithFixedDelay(()->{
			if (foundRabiRibi) {
				UpdateMyProfile();
			}
		}, 250, 250, TimeUnit.MILLISECONDS);
		
		File dir = new File(ITEMS_DIRECTORY);
		
		for (MemoryData data : MemoryData.values()) {
			//Attempt to fetch from server.
			new FileManager("sigIRC/rabi-ribi/items/"+data.img_path).verifyAndFetchFileFromServer();
		}
		for (Avatar avatar : Avatar.values()) {
			new FileManager("sigIRC/rabi-ribi/characters/"+avatar.fileName).verifyAndFetchFileFromServer();
		}
		new FileManager("sigIRC/rabi-ribi/items/easter_egg.png").verifyAndFetchFileFromServer();
		new FileManager("sigIRC/rabi-ribi/items/health_up.png").verifyAndFetchFileFromServer();
		new FileManager("sigIRC/rabi-ribi/items/mana_up.png").verifyAndFetchFileFromServer();
		new FileManager("sigIRC/rabi-ribi/items/regen_up.png").verifyAndFetchFileFromServer();
		new FileManager("sigIRC/rabi-ribi/items/pack_up.png").verifyAndFetchFileFromServer();
		new FileManager("sigIRC/rabi-ribi/items/attack_up.png").verifyAndFetchFileFromServer();
		
		AddImagesToImageMap(dir,ITEMS_DIRECTORY);
		dir = new File(AVATAR_DIRECTORY);
		AddImagesToImageMap(dir,AVATAR_DIRECTORY);
		
		for (MemoryData md : MemoryData.values()) {
			if (md.key_item) {
				key_items_list.add(md);
			} else {
				badges_list.add(md);
			}
		}
		
		try {
			UNKNOWN_ITEM = ImageIO.read(new File(sigIRC.BASEDIR+"sigIRC/rabi-ribi/unknown.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//trimeadProfile.username = "trimead";
		
		join_button = new JoinButton(new Rectangle(2,(int)(position.getHeight()-18),120,18),"Join Session (0)",this);
		create_button = new CreateButton(new Rectangle(122,(int)(position.getHeight()-18),120,18),"Create Session",this);
	}

	private void VerifyClientIsValid() {
		String[] data = RequestAndStoreData("tmptimer.dat","key=timestamp");
		int time = -1;
		if (data.length>0) {
			time = Integer.parseInt(data[0]);
		}
		CLIENT_SERVER_READTIME = time;
	}

	private void RequestData(String filename,String requestString) {
		File file = new File(sigIRC.BASEDIR+"sigIRC/"+filename);
		try {
			org.apache.commons.io.FileUtils.copyURLToFile(new URL("http://45.33.13.215/rabirace/send.php?"+requestString),file);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	String[] RequestAndStoreData(String filename,String requestString) {
		RequestData(filename,requestString);
		String[] data = FileUtils.readFromFile(sigIRC.BASEDIR+"sigIRC/"+filename);
		return data;
	}

	private void AddImagesToImageMap(File dir, String DIRECTORY) {
		String[] images = dir.list();
		List<String> filtered_images = new ArrayList<String>();
		for (String file : images) {
			File f = new File(DIRECTORY+file);
			if (!f.isDirectory()) {
				filtered_images.add(file);
			}
		}
		images = filtered_images.toArray(new String[filtered_images.size()]);
		for (String image : images) {
			try {
				//System.out.println("Loaded "+image);
				image_map.put(image, ImageIO.read(new File(DIRECTORY+image)));
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
	
	private void getMessageUpdates() {
		File file = new File(sigIRC.BASEDIR+"sigIRC/messages");
		try {
			org.apache.commons.io.FileUtils.copyURLToFile(new URL("http://45.33.13.215/rabirace/send.php?key=getupdates&timekey="+RabiRaceModule.CLIENT_SERVER_READTIME+"&name="+myProfile.username),file);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] data = FileUtils.readFromFile(sigIRC.BASEDIR+"sigIRC/messages");
		//boolean message_played=false;
		for (String s : data) {
			if (s.length()>0) {
				messages.add(new ScrollingText(s,(int)(lastScrollX+position.getWidth()+24),(int)(position.getHeight()-28)));
				/*message_played=true;
				System.out.println("Perform item sync with other players.");
				SyncItemsWithOtherPlayers();*/
				syncItems=true;
			}
		}
		/*if (message_played && mySession.isCoop()) {
			SoundUtils.playSound(sigIRC.BASEDIR+"sigIRC/collect_item.wav");
		}*/
	}
	
	public void SyncItemsWithOtherPlayers() {
		boolean soundPlayed=false;
		for (Profile p : mySession.getPlayers()) {
			if (p!=myProfile && !p.isPaused) {
				boolean updateRequired=false;
				for (MemoryData m : p.key_items.keySet()) {
					if (p.key_items.get(m)!=0 && (!myProfile.key_items.containsKey(m) ||  myProfile.key_items.get(m)==0)) {
						System.out.println("You do not have a "+m.name+". Syncing from "+p.displayName+".");
						writeIntToMemory(m.mem.getOffset(),Math.abs(p.key_items.get(m)));
						updateRequired=true;
					}
				}
				for (MemoryData m : p.badges.keySet()) {
					if (p.badges.get(m)!=0 && (!myProfile.badges.containsKey(m) ||  myProfile.badges.get(m)==0)) {
						System.out.println("You do not have a "+m.name+". Syncing from "+p.displayName+".");
						writeIntToMemory(m.mem.getOffset(),Math.abs(p.badges.get(m)));
						updateRequired=true;
					}
				}
				if (Profile.GetHealthUpCount(p)>Profile.GetHealthUpCount(myProfile)) {
					System.out.println("You do not have the correct amount of health ups. Syncing to ("+p.healthUps+") from "+p.displayName+".");
					UpdateRange(MemoryOffset.HEALTHUP_START,MemoryOffset.HEALTHUP_END,p.healthUps);
					updateRequired=true;
				}
				if (Profile.GetManaUpCount(p)>Profile.GetManaUpCount(myProfile)) {
					System.out.println("You do not have the correct amount of mana ups. Syncing to ("+p.manaUps+") from "+p.displayName+".");
					UpdateRange(MemoryOffset.MANAUP_START,MemoryOffset.MANAUP_END,p.manaUps);
					updateRequired=true;
				}
				if (Profile.GetRegenUpCount(p)>Profile.GetRegenUpCount(myProfile)) {
					System.out.println("You do not have the correct amount of regen ups. Syncing to ("+p.regenUps+") from "+p.displayName+".");
					UpdateRange(MemoryOffset.REGENUP_START,MemoryOffset.REGENUP_END,p.regenUps);
					updateRequired=true;
				}
				if (Profile.GetPackUpCount(p)>Profile.GetPackUpCount(myProfile)) {
					System.out.println("You do not have the correct amount of pack ups. Syncing to ("+p.packUps+") from "+p.displayName+".");
					UpdateRange(MemoryOffset.PACKUP_START,MemoryOffset.PACKUP_END,p.packUps);
					updateRequired=true;
				}
				if (Profile.GetAttackUpCount(p)>Profile.GetAttackUpCount(myProfile)) {
					System.out.println("You do not have the correct amount of attack ups. Syncing to ("+p.attackUps+") from "+p.displayName+".");
					UpdateRange(MemoryOffset.ATTACKUP_START,MemoryOffset.ATTACKUP_END,p.attackUps);
					updateRequired=true;
				}
				
				if (updateRequired && mySession.isCoop()) {
					if (!soundPlayed) {
						SoundUtils.playSound(sigIRC.BASEDIR+"sigIRC/collect_item.wav");
						soundPlayed=true;
					}
					updateRequired=false;
				}
			}
		}
	}

	private void UpdateRange(MemoryOffset start, MemoryOffset end, String i) {
		/*int f=63;
		while (i>0 && f>0) {
			if (readIntFromMemory(start.getOffset())==0) {
				writeIntToMemory(start.getOffset()+(f*4),1);
				i--;
			}
			f--;
		}*/
		for (int l=0;l<i.length();l++) {
			if (i.charAt(l)=='1') {
				writeIntToMemory(start.getOffset()+(l*4),1);
			}
		}
	}

	public void getSessionList() {
		File file = new File(sigIRC.BASEDIR+"sessions");
		try {
			org.apache.commons.io.FileUtils.copyURLToFile(new URL("http://45.33.13.215/rabirace/send.php?key=getsessions"),file);
			String[] data = FileUtils.readFromFile(sigIRC.BASEDIR+"sessions");
			//System.out.println("Data is "+Arrays.toString(data));
			session_listing.UpdateData(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (mySession==null) {
			join_button.setButtonLabel("Join Session ("+session_listing.getSessions().size()+")");
		} else {
			join_button.setButtonLabel("Leave Session");
		}
		window.UpdateSessionList();
	}

	public void mousePressed(MouseEvent ev) {
		if (firstCheck && join_button.mouseInsideBounds(ev)) {
			join_button.onClickEvent(ev);
		}
		if (firstCheck && mySession==null && create_button.mouseInsideBounds(ev)) {
			create_button.onClickEvent(ev);
		}
		if (mouseoverAvatar) {
			avatarwindow.setVisible(true);
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
			/*System.out.println("Value: ("+Integer.toHexString((int)(rabiRibiMemOffset+0x1679EF0))+"): "+readIntFromMemory(0x1679EF0));
			System.out.println("Write...");
			writeIntToMemory(0x1679EF0,0);
			System.out.println("Value: ("+Integer.toHexString((int)(rabiRibiMemOffset+0x1679EF0))+"): "+readIntFromMemory(0x1679EF0));*/
			if (window!=null) {
				window.run();
			} 
			for (int i=0;i<messages.size();i++) {
				if (!messages.get(i).run()) {
					messages.remove(i--);
				}
			}
			if (lastScrollX>0) {
				lastScrollX-=ScrollingText.SCROLLSPD;
			}
		}
	}
	
	private void UpdateMyProfile() {
		if (foundRabiRibi) {
			//System.out.println("Called.");
			int paused = readIntFromMemory(MemoryOffset.PAUSED) + readIntFromMemory(MemoryOffset.TITLE_SCREEN);
			//int paused = 0; //TODO FORCE UNPAUSE FOR NOW.
			float itempct = readFloatFromMemory(MemoryOffset.ITEM_PERCENT);
			myProfile.isPaused = paused>=1;
			//System.out.println(itempct+","+paused);
			if (paused==0 && itempct>=0) {
				myProfile.archiveAllValues();
				myProfile.rainbowEggs = readIntFromMemory(MemoryOffset.RAINBOW_EGG_COUNT);
				myProfile.attackUps = readItemCountFromMemory(MemoryOffset.ATTACKUP_START,MemoryOffset.ATTACKUP_END);
				myProfile.healthUps = readItemCountFromMemory(MemoryOffset.HEALTHUP_START,MemoryOffset.HEALTHUP_END);
				myProfile.manaUps = readItemCountFromMemory(MemoryOffset.MANAUP_START,MemoryOffset.MANAUP_END);
				myProfile.regenUps = readItemCountFromMemory(MemoryOffset.REGENUP_START,MemoryOffset.REGENUP_END);
				myProfile.packUps = readItemCountFromMemory(MemoryOffset.PACKUP_START,MemoryOffset.PACKUP_END);
				myProfile.itempct = itempct;
				myProfile.mappct = readFloatFromMemory(MemoryOffset.MAP_PERCENT);
				myProfile.difficulty = readIntFromMemory(MemoryOffset.GAME_DIFFICULTY);
				myProfile.loop = readIntFromMemory(MemoryOffset.GAME_LOOP);
				myProfile.updateClientValues();
				if (mySession!=null && !firstUpdate) {
					myProfile.compareAndAnnounceAllChangedValues();
				}
				if (myProfile.compareAllChangedValues()>0) {
					myProfile.image_display_update_required=true;
				}
				myProfile.stat_update_required=true;
				firstUpdate=false;
			}
			myProfile.playtime = readIntFromMemory(MemoryOffset.PLAYTIME);
			if (mySession!=null) {
				for (Profile p : mySession.getPlayers()) {
					if (!p.username.equalsIgnoreCase(myProfile.username)) {
						if (!p.isPaused) {
							p.playtime += myProfile.playtime-myProfile.getArchive().playtime;
						}
					}
				}
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
	
	public void writeIntToMemory(long offset,int value) {
		//Pointer valueptr = new Pointer();
		Memory valueptr = new Memory(8);
		valueptr.setInt(0, value);
		//new Pointer(rabiRibiMemOffset+offset).setMemory((long)0, (long)4, (byte)value);
		Kernel32.INSTANCE.WriteProcessMemory(rabiribiProcess, 
				new Pointer(rabiRibiMemOffset+offset),valueptr,4,null);
		//Kernel32.INSTANCE.ReadProcessMemory(rabiribiProcess, new Pointer(rabiRibiMemOffset+offset), mem, 4, null);
		//return mem.getInt(0);
	}
	
	public void writeFloatToMemory(long offset,float value) {
		Memory mem = new Memory(4);
		//Pointer valueptr = new Pointer();
		new Pointer(rabiRibiMemOffset+offset).setMemory((long)0, (long)4, (byte)value);
		//Kernel32.INSTANCE.WriteProcessMemory(rabiribiProcess, , value,4,null);
		//Kernel32.INSTANCE.ReadProcessMemory(rabiribiProcess, new Pointer(rabiRibiMemOffset+offset), mem, 4, null);
		//return mem.getInt(0);
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
	
	String readItemCountFromMemory(MemoryOffset start_range,
			MemoryOffset end_range) {
		StringBuilder sb = new StringBuilder();
		for (long i=start_range.getOffset();i<=end_range.getOffset();i+=4) {
			sb.append(readIntFromMemory(i));
		}
		return sb.toString();
	}
	
	public void draw(Graphics g) {
		super.draw(g);
		
		if (!foundRabiRibi) {
			DrawUtils.drawTextFont(g, sigIRC.panel.userFont, position.getX(), position.getY()+26, Color.BLACK, "Rabi-Ribi not found! Please start it.");
		} else {
			//myProfile.draw(g);
			Image panel = myProfile.getStatPanel((int)position.getWidth(),mySession);
			
			if (sigIRC.panel.lastMouseX>=position.getX() && 
					sigIRC.panel.lastMouseX<=position.getX()+(int)((position.getWidth()/400)*50) &&
					sigIRC.panel.lastMouseY>=position.getY() && 
					sigIRC.panel.lastMouseY<=position.getY()+(int)(((position.getWidth()/400)*50))) {
				mouseoverAvatar=true;
				Color ident = g.getColor();
				g.setColor(new Color(196,196,196,128));
				g.fillRect((int)(position.getX()+1), (int)(position.getY()+1), (int)((position.getWidth()/400)*50), (int)((position.getWidth()/400)*50));
				g.setColor(ident);
				//System.out.println("Mouse over avatar.");
			} else {
				mouseoverAvatar=false;
			}
			
			g.drawImage(panel, (int)position.getX(), (int)position.getY(), sigIRC.panel);
			g.drawImage(myProfile.getStatText((int)position.getWidth(),mySession), (int)position.getX(), (int)position.getY(), sigIRC.panel);
			
			//Profile.DrawMultiPanel(g, (int)(position.getX()), (int)(position.getY())+panel.getHeight(sigIRC.panel), (int)position.getWidth(), testing);
			if (mySession!=null) {
				List<Profile> sessionPlayers = new ArrayList<Profile>();
				for (Profile p : mySession.getPlayers()) {
					if (!p.username.equalsIgnoreCase(myProfile.username)) {
						sessionPlayers.add(p);
						//System.out.println("Found unique player "+p);
					}
				}
				Profile.DrawMultiPanel(g, (int)(position.getX()), (int)(position.getY())+panel.getHeight(sigIRC.panel), (int)position.getWidth(), mySession);
			}
			
			if (firstCheck) {
				join_button.draw(g);
				if (mySession==null) {
					create_button.draw(g);
				}
			}
			g.setColor(Color.BLACK);
			g.fillRect((int)(position.getX()), (int)(position.getY()+position.getHeight()-28-20), (int)(position.getWidth()), 20);
			for (int i=0;i<messages.size();i++) {
				messages.get(i).draw(g);
			}
		}
	}
	
	class ScrollingText{
		String msg;
		int x;
		int y;
		Rectangle2D bounds;
		final static int SCROLLSPD = 4;
		
		public ScrollingText(String message, int x, int y) {
			this.msg = message;
			this.x = x;
			this.y = y;
			this.bounds = TextUtils.calculateStringBoundsFont(message, sigIRC.panel.rabiRibiTinyDisplayFont);
			RabiRaceModule.lastScrollX += bounds.getWidth() + 96;
		}
		
		public boolean run() {
			x-=SCROLLSPD;
			if (x+bounds.getWidth()<0) {
				return false;
			}
			return true;
		}
		
		public void draw(Graphics g) {
			if (x<position.getWidth()) {
				DrawUtils.drawOutlineText(g, sigIRC.panel.rabiRibiTinyDisplayFont, position.getX()+x, position.getY()+y-6, 2, Color.WHITE, Color.GRAY, msg);
			}
		}
	}
}
