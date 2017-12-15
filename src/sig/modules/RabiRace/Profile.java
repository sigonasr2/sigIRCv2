package sig.modules.RabiRace;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
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
import sig.utils.DrawUtils;
import sig.utils.FileUtils;
import sig.utils.TextUtils;

public class Profile {
	public String username = sigIRC.nickname.toLowerCase();
	public String displayName = sigIRC.nickname;
	public Avatar avatar;
	public int playtime = 0;
	public int healthUps = 0;
	public int attackUps = 0;
	public int manaUps = 0;
	public int regenUps = 0;
	public int packUps = 0;
	public int rainbowEggCount = 0;
	public boolean isPaused = false;
	public int difficulty = 0;
	public int loop = 0;
	public float itempct = 0;
	public float mappct = 0;
	public LinkedHashMap<MemoryData,Integer> key_items = new LinkedHashMap<MemoryData,Integer>();
	public LinkedHashMap<MemoryData,Integer> badges = new LinkedHashMap<MemoryData,Integer>();
	public List<String> updates = new ArrayList<String>();
	RabiRaceModule parent;
	public long lastWebUpdate = System.currentTimeMillis(); 
	DecimalFormat df = new DecimalFormat("0.0");
	public Profile oldProfile;
	public boolean isArchive = false;
	final static Color TEAL = new Color(0,128,128);
	public Image statUpdateCacheImage;
	public Image imageDisplayUpdateImage;
	public boolean stat_update_required = true;
	public boolean image_display_update_required = true;
	public int avatarval = 0;
	public static Image rainbowEggImage;
	public static Image darkRainbowEggImage;
	
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
		oldProfile.rainbowEggCount = rainbowEggCount;
		oldProfile.key_items = (LinkedHashMap<MemoryData, Integer>)key_items.clone();
		oldProfile.badges = (LinkedHashMap<MemoryData, Integer>)badges.clone();
		oldProfile.playtime = playtime;
		oldProfile.difficulty = difficulty;
		oldProfile.loop = loop;
		oldProfile.itempct = itempct;
		oldProfile.mappct = mappct;
	}

	public int compareAllChangedStatValues() {
		int count=0;
		if (oldProfile.playtime/60!=playtime/60 ||
				oldProfile.avatarval!=avatar.value ||
				oldProfile.isPaused!=isPaused ||
				oldProfile.difficulty!=difficulty ||
				oldProfile.loop!=loop ||
				oldProfile.itempct!=itempct ||
				oldProfile.mappct!=mappct) {
			System.out.println("Something has changed.");
			count++;
		} else {
			System.out.println("All values are the same... "+oldProfile.playtime+","+playtime+"|"
					+oldProfile.avatarval+","+avatar.value+"|"+oldProfile.isPaused+","+isPaused+"|"
					+oldProfile.difficulty+","+difficulty+"|"+oldProfile.loop+","+loop+"|"
					+oldProfile.itempct+","+itempct+"|"+oldProfile.mappct+","+mappct+"|");
		}
		return count;
	}
	
	public int compareAllChangedValues() {
		int count=0;
		if (oldProfile.healthUps!=healthUps) {
			count++;
			//System.out.println("Health ups changed. "+(healthUps-oldProfile.healthUps));
		}
		if (oldProfile.attackUps!=attackUps) {
			count++;
			//System.out.println("Attack ups changed. "+(attackUps-oldProfile.attackUps));
		}
		if (oldProfile.manaUps!=manaUps) {
			count++;
			//System.out.println("Mana ups changed. "+(manaUps-oldProfile.manaUps));
		}
		if (oldProfile.regenUps!=regenUps) {
			count++;
			//System.out.println("Regen ups changed. "+(regenUps-oldProfile.regenUps));
		}
		if (oldProfile.packUps!=packUps) {
			count++;
			//System.out.println("Pack ups changed. "+(packUps-oldProfile.packUps));
		}
		if (oldProfile.rainbowEggCount!=rainbowEggCount) {
			//System.out.println("Rainbow Egg Count changed. "+(rainbowEggCount-oldProfile.rainbowEggCount));
			count++;
		}
		for (MemoryData md : key_items.keySet()) {
			if ((!oldProfile.key_items.containsKey(md) &&
					key_items.containsKey(md)) || ((
							oldProfile.key_items.containsKey(md) &&
							key_items.containsKey(md)) &&
						oldProfile.key_items.get(md).compareTo(key_items.get(md))!=0)
					) {
				/*if (!oldProfile.key_items.containsKey(md) &&
					key_items.containsKey(md)) {
					System.out.println("Key item difference: "+(md));
				} else {
					System.out.println("Key item value difference: "+md+":"+(oldProfile.key_items.get(md)+"->"+key_items.get(md)));
				}*/
				count++;
			}
		}
		for (MemoryData md : badges.keySet()) {
			if ((!oldProfile.badges.containsKey(md) &&
					badges.containsKey(md)) || (
							oldProfile.badges.containsKey(md) &&
							badges.containsKey(md)) &&
						oldProfile.badges.get(md).compareTo(badges.get(md))!=0
					) {
				/*if (!oldProfile.badges.containsKey(md) &&
					badges.containsKey(md)) {
					System.out.println("Badge difference: "+(md));
				} else {
					System.out.println("Badge value difference: "+md+":"+(oldProfile.badges.get(md)+"->"+badges.get(md)));
				}*/
				count++;
			}
		}
		return count;
	}
	
	public void compareAndAnnounceAllChangedValues() {
		//System.out.println(oldProfile.key_items.get(MemoryData.HAMMER)+","+key_items.get(MemoryData.HAMMER));
		int changedValueCount = compareAllChangedValues();
		if (changedValueCount==0) {
			return;
		}
		String announcement = "";
		if (oldProfile.healthUps==healthUps-1) {
			announcement = "has obtained a Health Up! ("+healthUps+" total)";
		}
		if (oldProfile.attackUps==attackUps-1) {
			announcement = "has obtained an Attack Up! ("+attackUps+" total)";
		}
		if (oldProfile.manaUps==manaUps-1) {
			announcement = "has obtained a Mana Up! ("+manaUps+" total)";
		}
		if (oldProfile.regenUps==regenUps-1) {
			announcement = "has obtained a Regen Up! ("+regenUps+" total)";
		}
		if (oldProfile.packUps==packUps-1) {
			announcement = "has obtained a Pack Up! ("+packUps+" total)";
		}
		if (oldProfile.rainbowEggCount==rainbowEggCount-1) {
			if (RabiRaceModule.mySession!=null &&
					RabiRaceModule.mySession.gamemode==0 &&
					RabiRaceModule.mySession.eggCount>0) {
				if (RabiRaceModule.mySession.eggCount-rainbowEggCount==0) {
					announcement = "has obtained "+RabiRaceModule.mySession.eggCount+" Rainbow Eggs! (NAME) has completed the race!";
				} else if (RabiRaceModule.mySession.eggCount-rainbowEggCount>0)
				{
					announcement = "has obtained a Rainbow Egg! ("+Math.max(RabiRaceModule.mySession.eggCount-rainbowEggCount, 0)+" to go!)";
				}
			} else {
				announcement = "has obtained a Rainbow Egg! ("+rainbowEggCount+" total)";
			}
		}
		for (MemoryData md : key_items.keySet()) {
			if (!oldProfile.key_items.containsKey(md) &&
					key_items.containsKey(md)) {
				announcement = "has obtained "+md.name+"!";
			}
		}
		for (MemoryData md : badges.keySet()) {
			if (!oldProfile.badges.containsKey(md) &&
					badges.containsKey(md)) {
				announcement = "has obtained the "+md.name+" badge!";
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
			org.apache.commons.io.FileUtils.copyURLToFile(new URL("http://45.33.13.215/rabirace/send.php?key=addupdate&session="+RabiRaceModule.mySession.id+"&message="+string),file);
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
	}
	
	public void uploadProfile() {
		if (sigIRC.authenticated) {
			File file = new File(sigIRC.BASEDIR+"tmp");
			try {
				org.apache.commons.io.FileUtils.copyURLToFile(new URL("http://45.33.13.215/rabirace/send.php?key=playerdata&name="+sigIRC.nickname.toLowerCase()+"&data="+getDataString()),file);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean downloadProfile() {
		if (sigIRC.authenticated) {
			File file = new File(sigIRC.BASEDIR+"tmp_profile");
			try {
				org.apache.commons.io.FileUtils.copyURLToFile(new URL("http://45.33.13.215/rabirace/send.php?key=retrievedata&name="+username.toLowerCase()),file);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String[] data = FileUtils.readFromFile(sigIRC.BASEDIR+"tmp_profile");
			//System.out.println(Arrays.toString(data));
			if (data.length>=18) {
				int i=0;
				displayName = data[i++];
				avatar = Avatar.getAvatarFromID(Integer.parseInt(data[i++]));
				//System.out.println("Updated Avatar for Player "+displayName+" with Avatar "+avatar.displayName);
				playtime = Integer.parseInt(data[i++]);
				healthUps = Integer.parseInt(data[i++]);
				manaUps = Integer.parseInt(data[i++]);
				regenUps = Integer.parseInt(data[i++]);
				packUps = Integer.parseInt(data[i++]);
				attackUps = Integer.parseInt(data[i++]);
				rainbowEggCount = Integer.parseInt(data[i++]);
				isPaused = Boolean.parseBoolean(data[i++]);
				difficulty = Integer.parseInt(data[i++]);
				loop = Integer.parseInt(data[i++]);
				itempct = Float.parseFloat(data[i++]);
				mappct = Float.parseFloat(data[i++]);
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
				lastWebUpdate = System.currentTimeMillis();
				return true;
			}
		}
		return false;
	}

	private String getDataString() {
		StringBuilder sb = new StringBuilder();
		appendData(sigIRC.nickname,sb);
		appendData(avatar.value,sb);
		appendData(playtime,sb);
		appendData(healthUps,sb);
		appendData(manaUps,sb);
		appendData(regenUps,sb);
		appendData(packUps,sb);
		appendData(attackUps,sb);
		appendData(rainbowEggCount,sb);
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
		return sb.toString();
	}
	
	private void appendData(Object data, StringBuilder str) {
		if (str.length()!=0) {
			str.append(",");
		}
		str.append(data);
	}

	public static Avatar GetSeededAvatar(String username) {
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
			g2.drawImage(avatar.getAvatarImage(), 1, 1, parent.panel);
			g2.setColor(ScrollingText.GetUserNameColor(displayName));
			DrawUtils.drawOutlineText(g2, sigIRC.rabiRibiMoneyDisplayFont, 54, 26, 1, g2.getColor(), Color.BLACK, displayName);
			DrawUtils.drawCenteredOutlineText(g2, sigIRC.rabiRibiTinyDisplayFont, (int)(tmp.getWidth()*0.2), 50, 1, GetDifficultyColor(), Color.BLACK, GetDifficultyName());
			String text = TextUtils.convertSecondsToTimeFormat(playtime/60);
			if (isPaused) {
				g2.setColor(new Color(128,96,0));
			} else {
				g2.setColor(Color.BLACK);
			}
			DrawUtils.drawOutlineText(g2, sigIRC.rabiRibiMoneyDisplayFont, (int)(tmp.getWidth() - TextUtils.calculateStringBoundsFont(text, sigIRC.rabiRibiMoneyDisplayFont).getWidth()) - 2, 16, 1, g2.getColor(), Color.GRAY, text);
			text = "Map "+df.format(mappct)+"%  Item "+df.format(itempct)+"%";
			//DrawUtils.drawOutlineText(g2, sigIRC.panel.rabiRibiMoneyDisplayFont, (int)(parent.position.getWidth() - TextUtils.calculateStringBoundsFont(text, sigIRC.panel.rabiRibiMoneyDisplayFont).getWidth()) - 2, 16, 1, g2.getColor(), Color.GRAY, text);
			DrawUtils.drawCenteredOutlineText(g2, sigIRC.rabiRibiTinyDisplayFont, (int)(tmp.getWidth()*0.6), 50, 2, Color.WHITE, Color.BLACK, text);
			
			if (statUpdateCacheImage!=null) {
				statUpdateCacheImage.flush();
			}
			g2.dispose();
			statUpdateCacheImage = tmp.getScaledInstance(w, -1, Image.SCALE_AREA_AVERAGING);
			stat_update_required = false;
			System.out.println("Updated stat text for user "+username+".");
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
	
	public Image getRainbowEggPanel(int w, Session session) {
		BufferedImage tmp = new BufferedImage(400,175,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = tmp.createGraphics();
		if (Profile.darkRainbowEggImage==null) {
			Profile.darkRainbowEggImage = DrawUtils.getBlendedImage(g2, RabiRaceModule.image_map.get("easter_egg.png"), new Color(0,0,0,192), RabiRaceModule.window);
			Profile.rainbowEggImage = DrawUtils.getBlendedImage(g2, RabiRaceModule.image_map.get("easter_egg.png"), RabiRaceModule.rainbowcycler.getCycleColor(), RabiRaceModule.window);
		} else {
			Profile.rainbowEggImage.flush();
			Profile.rainbowEggImage = DrawUtils.getBlendedImage(g2, RabiRaceModule.image_map.get("easter_egg.png"), RabiRaceModule.rainbowcycler.getCycleColor(), RabiRaceModule.window);
			//System.out.println("Rainbow egg color is "+RabiRaceModule.rainbowcycler.getCycleColor());
		}
		//DrawUtils.drawTextFont(g, sigIRC.panel.userFont, parent.position.getX(), parent.position.getY()+26, Color.BLACK, "Values: "+readIntFromMemory(MemoryOffset.DLC_ITEM1)+","+readIntFromMemory(MemoryOffset.DLC_ITEM2)+","+readIntFromMemory(MemoryOffset.DLC_ITEM3)+","+readIntFromMemory(MemoryOffset.DLC_ITEM4));
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
					if (session.eggCount>0) {
						spacing = width/session.eggCount;
						rainbowEggLimit = session.eggCount;
					} else {
						spacing = width/5;
						rainbowEggLimit = session.eggCount;
					}
					//Image img = RabiRaceModule.image_map.get("easter_egg.png");
					for (int i=0;i<session.eggCount;i++) {
						//Color col = (rainbowEggCount>i)?RabiRaceModule.rainbowcycler.getCycleColor():new Color(0,0,0,192);
						//DrawUtils.drawImage(g2, img, (int)(border+i*spacing-img.getWidth(parent.panel)/4),(int)(36),col,parent.panel);
						Image img;
						if (rainbowEggCount>i) {
							img = Profile.rainbowEggImage;
						} else {
							img = Profile.darkRainbowEggImage;
						}
						g2.drawImage(img, (int)(border+i*spacing-img.getWidth(parent.panel)/4),(int)(36), parent.panel);
					}
				}break;
			}
		} else {
			shiftyval = -RabiRaceModule.image_map.get("easter_egg.png").getWidth(parent.panel)/2;
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
			int i=0;
			Image[] imgs = new Image[]{RabiRaceModule.image_map.get("health_up.png"),
					RabiRaceModule.image_map.get("mana_up.png"),
					RabiRaceModule.image_map.get("regen_up.png"),
					RabiRaceModule.image_map.get("pack_up.png"),
					RabiRaceModule.image_map.get("attack_up.png")};
			int[] amts = new int[]{
					healthUps,
					manaUps,
					regenUps,
					packUps,
					attackUps,
			};
			if (rainbowEggCount>rainbowEggLimit) {
				imgs = Arrays.copyOf(imgs, imgs.length+1);
				imgs[imgs.length-1] = RabiRaceModule.image_map.get("easter_egg.png");
				amts = Arrays.copyOf(amts, amts.length+1);
				amts[amts.length-1] = rainbowEggCount;
				spacing = width/6;
			}
			//g2.drawImage(RabiRaceModule.image_map.get("bunny_strike.png"),(int)(+border+(i++)*(spacing)-img2.getWidth(sigIRC.panel)/4),(int)(+96+56), (int)icon_size, (int)icon_size, sigIRC.panel);
			int counting=0;
			for (Image img : imgs) {
				if (counting++==5) {
					g2.drawImage(img, (int)(+border+((i)*(spacing))-icon_size/2),(int)(+96+56)+shiftyval,(int)icon_size,(int)icon_size,parent.panel);
					//DrawUtils.drawImageScaled(g2, img,(int)(+border+((i)*(spacing))-icon_size/2),(int)(+96+56)+shiftyval, (int)icon_size, (int)icon_size, RabiRaceModule.rainbowcycler.getCycleColor(), parent.panel);
				}
				DrawUtils.drawCenteredOutlineText(g2, sigIRC.programFont, (int)((+border+((i)*(spacing))-icon_size/2)+(spacing/2)+4), (int)(+96+56+icon_size+12)+shiftyval, 1, Color.WHITE, Color.BLUE, Integer.toString(amts[i++]));
			}
		} catch (ConcurrentModificationException e) {
			
		}
		g2.dispose();
		//g.drawImage(tmp, (int)parent.position.getX(), (int)parent.position.getY(), 120, 64, sigIRC.panel);
		//image_display_update_required=false;
		//System.out.println("Updated Image Display for user "+username+".");
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
						if (session.eggCount>0) {
							spacing = width/session.eggCount;
							rainbowEggLimit = session.eggCount;
						} else {
							spacing = width/5;
							rainbowEggLimit = session.eggCount;
						}
						//Image img = RabiRaceModule.image_map.get("easter_egg.png");
						for (int i=0;i<session.eggCount;i++) {
							//Color col = (rainbowEggCount>i)?RabiRaceModule.rainbowcycler.getCycleColor():new Color(0,0,0,192);
							//DrawUtils.drawImage(g2, img, (int)(border+i*spacing-img.getWidth(parent.panel)/4),(int)(36),col,parent.panel);
							Image img;
							if (rainbowEggCount>i) {
								img = Profile.rainbowEggImage;
							} else {
								img = Profile.darkRainbowEggImage;
							}
							g2.drawImage(img, (int)(border+i*spacing-img.getWidth(parent.panel)/4),(int)(36), parent.panel);
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
								g2.drawImage(item.getImage(), (int)(border+i*spacing),(int)(36+16), icon_size*2, icon_size*2, parent.panel);
							} else {
								DrawUtils.drawImageScaled(g2, item.getImage(), (int)(border+i*spacing),(int)(36+16),icon_size*2, icon_size*2,new Color(0,0,0,192),parent.panel);
							}
						}
						spacing=width/5;
					}break;
				}
			} else {
				shiftyval = -RabiRaceModule.image_map.get("easter_egg.png").getWidth(parent.panel)/2;
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
							DrawUtils.drawImageScaled(g2, img, (int)(+border+((count++)*icon_size)), (int)(+96+8)+shiftyval, (int)icon_size*iconsize, (int)icon_size*iconsize, new Color(0,0,0,128), parent.panel);
						} else {
							DrawUtils.drawImageScaled(g2, img, (int)(+border+((width/size)*(count++))), (int)(+96+8)+shiftyval, (int)icon_size*iconsize, (int)icon_size*iconsize, new Color(0,0,0,128), parent.panel);
						}
					} else {
						if (size*icon_size<width) {
							g2.drawImage(data.getImage(), (int)(+border+((count++)*icon_size)), (int)(+96+8)+shiftyval, (int)(icon_size*iconsize), (int)(icon_size*iconsize), parent.panel);
						} else {
							g2.drawImage(data.getImage(), (int)(+border+((width/size)*(count++))), (int)(+96+8)+shiftyval, (int)(icon_size*iconsize), (int)(icon_size*iconsize), parent.panel);
						}
					}
				}
				count=0;
				size = badges.size();
				for (MemoryData data : badges.keySet()) {
					if (size*icon_size<width) {
						g2.drawImage(data.getImage(), (int)(+border+((count++)*icon_size)), (int)(+96+32)+shiftyval, (int)(icon_size*iconsize), (int)(icon_size*iconsize), parent.panel);
						if (badges.get(data)==2) {
							DrawUtils.drawOutlineText(g2, sigIRC.smallFont, (int)(+border+((count-1)*icon_size))+4, (int)(+96+32)+icon_size+shiftyval, 1, Color.WHITE, TEAL, "E");
						}
					} else {
						g2.drawImage(data.getImage(), (int)(+border+((width/size)*(count++))), (int)(+96+32)+shiftyval, (int)(icon_size*iconsize), (int)(icon_size*iconsize), parent.panel);
						if (badges.get(data)==2) {
							DrawUtils.drawOutlineText(g2, sigIRC.smallFont, (int)(+border+((width/size)*(count-1)))+4, (int)(+96+32)+icon_size+shiftyval, 1, Color.WHITE, TEAL, "E");
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
						healthUps,
						manaUps,
						regenUps,
						packUps,
						attackUps,
				};
				if (rainbowEggCount>rainbowEggLimit) {
					imgs = Arrays.copyOf(imgs, imgs.length+1);
					imgs[imgs.length-1] = RabiRaceModule.image_map.get("easter_egg.png");
					amts = Arrays.copyOf(amts, amts.length+1);
					amts[amts.length-1] = rainbowEggCount;
					spacing = width/6;
				}
				//g2.drawImage(RabiRaceModule.image_map.get("bunny_strike.png"),(int)(+border+(i++)*(spacing)-img2.getWidth(sigIRC.panel)/4),(int)(+96+56), (int)icon_size, (int)icon_size, sigIRC.panel);
				int counting=0;
				for (Image img : imgs) {
					if (counting++==5) {
						g2.drawImage(img, (int)(+border+((i)*(spacing))-icon_size/2),(int)(+96+56)+shiftyval,(int)icon_size,(int)icon_size,parent.panel);
						//DrawUtils.drawImageScaled(g2, img,(int)(+border+((i)*(spacing))-icon_size/2),(int)(+96+56)+shiftyval, (int)icon_size, (int)icon_size, RabiRaceModule.rainbowcycler.getCycleColor(), parent.panel);
					} else {
						g2.drawImage(img,(int)(+border+((i)*(spacing))-icon_size/2),(int)(+96+56)+shiftyval, (int)icon_size, (int)icon_size, parent.panel);
					}
					DrawUtils.drawCenteredOutlineText(g2, sigIRC.programFont, (int)((+border+((i)*(spacing))-icon_size/2)+(spacing/2)+4), (int)(+96+56+icon_size+12)+shiftyval, 1, Color.WHITE, Color.BLUE, Integer.toString(amts[i++]));
				}
			} catch (ConcurrentModificationException e) {
				
			}
			if (imageDisplayUpdateImage!=null) {
				imageDisplayUpdateImage.flush();
			}
			imageDisplayUpdateImage = tmp.getScaledInstance(w, -1, Image.SCALE_AREA_AVERAGING);
			//g.drawImage(tmp, (int)parent.position.getX(), (int)parent.position.getY(), 120, 64, sigIRC.panel);
			image_display_update_required=false;
			System.out.println("Updated Image Display for user "+username+".");
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
			g.drawImage(panel,(int)(x+xx*panel.getWidth(RabiRaceModule.module.panel)/((rows+cols)/2d)),(int)(y+yy*panel.getHeight(RabiRaceModule.module.panel)/((rows+cols)/2d)),(int)(panel.getWidth(RabiRaceModule.module.panel)/((rows+cols)/2d)),(int)(panel.getHeight(RabiRaceModule.module.panel)/((rows+cols)/2d)),RabiRaceModule.module.panel);
			g.drawImage(panel2,(int)(x+xx*panel2.getWidth(RabiRaceModule.module.panel)/((rows+cols)/2d)),(int)(y+yy*panel2.getHeight(RabiRaceModule.module.panel)/((rows+cols)/2d)),(int)(panel2.getWidth(RabiRaceModule.module.panel)/((rows+cols)/2d)),(int)(panel2.getHeight(RabiRaceModule.module.panel)/((rows+cols)/2d)),RabiRaceModule.module.panel);
			if (xx+1<cols) {
				xx++;
			} else {
				yy++;
				xx=0;
			}
		}
	}
}
