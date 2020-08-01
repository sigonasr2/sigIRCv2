package sig.modules.RabiRace;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import sig.ScrollingText;
import sig.sigIRC;
import sig.modules.RabiRaceModule;
import sig.modules.RabiRibi.MemoryOffset;
import sig.utils.DrawUtils;
import sig.utils.FileUtils;
import sig.utils.TextUtils;

public class Profile {
	public static final int EVENT_COUNT = 265;
	public String username = sigIRC.nickname.toLowerCase();
	public String displayName = sigIRC.nickname;
	public Avatar avatar;
	public int playtime = 0;
	public String healthUps = "0000000000000000000000000000000000000000000000000000000000000000";
	public String attackUps = "0000000000000000000000000000000000000000000000000000000000000000";
	public String manaUps = "0000000000000000000000000000000000000000000000000000000000000000";
	public String regenUps = "0000000000000000000000000000000000000000000000000000000000000000";
	public String packUps = "0000000000000000000000000000000000000000000000000000000000000000";
	public String eventStruct = "0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_0_";
	public int rainbowEggs = 0;
	public boolean isPaused = false;
	public int difficulty = 0;
	public int loop = 0;
	public int map = 0; //Map color.
	public float itempct = 0;
	public float mappct = 0;
	public LinkedHashMap<MemoryData,Integer> key_items = new LinkedHashMap<MemoryData,Integer>();
	public LinkedHashMap<MemoryData,Integer> badges = new LinkedHashMap<MemoryData,Integer>();
	public List<String> updates = new ArrayList<String>();
	RabiRaceModule parent;
	public long lastWebUpdate = System.currentTimeMillis(); 
	DecimalFormat df = new DecimalFormat("0.0");
	Profile oldProfile;
	public boolean isArchive = false;
	final static Color TEAL = new Color(0,128,128);
	public Image statUpdateCacheImage;
	public Image imageDisplayUpdateImage;
	public boolean stat_update_required = true;
	public boolean image_display_update_required = true;
	public int timeKey = -1;
	public boolean syncing = false;
	
	public Profile(RabiRaceModule module) {
		this(module,true);
	}
	public Profile(RabiRaceModule module, boolean archive) {
		this.isArchive = archive;
		if (!isArchive) {
			oldProfile = new Profile(module,true);
		}
		this.parent = module;
		this.avatar = GetSeededAvatar(username);
	}
	
	public Profile getArchive() {
		return oldProfile;
	}
	
	public void archiveAllValues() {
		oldProfile.healthUps = healthUps;
		oldProfile.attackUps = attackUps;
		oldProfile.manaUps = manaUps;
		oldProfile.regenUps = regenUps;
		oldProfile.packUps = packUps;
		oldProfile.rainbowEggs = rainbowEggs;
		oldProfile.key_items = (LinkedHashMap<MemoryData, Integer>)key_items.clone();
		oldProfile.badges = (LinkedHashMap<MemoryData, Integer>)badges.clone();
		oldProfile.playtime = playtime;
	}
	
	public int compareAllChangedValues() {
		int count=0;
		if (oldProfile.healthUps!=healthUps) {
			count++;
		}
		if (oldProfile.attackUps!=attackUps) {
			count++;
		}
		if (oldProfile.manaUps!=manaUps) {
			count++;
		}
		if (oldProfile.regenUps!=regenUps) {
			count++;
		}
		if (oldProfile.packUps!=packUps) {
			count++;
		}
		if (oldProfile.rainbowEggs!=rainbowEggs) {
			count++;
		}
		for (MemoryData md : key_items.keySet()) {
			if ((!oldProfile.key_items.containsKey(md) &&
					key_items.containsKey(md)) || (
							oldProfile.key_items.containsKey(md) &&
							key_items.containsKey(md)) &&
						oldProfile.key_items.get(md)!=key_items.get(md)
					) {
				count++;
			}
		}
		for (MemoryData md : badges.keySet()) {
			if ((!oldProfile.badges.containsKey(md) &&
					badges.containsKey(md)) || (
							oldProfile.badges.containsKey(md) &&
							badges.containsKey(md)) &&
						oldProfile.badges.get(md)!=badges.get(md)
					) {
				count++;
			}
		}
		return count;
	}
	
	public static int GetHealthUpCount(Profile p) {
		int numb = 0;
		for (int i=0;i<p.healthUps.length();i++) {
			if (p.healthUps.charAt(i)=='1') {
				numb++;
			}
		}
		return numb;
	}
	
