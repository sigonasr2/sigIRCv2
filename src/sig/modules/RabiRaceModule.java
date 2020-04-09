package sig.modules;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
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
import sig.modules.RabiRace.MarkMapButton;
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
	final static int ERINA = 0;
	final static int RIBBON = 1;
	final static int CICINI = 2;
	final static int MIRIAM = 3;
	public final static int DEBUGMODE = 0;
	final static String ITEMS_DIRECTORY = sigIRC.BASEDIR+"sigIRC/rabi-ribi/items/";
	final static String AVATAR_DIRECTORY = sigIRC.BASEDIR+"sigIRC/rabi-ribi/characters/";
	final int PROCESS_PERMISSIONS = WinNT.PROCESS_QUERY_INFORMATION | WinNT.PROCESS_VM_READ | WinNT.PROCESS_VM_WRITE;
	boolean foundRabiRibi = false;
	final static int WAITFRAMEMAX = 120;
	int waitframes = WAITFRAMEMAX;
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
	public List<String> messagequeue = new ArrayList<String>();
	public static int lastScrollX = 0;
	boolean firstUpdate=true;
	boolean mouseoverAvatar=false;
	public static boolean avatarRetrieved=false;
	public static int CLIENT_SERVER_READTIME = -1;
	public static boolean syncItems = false;
	public static int selectedMapIcon = 13;
	public static int lastreadmapdata = 0;
	public boolean viewingupdatedMapIcons=false;
	public HashMap<Integer,Integer> mapdata = new HashMap<Integer,Integer>();
	public HashMap<Integer,Integer> newmapdata = new HashMap<Integer,Integer>();
	public static ScheduledExecutorService scheduler,scheduler2; 
	public static boolean syncEvents = true;
	//public static boolean darknessHasReachedzero = true; //darkness needs to go down to 0, then go back up.
	public static boolean hasDied = false;
	public static String lastEventString = "";
	int frames=0;
	
	public static final int[] RESTRICTED_EVENTS = new int[] {256,257,260,262,264,265,266,267,268,269,271,272,278,279,284,289,290,295,296,307,315,316,322,323,324,331,333,344,345,371,377,379,385,386,387,399,412,427,428,451,452,464,465,484,516,517,518,519};
	public static final int[] BOSSSONGS = new int[] {8,27,33,34,36,37,38,39,42,43,44,47,48,51,52,54,56,59,60,61,63,64};
	
	public SessionListData session_listing = new SessionListData();
	
	ClickableButton join_button,create_button,markmap_button;
	
	public Image mapiconimg;
	
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
		
		File f = new File("debug.log");
		f.delete();
		f = new File("debug2.log");
		f.delete();
		
		CheckRabiRibiClient();
		
		VerifyClientIsValid(); //If the client is not allowed to send data, we need to know that.

		scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleWithFixedDelay(()->{
			RunRabiRace();
		}, 1500, 1500, TimeUnit.MILLISECONDS);
		
		myProfile.downloadProfile(); //Synchronize our profile at the beginning.
		//System.out.println(myProfile.avatar.displayName);
		
		scheduler2 = Executors.newScheduledThreadPool(1);
		scheduler2.scheduleWithFixedDelay(()->{
			RunRabiRaceUpdater();
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
			mapiconimg = ImageIO.read(new File(sigIRC.BASEDIR+"map_icons.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//trimeadProfile.username = "trimead";
		
		join_button = new JoinButton(new Rectangle(2,(int)(position.getHeight()-18),120,18),"Join Session (0)",this);
		create_button = new CreateButton(new Rectangle(122,(int)(position.getHeight()-18),120,18),"Create Session",this);
		markmap_button = new MarkMapButton(new Rectangle(2,(int)(position.getHeight()-42),120,18),"Mark Map",this);
	}

	private void RunRabiRaceUpdater() {
		if (foundRabiRibi) {
			UpdateMyProfile();
		}
	}

	private void RunRabiRace() {
		CheckRabiRibiClient();
		if (foundRabiRibi) {
			FileUtils.logToFile("["+System.currentTimeMillis()+"]Start update cycle...", "debug.log");
			myProfile.uploadProfile();
			getSessionList();
			getMessageUpdates();
			//trimeadProfile.downloadProfile();
			firstCheck=true;
			if (mySession!=null) {
				RequestData("tmp.data","key=keepalivesession&session="+mySession.getID());
				FileUtils.logToFile("["+System.currentTimeMillis()+"]Requested data"+"key=keepalivesession&session="+mySession.getID(), "debug.log");
			}
		}
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
		FileUtils.logToFile("["+System.currentTimeMillis()+"]Message updates."+"http://45.33.13.215/rabirace/send.php?key=getupdates&timekey="+RabiRaceModule.CLIENT_SERVER_READTIME+"&name="+myProfile.username+" Data: "+Arrays.deepToString(data), "debug.log");
		//boolean message_played=false;
		for (String s : data) {
			if (s.length()>0) {
				messages.add(new ScrollingText(s,(int)(lastScrollX+position.getWidth()+24),(int)(position.getHeight()-28)));
				/*message_played=true;
				System.out.println("Perform item sync with other players.");
				SyncItemsWithOtherPlayers();*/
				messagequeue.add(s);
				if (mySession!=null && mySession.isCoop()) {
					syncItems=true;
				}
				FileUtils.logToFile("Add "+s+" to message queue.", "debug.log");
			}
		}
		if (mySession!=null) {
			File file2 = new File(sigIRC.BASEDIR+"mapdata");
			try {
				org.apache.commons.io.FileUtils.copyURLToFile(new URL("http://45.33.13.215/rabirace/maps/"+mySession.getID()),file2);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			int MapUpdatesRequired=0;
			String byWhom = "";
			String[] mapdata = FileUtils.readFromFile(sigIRC.BASEDIR+"mapdata");
			for (int i=lastreadmapdata+1;i<mapdata.length;i++) {
				String[] s = mapdata[i].split(";");
				if (s.length>=2) {
					if (!s[0].equalsIgnoreCase(myProfile.username)) {
						if (!this.newmapdata.containsKey(Integer.parseInt(s[1]))) {
							MapUpdatesRequired++;
							byWhom = s[0];
							this.newmapdata.put(Integer.parseInt(s[1]),Integer.parseInt(s[2]));
						}
					}
					this.mapdata.put(Integer.parseInt(s[1]),Integer.parseInt(s[2]));
					int id = Integer.parseInt(s[1]);
					AddMapPoint((int)Math.floor(id/450),(int)Math.floor(id%450/18),id%450%18,Integer.parseInt(s[2]),false);
					lastreadmapdata=Math.max(i,lastreadmapdata);
				}
			}
			FileUtils.logToFile("["+System.currentTimeMillis()+"]MapUpdates:"+MapUpdatesRequired+" - "+byWhom, "debug.log");
			
			if (MapUpdatesRequired>0) {
				if (MapUpdatesRequired==1) {
					messagequeue.add(byWhom+" has added a new marker to the map!");
				} else {
					messagequeue.add(MapUpdatesRequired+" new markers have been added to the map!");
				}
			}
		}
		/*if (message_played && mySession.isCoop()) {
			SoundUtils.playSound(sigIRC.BASEDIR+"sigIRC/collect_item.wav");
		}*/
	}
	
	public void AddMapPoint(int area, int x, int y, int color, boolean update) {
		int id = y+18*x+area*450;
		writeIntToMemory(MemoryOffset.MAP_REGION_START.getOffset()+
				id*4,color);
		
		
		if (mySession!=null && mySession.isCoop() && update) {
			mapdata.put(id,color);
			File file2 = new File(sigIRC.BASEDIR+"tmp_mapdata");
			try {
				org.apache.commons.io.FileUtils.copyURLToFile(new URL("http://45.33.13.215/rabirace/send.php?session="+mySession.getID()+"&key=addmappoint&timekey="+RabiRaceModule.CLIENT_SERVER_READTIME+"&mappoint="+myProfile.username+";"+id+";"+color),file2);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void SyncItemsWithOtherPlayers() {
		boolean soundPlayed=false;
		if (mySession!=null && mySession.isCoop()) {
			for (Profile p : mySession.getPlayers()) {
				if (p!=myProfile && !p.isPaused && !myProfile.isPaused && p.username!=myProfile.username) {
					boolean updateRequired=false;
					for (MemoryData m : p.key_items.keySet()) {
						if (p.key_items.get(m)!=0 && (!myProfile.key_items.containsKey(m) ||  myProfile.key_items.get(m)==0)) {
							System.out.println("You do not have a "+m.name+". Syncing from "+p.displayName+".");
							if (!(readIntFromMemory(MemoryOffset.BOSS_FIGHT.getOffset())==1 && m==MemoryData.RIBBON)) {
								writeIntToMemory(m.mem.getOffset(),Math.abs(p.key_items.get(m)));
								updateRequired=true;
							}
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
						UpdateRange_WithoutParsing(MemoryOffset.HEALTHUP_START,MemoryOffset.HEALTHUP_END,p.healthUps);
						updateRequired=true;
					}
					if (Profile.GetManaUpCount(p)>Profile.GetManaUpCount(myProfile)) {
						System.out.println("You do not have the correct amount of mana ups. Syncing to ("+p.manaUps+") from "+p.displayName+".");
						UpdateRange_WithoutParsing(MemoryOffset.MANAUP_START,MemoryOffset.MANAUP_END,p.manaUps);
						updateRequired=true;
					}
					if (Profile.GetRegenUpCount(p)>Profile.GetRegenUpCount(myProfile)) {
						System.out.println("You do not have the correct amount of regen ups. Syncing to ("+p.regenUps+") from "+p.displayName+".");
						UpdateRange_WithoutParsing(MemoryOffset.REGENUP_START,MemoryOffset.REGENUP_END,p.regenUps);
						updateRequired=true;
					}
					if (Profile.GetPackUpCount(p)>Profile.GetPackUpCount(myProfile)) {
						System.out.println("You do not have the correct amount of pack ups. Syncing to ("+p.packUps+") from "+p.displayName+".");
						UpdateRange_WithoutParsing(MemoryOffset.PACKUP_START,MemoryOffset.PACKUP_END,p.packUps);
						updateRequired=true;
					}
					if (Profile.GetAttackUpCount(p)>Profile.GetAttackUpCount(myProfile)) {
						System.out.println("You do not have the correct amount of attack ups. Syncing to ("+p.attackUps+") from "+p.displayName+".");
						UpdateRange_WithoutParsing(MemoryOffset.ATTACKUP_START,MemoryOffset.ATTACKUP_END,p.attackUps);
						updateRequired=true;
					}
					if (!p.eventStruct.equalsIgnoreCase(myProfile.eventStruct)) {
						FileUtils.logToFile("["+System.currentTimeMillis()+"]Events are not synced with "+p.displayName, "debug.log");
						StringBuilder finalevents = new StringBuilder();
						String[] events = p.eventStruct.split("_");
						FileUtils.logToFile("["+System.currentTimeMillis()+"]"+p.displayName+"'s events: "+Arrays.toString(events), "debug.log");
						String[] myevents = myProfile.eventStruct.split("_");
						FileUtils.logToFile("["+System.currentTimeMillis()+"]"+myProfile.displayName+"'s events: "+Arrays.toString(myevents), "debug.log");
						for (int i=0;i<Profile.EVENT_COUNT;i++) {
							if (i!=1 && i!=90 && i!=190 && i!=177) { //Ignore syncing ribbon event, irisu library event, and forgotten cave 2 event.
								finalevents.append((events[i].compareTo(Integer.toString(readIntFromMemory(MemoryOffset.EVENT_START.getOffset()+i*4)))>0)?events[i]:readIntFromMemory(MemoryOffset.EVENT_START.getOffset()+i*4));
								/*if (events[i].compareTo(Integer.toString(readIntFromMemory(MemoryOffset.EVENT_START.getOffset()+i*4)))>0) {
									FileUtils.logToFile("["+System.currentTimeMillis()+"]Updated event "+i+" to value "+events[i], "debug.log");
								}*/
								//finalevents.append((Integer.compare(myProfile.eventStruct.charAt(i),p.eventStruct.charAt(i))<0)?p.eventStruct.charAt(i):myProfile.eventStruct.charAt(i));
							} else {
								finalevents.append(readIntFromMemory(MemoryOffset.EVENT_START.getOffset()+i*4));
							}
							finalevents.append("_");
						}
						UpdateRange(MemoryOffset.EVENT_START,MemoryOffset.EVENT_END,finalevents.toString());
						FileUtils.logToFile("["+System.currentTimeMillis()+"]ll event ranges updated.", "debug.log");
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
	}

	private void UpdateRange_WithoutParsing(MemoryOffset start, MemoryOffset end, String i) {
		/*int f=63;
		while (i>0 && f>0) {
			if (readIntFromMemory(start.getOffset())==0) {
				writeIntToMemory(start.getOffset()+(f*4),1);
				i--;
			}
			f--;
		}*/
		for (int l=0;l<i.length();l++) {
			writeIntToMemory(start.getOffset()+(l*4),Integer.parseInt(Character.toString(i.charAt(l))));
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
		String[] split = i.split("_");
		FileUtils.logToFile("["+System.currentTimeMillis()+"]Parsed events: "+Arrays.deepToString(split)+".", "debug.log");
		for (int l=0;l<split.length;l++) {
			writeIntToMemory(start.getOffset()+(l*4),Integer.parseInt(split[l]));
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
		FileUtils.logToFile("["+System.currentTimeMillis()+"]Retrieve session list."+"http://45.33.13.215/rabirace/send.php?key=getsessions", "debug.log");
		window.UpdateSessionList();
	}

	public void mousePressed(MouseEvent ev) {
		if (firstCheck && join_button.mouseInsideBounds(ev)) {
			join_button.onClickEvent(ev);
		}
		if (firstCheck && mySession==null && create_button.mouseInsideBounds(ev)) {
			create_button.onClickEvent(ev);
		}
		if (firstCheck && markmap_button.mouseInsideBounds(ev)) {
			markmap_button.onClickEvent(ev);
		}
		if (mouseoverAvatar) {
			avatarwindow.setVisible(true);
		}
	}

	public void mouseWheel(MouseWheelEvent ev) {
		selectedMapIcon+=Math.signum(ev.getWheelRotation());
		selectedMapIcon=(selectedMapIcon<0)?15:selectedMapIcon%16;
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
		frames++;
		if (foundRabiRibi) {
			if (scheduler.isTerminated() || scheduler.isShutdown()) {
				FileUtils.logToFile("["+System.currentTimeMillis()+"]For some reason scheduler was terminated! Trying to restart...", "debug2.log");
				scheduler.scheduleWithFixedDelay(()->{
					RunRabiRace();
				}, 1500, 1500, TimeUnit.MILLISECONDS);
			}
			if (scheduler2.isTerminated() || scheduler2.isShutdown()) {
				FileUtils.logToFile("["+System.currentTimeMillis()+"]For some reason scheduler2 was terminated! Trying to restart...", "debug2.log");
				scheduler2.scheduleWithFixedDelay(()->{
					RunRabiRaceUpdater();
				}, 250, 250, TimeUnit.MILLISECONDS);
			}
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
			if (messagequeue.size()>0) {
				
				if (NoMessageDisplayed()) {
					int character=0;
					character = messagequeue.get(0).contains("to the map!")?RabiRaceModule.MIRIAM:messagequeue.get(0).contains("Rainbow Egg")?RabiRaceModule.CICINI:messagequeue.get(0).contains(myProfile.displayName)?RabiRaceModule.ERINA:RabiRaceModule.RIBBON;
					DisplayMessage(messagequeue.get(0).length()>255?messagequeue.remove(0).substring(0,255):messagequeue.remove(0),8,character);
				}
			}
			
			if (lastScrollX>0) {
				lastScrollX-=ScrollingText.SCROLLSPD;
			}
		}
	}
	
	public void DisplayMessage(String s, int seconds, int character) {
		writeIntToMemory(MemoryOffset.MESSAGE_CHARACTER.getOffset(),character);
		writeStringToMemory(MemoryOffset.MESSAGE_TEXT.getOffset(),s,256);
		writeIntToMemory(MemoryOffset.MESSAGE_TEXTREF.getOffset(),27);
	}

	private boolean NoMessageDisplayed() {
		return readIntFromMemory(MemoryOffset.MESSAGE_TIMER)==0;
	}
	
	public boolean OnTitleScreen() {
		/*return readIntFromMemory(MemoryOffset.INTERFACE_GRAPHICS_CONTROLLER)==809054496 ||
				readIntFromMemory(MemoryOffset.INTERFACE_GRAPHICS_CONTROLLER)==1593843744 ||
				readIntFromMemory(MemoryOffset.INTERFACE_GRAPHICS_CONTROLLER)==842018353 ||
				readIntFromMemory(MemoryOffset.INTERFACE_GRAPHICS_CONTROLLER)==706755104;*/
		return readIntFromMemory(MemoryOffset.TITLE_SCREEN)==0;
	}

	private void UpdateMyProfile() {
		if (foundRabiRibi) {
			//System.out.println("Called.");
			int paused = readIntFromMemory(MemoryOffset.PAUSED) + (OnTitleScreen()?1:0);
			//int paused = 0; //TODO FORCE UNPAUSE FOR NOW.
			float itempct = readFloatFromMemory(MemoryOffset.ITEM_PERCENT);
			myProfile.isPaused = paused>=1;
			
			if (mySession!=null && mySession.isCoop()) {

				if (OnTitleScreen()) {
					if (waitframes--<=0) {
						mapdata.clear();
						newmapdata.clear();
						lastreadmapdata=0;
					}
				} else {
					waitframes = WAITFRAMEMAX;
				}
				
				if (newmapdata.size()>0 && readIntFromMemory(MemoryOffset.PAUSED)>0) {
					viewingupdatedMapIcons=true;
					for (Integer i : newmapdata.keySet()) {
						int icon = mapdata.get(i);
						if ((frames%40)>=20) {
							AddMapPoint((int)Math.floor(i/450),(int)Math.floor(i%450/18),i%450%18,6,false);
						} else {
							AddMapPoint((int)Math.floor(i/450),(int)Math.floor(i%450/18),i%450%18,icon,false);
						}
					}
				}
				
				if (viewingupdatedMapIcons && readIntFromMemory(MemoryOffset.PAUSED)==0) {
					viewingupdatedMapIcons=false;
					for (Integer i : newmapdata.keySet()) {
						int icon = mapdata.get(i);
						AddMapPoint((int)Math.floor(i/450),(int)Math.floor(i%450/18),i%450%18,icon,false);
					}
					newmapdata.clear();
				}
			}
			
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
				myProfile.map = readIntFromMemory(MemoryOffset.MAP_AREA_COLOR);
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
	public void writeStringToMemory(long offset,String value, int size) {
		//Pointer valueptr = new Pointer();
		Memory valueptr = new Memory(size);
		valueptr.setString(0, value);
		//new Pointer(rabiRibiMemOffset+offset).setMemory((long)0, (long)4, (byte)value);
		Kernel32.INSTANCE.WriteProcessMemory(rabiribiProcess, 
				new Pointer(rabiRibiMemOffset+offset),valueptr,size,null);
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
			if (mySession!=null) {
				markmap_button.draw(g);
				g.drawImage(mapiconimg, (int)(position.getX()+128), (int)(position.getY()+position.getHeight()-48), (int)(position.getX()+128+29), (int)(position.getY()+position.getHeight()-48)+29, selectedMapIcon*29, 0, selectedMapIcon*29+29, 29, sigIRC.panel);
			}
			/*g.setColor(Color.BLACK);
			g.fillRect((int)(position.getX()), (int)(position.getY()+position.getHeight()-28-20), (int)(position.getWidth()), 20);
			for (int i=0;i<messages.size();i++) {
				messages.get(i).draw(g);
			}*/
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

	public void MarkCurrentPosition() {
		AddMapPoint(readIntFromMemory(MemoryOffset.MAP_AREA_NUMBER),
				readIntFromMemory(MemoryOffset.MAP_TILE_X),
				readIntFromMemory(MemoryOffset.MAP_TILE_Y),
				selectedMapIcon+1,true);
		/*for (int i=0;i<17;i++) {
			AddMapPoint(0,i,0,i);
		}*/
	}
}