	public static int GetManaUpCount(Profile p) {
		int numb = 0;
		for (int i=0;i<p.manaUps.length();i++) {
			if (p.manaUps.charAt(i)=='1') {
				numb++;
			}
		}
		return numb;
	}
	
	public static int GetRegenUpCount(Profile p) {
		int numb = 0;
		for (int i=0;i<p.regenUps.length();i++) {
			if (p.regenUps.charAt(i)=='1') {
				numb++;
			}
		}
		return numb;
	}
	
	public static int GetPackUpCount(Profile p) {
		int numb = 0;
		for (int i=0;i<p.packUps.length();i++) {
			if (p.packUps.charAt(i)=='1') {
				numb++;
			}
		}
		return numb;
	}
	
	public static int GetAttackUpCount(Profile p) {
		int numb = 0;
		for (int i=0;i<p.attackUps.length();i++) {
			if (p.attackUps.charAt(i)=='1') {
				numb++;
			}
		}
		return numb;
	}
	
	public static int GetRainbowEggCount(Profile p) {
		return p.rainbowEggs;
	}

	public void MarkCurrentPosition() {
		int id = RabiRaceModule.module.readIntFromMemory(MemoryOffset.MAP_TILE_Y)+18*RabiRaceModule.module.readIntFromMemory(MemoryOffset.MAP_TILE_X)+RabiRaceModule.module.readIntFromMemory(MemoryOffset.MAP_AREA_NUMBER)*450;
		if (RabiRaceModule.module.readIntFromMemory(MemoryOffset.MAP_REGION_START.getOffset()+id*4)>1 &&
				RabiRaceModule.module.readIntFromMemory(MemoryOffset.MAP_REGION_START.getOffset()+id*4)!=3 &&
				RabiRaceModule.module.readIntFromMemory(MemoryOffset.MAP_REGION_START.getOffset()+id*4)!=7 
				&& !RabiRaceModule.module.mapdata.containsKey(id)) {
			RabiRaceModule.module.AddMapPoint(RabiRaceModule.module.readIntFromMemory(MemoryOffset.MAP_AREA_NUMBER),
					RabiRaceModule.module.readIntFromMemory(MemoryOffset.MAP_TILE_X),
					RabiRaceModule.module.readIntFromMemory(MemoryOffset.MAP_TILE_Y),
					16,false);
		}
		/*for (int i=0;i<17;i++) {
			AddMapPoint(0,i,0,i);
		}*/
	}
	
	public void compareAndAnnounceAllChangedValues() {
		//System.out.println(oldProfile.key_items.get(MemoryData.HAMMER)+","+key_items.get(MemoryData.HAMMER));
		int changedValueCount = compareAllChangedValues();
		if (changedValueCount==0) {
			return;
		}
		String announcement = "";
		if (GetHealthUpCount(oldProfile)==GetHealthUpCount(this)-1) {
			announcement = "has obtained a Health Up! ("+GetHealthUpCount(this)+" total)";
			MarkCurrentPosition();
		}
		if (GetAttackUpCount(oldProfile)==GetAttackUpCount(this)-1) {
			announcement = "has obtained an Attack Up! ("+GetAttackUpCount(this)+" total)";
			MarkCurrentPosition();
		}
		if (GetManaUpCount(oldProfile)==GetManaUpCount(this)-1) {
			announcement = "has obtained a Mana Up! ("+GetManaUpCount(this)+" total)";
			MarkCurrentPosition();
		}
		if (GetRegenUpCount(oldProfile)==GetRegenUpCount(this)-1) {
			announcement = "has obtained a Regen Up! ("+GetRegenUpCount(this)+" total)";
			MarkCurrentPosition();
		}
		if (GetPackUpCount(oldProfile)==GetPackUpCount(this)-1) {
			announcement = "has obtained a Pack Up! ("+GetPackUpCount(this)+" total)";
			MarkCurrentPosition();
		}
		if (GetRainbowEggCount(oldProfile)==GetRainbowEggCount(this)-1) {
			if (RabiRaceModule.mySession!=null &&
					RabiRaceModule.mySession.gamemode==0 &&
					RabiRaceModule.mySession.rainbowEggGoal>0) {
				if (RabiRaceModule.mySession.rainbowEggGoal-GetRainbowEggCount(this)==0) {
					announcement = "has obtained "+RabiRaceModule.mySession.rainbowEggGoal+" Rainbow Eggs! (NAME) has completed the race!";
				} else if (RabiRaceModule.mySession.rainbowEggGoal-GetRainbowEggCount(this)>0)
				{
					announcement = "has obtained a Rainbow Egg! ("+Math.max(RabiRaceModule.mySession.rainbowEggGoal-GetRainbowEggCount(this), 0)+" to go!)";
				}
			} else {
				announcement = "has obtained a Rainbow Egg! ("+rainbowEggs+" total)";
			}
		}
		for (MemoryData md : key_items.keySet()) {
			if (!oldProfile.key_items.containsKey(md) &&
					key_items.containsKey(md)) {
				announcement = "has obtained "+md.name+"!";
				MarkCurrentPosition();
			}
		}
		for (MemoryData md : badges.keySet()) {
			if (!oldProfile.badges.containsKey(md) &&
					badges.containsKey(md)) {
				announcement = "has obtained the "+md.name+" badge!";
				MarkCurrentPosition();
			}
		}
		if (announcement.length()>0 && changedValueCount!=0) {
			SendAnnouncement(announcement);
		}
	}
	
	private void SendAnnouncement(String string) {
		string = displayName+" "+string.replaceAll("(NAME)", displayName);
		string = string.replaceAll(" ", "%20");

		File file = new File(sigIRC.BASEDIR+"sigIRC/tmp.data");
		try {
			org.apache.commons.io.FileUtils.copyURLToFile(new URL("http://45.33.13.215:8080/rabirace/send.php?key=addupdate&session="+RabiRaceModule.mySession.id+"&message="+string),file);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void updateClientValues() {
		for (MemoryData md : RabiRaceModule.key_items_list) {
			//System.out.println("Checking "+md.getDisplayName());
			int val = parent.readIntFromMemory(md.mem);
			if (val!=0) {
				key_items.put(md, val);
				//System.out.println("Obtained "+md.getDisplayName());
			} else {
				key_items.remove(md);
			}
		}
		for (MemoryData md : RabiRaceModule.badges_list) {
			int val = parent.readIntFromMemory(md.mem);
			if (val!=0) {
				badges.put(md, val);
				//System.out.println("Obtained "+md.getDisplayName());
			} else {
				badges.remove(md);
			}
		}
		String[] previousEventStruct = eventStruct.split("_");
		StringBuilder events = new StringBuilder();
	
		
		if (parent.readIntFromMemory(MemoryOffset.PLAYERHEALTH)<=0) {
			RabiRaceModule.syncEvents=false;
			RabiRaceModule.hasDied=true;
		}
		
		if (!RabiRaceModule.syncEvents && RabiRaceModule.hasDied && parent.readIntFromMemory(MemoryOffset.PLAYERHEALTH)>0) {
			RabiRaceModule.syncEvents=true;
			RabiRaceModule.hasDied=false;
		}
		/*
		if (parent.readIntFromMemory(MemoryOffset.DARKNESS)>0 && RabiRaceModule.syncEvents) {
			RabiRaceModule.syncEvents=false;
		}
		if (!RabiRaceModule.syncEvents && !RabiRaceModule.darknessHasReachedzero &&
				parent.readIntFromMemory(MemoryOffset.DARKNESS)==0) {
			RabiRaceModule.darknessHasReachedzero=true;
		}
		if (!RabiRaceModule.syncEvents && RabiRaceModule.darknessHasReachedzero &&
				parent.readIntFromMemory(MemoryOffset.DARKNESS)>0) {
			RabiRaceModule.syncEvents=true;
			RabiRaceModule.darknessHasReachedzero=false;
		}*/
		
		RabiRaceModule.syncEvents = !InBossBattleSong();

		/*if (parent.readIntFromMemory(MemoryOffset.DARKNESS)>0 && RabiRaceModule.darknessHasReachedzero) {
			RabiRaceModule.syncEvents=!RabiRaceModule.syncEvents;
			RabiRaceModule.darknessHasReachedzero=false;
		}
		if (parent.readIntFromMemory(MemoryOffset.DARKNESS)==0) {
			RabiRaceModule.darknessHasReachedzero=true;
		}*/
		for (int i=0;i<EVENT_COUNT;i++) {
			if (NonRestrictedValue(i)) {
				int val = parent.readIntFromMemory(MemoryOffset.EVENT_START.getOffset()+i*4);
				events.append(val);
				events.append("_");
			} else {
				if (RabiRaceModule.syncEvents) {
					events.append(parent.readIntFromMemory(MemoryOffset.EVENT_START.getOffset()+i*4));
					events.append("_");
				} else {
					events.append(0);
					events.append("_");
				}
			}
			/*if (val>9 || val<0) {
				//System.out.println("WARNING! Event "+(256+i)+" has a value greater than 9 or negative number! Truncating to 1 value.");
				events.append(Integer.toString(val).charAt(0));
			} else {
				events.append(val);
			}*/
		}
		if (RabiRaceModule.syncEvents && 
				RabiRaceModule.lastEventString.equalsIgnoreCase(events.toString())) {
			eventStruct = events.toString();
		} else {
			RabiRaceModule.lastEventString = events.toString();
		}
		
		syncing = RabiRaceModule.syncEvents;
	}
	
	private boolean InBossBattleSong() {
		for (int i=0;i<RabiRaceModule.BOSSSONGS.length;i++) {
			if (parent.readIntFromMemory(MemoryOffset.MAP_AREA_COLOR)==RabiRaceModule.BOSSSONGS[i]) {
				return true;
			}
		}
		return false;
	}
	private boolean NonRestrictedValue(int i) {
		for (int j=0;j<RabiRaceModule.RESTRICTED_EVENTS.length;j++) {
			if (i==RabiRaceModule.RESTRICTED_EVENTS[j]-256) {
				return false;
			}
		}
		return true;
	}
	public void uploadProfile() {
		if (sigIRC.authenticated) {
			File file = new File(sigIRC.BASEDIR+"tmp2");
			try {
				org.apache.commons.io.FileUtils.copyURLToFile(new URL("http://45.33.13.215:8080/rabirace/send.php?key=playerdata&timekey="+RabiRaceModule.CLIENT_SERVER_READTIME+"&name="+sigIRC.nickname.toLowerCase()+"&data="+getDataString()),file);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			FileUtils.logToFile("["+System.currentTimeMillis()+"]Upload profile. "+"http://45.33.13.215:8080/rabirace/send.php?key=playerdata&timekey="+RabiRaceModule.CLIENT_SERVER_READTIME+"&name="+sigIRC.nickname.toLowerCase()+"&data="+getDataString(), "debug.log");
			
			//System.out.println(getDataString());
			//String[] data = FileUtils.readFromFile(sigIRC.BASEDIR+"tmp");
			//System.out.println(Arrays.toString(data));
		}
	}
	
	public boolean downloadProfile() {
		if (sigIRC.authenticated) {
			File file = new File(sigIRC.BASEDIR+"tmp_profile");
			try {
				org.apache.commons.io.FileUtils.copyURLToFile(new URL("http://45.33.13.215:8080/rabirace/send.php?key=retrievedata&timekey="+RabiRaceModule.CLIENT_SERVER_READTIME+"&retrievename="+username.toLowerCase()+"&name="+sigIRC.nickname.toLowerCase()),file);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			FileUtils.logToFile("["+System.currentTimeMillis()+"]Download profile w/settings: "+"http://45.33.13.215:8080/rabirace/send.php?key=retrievedata&timekey="+RabiRaceModule.CLIENT_SERVER_READTIME+"&retrievename="+username.toLowerCase()+"&name="+sigIRC.nickname.toLowerCase(), "debug2.log");
			String[] data = FileUtils.readFromFile(sigIRC.BASEDIR+"tmp_profile");
			//System.out.println(Arrays.toString(data));
			if (data.length>=23) {
				int i=0;
				displayName = data[i++];
				try {
				avatar = Avatar.getAvatarFromID(Integer.parseInt(data[i++]));
				//System.out.println("Updated Avatar for Player "+displayName+" with Avatar "+avatar.displayName);
				timeKey = Integer.parseInt(data[i++]);
				playtime = Integer.parseInt(data[i++]);
				healthUps = data[i++];
				manaUps = data[i++];
				regenUps = data[i++];
				packUps = data[i++]; 
				attackUps = data[i++];
				rainbowEggs = Integer.parseInt(data[i++]);
				isPaused = Boolean.parseBoolean(data[i++]);
				difficulty = Integer.parseInt(data[i++]);
				loop = Integer.parseInt(data[i++]);
				itempct = Float.parseFloat(data[i++]);
				mappct = Float.parseFloat(data[i++]);
				} catch (NumberFormatException e) {
					return false;
				}
				i+=2;
				String nextval = data[i++];
				if (!nextval.equalsIgnoreCase("BADGES:")) {
					do {
						String[] parse = nextval.split(";");
						key_items.put(MemoryData.valueOf(parse[0]), Integer.parseInt(parse[1]));
						//System.out.println("Added "+Arrays.toString(parse));
						nextval = data[i++];
					}
					while (!nextval.equalsIgnoreCase("BADGES:"));
				}
				nextval = data[i++];
				if (!nextval.equalsIgnoreCase("UPDATES:")) {
					do {
						String[] parse = nextval.split(";");
						badges.put(MemoryData.valueOf(parse[0]), Integer.parseInt(parse[1]));
						//System.out.println("Added "+Arrays.toString(parse));
						nextval = data[i++];
					}
					while (!nextval.equalsIgnoreCase("UPDATES:"));
				}
				nextval = data[i++];

				eventStruct = nextval;
				nextval = data[i++];
				map = Integer.parseInt(nextval);
				nextval = data[i++];
				//System.out.println(nextval);
				syncing = Boolean.parseBoolean(nextval);
				lastWebUpdate = System.currentTimeMillis();
				FileUtils.logToFile("["+System.currentTimeMillis()+"]Sync completed syccessfully.", "debug.log");
				return true;
			}
		}
		return false;
	}

	private String getDataString() {
		StringBuilder sb = new StringBuilder();
		appendData(sigIRC.nickname,sb);
		appendData(avatar.value,sb);
		appendData(RabiRaceModule.CLIENT_SERVER_READTIME,sb);
		appendData(playtime,sb);
		appendData(healthUps,sb);
		appendData(manaUps,sb);
		appendData(regenUps,sb);
		appendData(packUps,sb);
		appendData(attackUps,sb);
		appendData(rainbowEggs,sb);
		appendData(isPaused,sb);
		appendData(difficulty,sb);
		appendData(loop,sb);
		appendData(itempct,sb);
		appendData(mappct,sb);
		appendData(0,sb); 
		appendData("KEYITEMS:",sb);
		for (MemoryData data : key_items.keySet()) {
			Integer val = key_items.get(data);
			appendData(data.name()+";"+val,sb);
		}
		appendData("BADGES:",sb);
		for (MemoryData data : badges.keySet()) {
			Integer val = badges.get(data);
			appendData(data.name()+";"+val,sb);
		}
		appendData("UPDATES:",sb);
		appendData(eventStruct,sb); 
		appendData(map,sb);
		appendData(syncing,sb);
		appendData("END",sb);
		return sb.toString();
	}
	
	private void appendData(Object data, StringBuilder str) {
		if (str.length()!=0) {
			str.append(",");
		}
		str.append(data);
	}

	public static Avatar GetSeededAvatar(String username) {
		//System.out.println(RabiRaceModule.mySession.getPlayers());
		Random r = new Random();
		r.setSeed(username.toLowerCase().hashCode());
		int randomnumb = r.nextInt(28);
		return Avatar.getAvatarFromID(randomnumb);
	}
	
	public Image getStatText(int w, Session session) {
		
		if (statUpdateCacheImage==null || stat_update_required) {
			BufferedImage tmp = new BufferedImage(400,175,BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = tmp.createGraphics();
			
			g2.setColor(Color.BLACK);
			//g2.fillRect(1, 1, 32, 32);
			g2.drawImage(avatar.getAvatarImage(), 1, 1, sigIRC.panel);
			g2.setColor(ScrollingText.GetUserNameColor(displayName));
			DrawUtils.drawOutlineText(g2, sigIRC.panel.rabiRibiMoneyDisplayFont, 54, 26, 0, 1, g2.getColor(), Color.BLACK, displayName);
			DrawUtils.drawCenteredOutlineText(g2, sigIRC.panel.rabiRibiTinyDisplayFont, (int)(tmp.getWidth()*0.2), 50, 0, 1, GetDifficultyColor(), Color.BLACK, GetDifficultyName());
			String text = TextUtils.convertSecondsToTimeFormat(playtime/60);
			if (!syncing) {
				g2.setColor(new Color(196,24,24));
			} else 
			if (isPaused) {
				g2.setColor(new Color(128,96,0));
			} else {
				g2.setColor(Color.BLACK);
			}
			DrawUtils.drawOutlineText(g2, sigIRC.panel.rabiRibiMoneyDisplayFont, (int)(tmp.getWidth() - TextUtils.calculateStringBoundsFont(text, sigIRC.panel.rabiRibiMoneyDisplayFont).getWidth()) - 2, 16, 0, 1, g2.getColor(), Color.GRAY, text);
			text = "Map "+df.format(mappct)+"%  Item "+df.format(itempct)+"%";
			//DrawUtils.drawOutlineText(g2, sigIRC.panel.rabiRibiMoneyDisplayFont, (int)(parent.position.getWidth() - TextUtils.calculateStringBoundsFont(text, sigIRC.panel.rabiRibiMoneyDisplayFont).getWidth()) - 2, 16, 1, g2.getColor(), Color.GRAY, text);
			DrawUtils.drawCenteredOutlineText(g2, sigIRC.panel.rabiRibiTinyDisplayFont, (int)(tmp.getWidth()*0.6), 50, 0, 2, Color.WHITE, Color.BLACK, text);
			text = ColorLocation.getLocationName(map);
			Rectangle2D siz = TextUtils.calculateStringBoundsFont(text, sigIRC.panel.rabiRibiTinyDisplayFont);
			DrawUtils.drawOutlineText(g2, sigIRC.panel.rabiRibiTinyDisplayFont, (int)(tmp.getWidth()-siz.getWidth()-6), 30, 0, 2, ColorLocation.getColor(map), Color.BLACK, text);
			
			statUpdateCacheImage = tmp.getScaledInstance(w, -1, Image.SCALE_AREA_AVERAGING);
			//stat_update_required = false;
		}
		
		return statUpdateCacheImage;
	}
	
	private Color GetDifficultyColor() {
		Color[] color_list = new Color[]{
				new Color(99, 159, 255),
				new Color(119, 98, 255),
				new Color(60, 201, 112),
				new Color(200, 209, 100),
				new Color(209, 159, 12),
				new Color(209, 54, 11),
				new Color(68, 24, 12),
		};
		Color colorval = Color.BLACK;
		if (difficulty<color_list.length) {
			colorval = color_list[difficulty];
		} else {
			colorval = color_list[color_list.length-1];
		}
		return colorval;
	}

	private String GetDifficultyName() {
		String[] difficulty_list = new String[]{
				"Casual",
				"Novice",
				"Normal",
				"Hard",
				"Hell",
				"BEX",
				"???",
		};
		String diffstring = "";
		if (difficulty<difficulty_list.length) {
			diffstring = difficulty_list[difficulty]+((loop>1)?" Loop "+loop:"");
		} else {
			diffstring = difficulty_list[difficulty_list.length-1]+((loop>1)?" Loop "+loop:"");
		}
		return diffstring;
	}

	public Image getStatPanel(int w, Session session) {
		
		if (imageDisplayUpdateImage==null || image_display_update_required) {
			//DrawUtils.drawTextFont(g, sigIRC.panel.userFont, parent.position.getX(), parent.position.getY()+26, Color.BLACK, "Values: "+readIntFromMemory(MemoryOffset.DLC_ITEM1)+","+readIntFromMemory(MemoryOffset.DLC_ITEM2)+","+readIntFromMemory(MemoryOffset.DLC_ITEM3)+","+readIntFromMemory(MemoryOffset.DLC_ITEM4));
			BufferedImage tmp = new BufferedImage(400,175,BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = tmp.createGraphics();
			final int border=20;
			final int width=(int)(tmp.getWidth()-border*2);
			int spacing=width/5;
			int shiftyval = 0;
			double iconsize = 1;
			final int icon_size = 24;
			int rainbowEggLimit = 0;
			
			int gamemode = -1;
			if (RabiRaceModule.mySession!=null) {
				gamemode = RabiRaceModule.mySession.gamemode;
			}
			
			if (gamemode!=-1) {
				switch (gamemode) {
					case 0:{ //Egg Hunt.
						if (session.rainbowEggGoal>0) {
							spacing = width/session.rainbowEggGoal;
							rainbowEggLimit = session.rainbowEggGoal;
						} else {
							spacing = width/5;
							rainbowEggLimit = session.rainbowEggGoal;
						}
						Image img = RabiRaceModule.image_map.get("easter_egg.png");
						if (rainbowEggLimit>10) {
							Color col = RabiRaceModule.rainbowcycler.getCycleColor();
							Rectangle2D siz = TextUtils.calculateStringBoundsFont("x "+GetRainbowEggCount(this)+" / "+session.rainbowEggGoal, sigIRC.panel.rabiRibiMoneyDisplayFont);
							DrawUtils.drawImage(g2, img, (int)(border+spacing*3-siz.getX()),(int)(36),col,sigIRC.panel);
							DrawUtils.drawOutlineText(g2, sigIRC.panel.rabiRibiMoneyDisplayFont,  (border+spacing*3+img.getWidth(sigIRC.panel)*1.25), (36+img.getHeight(sigIRC.panel)/2),
									1,Color.WHITE,Color.BLACK,"x "+GetRainbowEggCount(this)+" / "+session.rainbowEggGoal);
						} else {
							for (int i=0;i<session.rainbowEggGoal;i++) {
								Color col = (GetRainbowEggCount(this)>i)?RabiRaceModule.rainbowcycler.getCycleColor():new Color(0,0,0,192);
								DrawUtils.drawImage(g2, img, (int)(border+i*spacing-img.getWidth(sigIRC.panel)/4),(int)(36),col,sigIRC.panel);
							}
						}
					}break;
					case 1:{ //Item Hunt.
						spacing = width/session.itemHuntData.length;
						for (int i=0;i<session.itemHuntData.length;i++) {
							MemoryData item = MemoryData.valueOf(session.itemHuntData[i]);
							if ((key_items.containsKey(item) &&
									key_items.get(item)>=1) ||
									(badges.containsKey(item) &&
									badges.get(item)>=1)) {
								//DrawUtils.drawImage(g2, item.getImage(), (int)(border+i*spacing-item.getImage().getWidth(sigIRC.panel)/4),(int)(36),col,sigIRC.panel);
								g2.drawImage(item.getImage(), (int)(border+i*spacing),(int)(36+16), icon_size*2, icon_size*2, sigIRC.panel);
							} else {
								DrawUtils.drawImageScaled(g2, item.getImage(), (int)(border+i*spacing),(int)(36+16),icon_size*2, icon_size*2,new Color(0,0,0,192),sigIRC.panel);
							}
						}
						spacing=width/5;
					}break;
				}
			} else {
				shiftyval = -RabiRaceModule.image_map.get("easter_egg.png").getWidth(sigIRC.panel)/2;
				iconsize = 1;
			}
			/*
			 {
				Image img = RabiRaceModule.image_map.get("easter_egg.png");
				Color col = RabiRaceModule.rainbowcycler.getCycleColor();
				DrawUtils.drawImage(g2, img, (int)(border+((1.5)*spacing)-img.getWidth(sigIRC.panel)/4),(int)(36),col,sigIRC.panel);
				DrawUtils.drawCenteredOutlineText(g2, sigIRC.panel.programFont, (int)(border+((3)*spacing)-img.getWidth(sigIRC.panel)/4),(int)12+img.getHeight(sigIRC.panel), 1, Color.WHITE, Color.BLUE,"x"+rainbowEggCount);
			}
			 */
			int size = key_items.size();
			int count = 0;
			try {
				for (MemoryData data : key_items.keySet()) {
					if (key_items.get(data)<0) {
						Image img = data.getImage().getScaledInstance(icon_size, icon_size, Image.SCALE_DEFAULT);
						if (size*icon_size<width) {
							DrawUtils.drawImageScaled(g2, img, (int)(+border+((count++)*icon_size)), (int)(+96+8)+shiftyval, (int)icon_size*iconsize, (int)icon_size*iconsize, new Color(0,0,0,128), sigIRC.panel);
						} else {
							DrawUtils.drawImageScaled(g2, img, (int)(+border+((width/size)*(count++))), (int)(+96+8)+shiftyval, (int)icon_size*iconsize, (int)icon_size*iconsize, new Color(0,0,0,128), sigIRC.panel);
						}
					} else {
						if (size*icon_size<width) {
							g2.drawImage(data.getImage(), (int)(+border+((count++)*icon_size)), (int)(+96+8)+shiftyval, (int)(icon_size*iconsize), (int)(icon_size*iconsize), sigIRC.panel);
						} else {
							g2.drawImage(data.getImage(), (int)(+border+((width/size)*(count++))), (int)(+96+8)+shiftyval, (int)(icon_size*iconsize), (int)(icon_size*iconsize), sigIRC.panel);
						}
					}
				}
				count=0;
				size = badges.size();
				for (MemoryData data : badges.keySet()) {
					if (size*icon_size<width) {
						g2.drawImage(data.getImage(), (int)(+border+((count++)*icon_size)), (int)(+96+32)+shiftyval, (int)(icon_size*iconsize), (int)(icon_size*iconsize), sigIRC.panel);
						if (badges.get(data)==2) {
							DrawUtils.drawOutlineText(g2, sigIRC.panel.smallFont, (int)(+border+((count-1)*icon_size))+4, (int)(+96+32)+icon_size+shiftyval, 1, Color.WHITE, TEAL, "E");
						}
					} else {
						g2.drawImage(data.getImage(), (int)(+border+((width/size)*(count++))), (int)(+96+32)+shiftyval, (int)(icon_size*iconsize), (int)(icon_size*iconsize), sigIRC.panel);
						if (badges.get(data)==2) {
							DrawUtils.drawOutlineText(g2, sigIRC.panel.smallFont, (int)(+border+((width/size)*(count-1)))+4, (int)(+96+32)+icon_size+shiftyval, 1, Color.WHITE, TEAL, "E");
						}
					}
				}
				int i=0;
				Image[] imgs = new Image[]{RabiRaceModule.image_map.get("health_up.png"),
						RabiRaceModule.image_map.get("mana_up.png"),
						RabiRaceModule.image_map.get("regen_up.png"),
						RabiRaceModule.image_map.get("pack_up.png"),
						RabiRaceModule.image_map.get("attack_up.png")};
				int[] amts = new int[]{
						GetHealthUpCount(this),
						GetManaUpCount(this),
						GetRegenUpCount(this),
						GetPackUpCount(this),
						GetAttackUpCount(this),
				};
				spacing=width/6;
				if (GetRainbowEggCount(this)>rainbowEggLimit) {
					imgs = Arrays.copyOf(imgs, imgs.length+1);
					imgs[imgs.length-1] = RabiRaceModule.image_map.get("easter_egg.png");
					amts = Arrays.copyOf(amts, amts.length+1);
					amts[amts.length-1] = GetRainbowEggCount(this);
					spacing = width/6;
				}
				//g2.drawImage(RabiRaceModule.image_map.get("bunny_strike.png"),(int)(+border+(i++)*(spacing)-img2.getWidth(sigIRC.panel)/4),(int)(+96+56), (int)icon_size, (int)icon_size, sigIRC.panel);
				int counting=0;
				for (Image img : imgs) {
					if (counting++==5) {
						DrawUtils.drawImageScaled(g2, img,(int)(+border+((i)*(spacing))-icon_size/2),(int)(+96+56)+shiftyval, (int)icon_size, (int)icon_size, RabiRaceModule.rainbowcycler.getCycleColor(), sigIRC.panel);
					} else {
						g2.drawImage(img,(int)(+border+((i)*(spacing))-icon_size/2),(int)(+96+56)+shiftyval, (int)icon_size, (int)icon_size, sigIRC.panel);
					}
					DrawUtils.drawCenteredOutlineText(g2, sigIRC.panel.rabiRibiMoneyDisplayFont, (int)((+border+((i)*(spacing))-icon_size/2)+(spacing/2)+4), (int)(+96+56+icon_size)+shiftyval, 0, 1, Color.WHITE, Color.BLUE, Integer.toString(amts[i++]));
				}
			} catch (ConcurrentModificationException e) {
				
			}
			imageDisplayUpdateImage = tmp.getScaledInstance(w, -1, Image.SCALE_AREA_AVERAGING);
			//g.drawImage(tmp, (int)parent.position.getX(), (int)parent.position.getY(), 120, 64, sigIRC.panel);
			//image_display_update_required=false;
			//System.out.println("Updated Image Display.");
		}
		
		return imageDisplayUpdateImage;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getName()+"(");
		boolean first=false;
		for (Field f : this.getClass().getDeclaredFields()) {
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
		}
		sb.append(")");
		return sb.toString();
	}
	
	public static Point calculateMultiPanelView(int elements) {
		int x = 1;
		int y = 1;
		while (x*y<elements) {
			if (x==y) {
				y++;
			} else {
				x++;
			}
		}
		//System.out.println(x+","+y);
		return new Point(x,y);
	}

	public static void DrawMultiPanel(Graphics g, int x, int y, int w, Session session) {
		List<Profile> players = session.getPlayers();
		if (RabiRaceModule.mySession!=null && session.id==RabiRaceModule.mySession.id) {
			for (int i=0;i<players.size();i++) {
				Profile p = players.get(i);
				if (p.username.equalsIgnoreCase(RabiRaceModule.module.myProfile.username)) {
					players.remove(i--);
				}
			}
		}
		int cols = calculateMultiPanelView(players.size()).x;
		int rows = calculateMultiPanelView(players.size()).y;
		
		int xx = 0;
		int yy = 0;
		
		for (Profile p : players) {
			Image panel = p.getStatPanel(w,session);
			Image panel2 = p.getStatText(w,session);
			g.drawImage(panel,(int)(x+xx*panel.getWidth(sigIRC.panel)/((rows+cols)/2d)),(int)(y+yy*panel.getHeight(sigIRC.panel)/((rows+cols)/2d)),(int)(panel.getWidth(sigIRC.panel)/((rows+cols)/2d)),(int)(panel.getHeight(sigIRC.panel)/((rows+cols)/2d)),sigIRC.panel);
			g.drawImage(panel2,(int)(x+xx*panel2.getWidth(sigIRC.panel)/((rows+cols)/2d)),(int)(y+yy*panel2.getHeight(sigIRC.panel)/((rows+cols)/2d)),(int)(panel2.getWidth(sigIRC.panel)/((rows+cols)/2d)),(int)(panel2.getHeight(sigIRC.panel)/((rows+cols)/2d)),sigIRC.panel);
			if (xx+1<cols) {
				xx++;
			} else {
				yy++;
				xx=0;
			}
		}
	}
}
