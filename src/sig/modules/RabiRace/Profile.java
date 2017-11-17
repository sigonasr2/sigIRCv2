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
import java.util.List;

import sig.ScrollingText;
import sig.sigIRC;
import sig.modules.RabiRaceModule;
import sig.utils.DrawUtils;
import sig.utils.FileUtils;
import sig.utils.TextUtils;

public class Profile {
	public String username = sigIRC.nickname.toLowerCase();
	public String displayName = sigIRC.nickname.toLowerCase();
	public int avatar = 0;
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
	public HashMap<MemoryData,Integer> key_items = new HashMap<MemoryData,Integer>();
	public HashMap<MemoryData,Integer> badges = new HashMap<MemoryData,Integer>();
	public List<String> updates = new ArrayList<String>();
	RabiRaceModule parent;
	public long lastWebUpdate = System.currentTimeMillis(); 
	DecimalFormat df = new DecimalFormat("00.0");
	
	public Profile(RabiRaceModule module) {
		this.parent = module;
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
	
	public void downloadProfile() {
		if (sigIRC.authenticated) {
			File file = new File(sigIRC.BASEDIR+"tmp");
			try {
				org.apache.commons.io.FileUtils.copyURLToFile(new URL("http://45.33.13.215/rabirace/send.php?key=retrievedata&name="+username.toLowerCase()),file);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String[] data = FileUtils.readFromFile(sigIRC.BASEDIR+"tmp");
			System.out.println(Arrays.toString(data));
			if (data.length>=18) {
				int i=0;
				displayName = data[i++];
				avatar = Integer.parseInt(data[i++]);
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
						System.out.println("Added "+Arrays.toString(parse));
						nextval = data[i++];
					}
					while (!nextval.equalsIgnoreCase("BADGES:"));
				}
				nextval = data[i++];
				if (!nextval.equalsIgnoreCase("UPDATES:")) {
					do {
						String[] parse = nextval.split(";");
						badges.put(MemoryData.valueOf(parse[0]), Integer.parseInt(parse[1]));
						System.out.println("Added "+Arrays.toString(parse));
						nextval = data[i++];
					}
					while (!nextval.equalsIgnoreCase("UPDATES:"));
				}
				lastWebUpdate = System.currentTimeMillis();
			}
		}
	}

	private String getDataString() {
		StringBuilder sb = new StringBuilder();
		appendData(sigIRC.nickname,sb);
		appendData(avatar,sb);
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
	
	public Image getStatText(int w) {
		BufferedImage tmp = new BufferedImage(400,175,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = tmp.createGraphics();
		
		g2.setColor(Color.BLACK);
		g2.fillRect(1, 1, 32, 32);
		g2.setColor(ScrollingText.GetUserNameColor(displayName));
		DrawUtils.drawOutlineText(g2, sigIRC.panel.rabiRibiMoneyDisplayFont, 36, 26, 1, g2.getColor(), Color.BLACK, displayName);
		String text = TextUtils.convertSecondsToTimeFormat(playtime/60);
		if (isPaused) {
			g2.setColor(new Color(128,96,0));
		} else {
			g2.setColor(Color.BLACK);
		}
		DrawUtils.drawOutlineText(g2, sigIRC.panel.rabiRibiMoneyDisplayFont, (int)(tmp.getWidth() - TextUtils.calculateStringBoundsFont(text, sigIRC.panel.rabiRibiMoneyDisplayFont).getWidth()) - 2, 16, 1, g2.getColor(), Color.GRAY, text);
		text = "Map "+df.format(mappct)+"%  Item "+df.format(itempct)+"%";
		//DrawUtils.drawOutlineText(g2, sigIRC.panel.rabiRibiMoneyDisplayFont, (int)(parent.position.getWidth() - TextUtils.calculateStringBoundsFont(text, sigIRC.panel.rabiRibiMoneyDisplayFont).getWidth()) - 2, 16, 1, g2.getColor(), Color.GRAY, text);
		DrawUtils.drawCenteredOutlineText(g2, sigIRC.panel.rabiRibiTinyDisplayFont, (int)(tmp.getWidth()*0.6), 50, 2, Color.WHITE, Color.BLACK, text);
		
		return tmp.getScaledInstance(w, -1, Image.SCALE_AREA_AVERAGING);
	}
	
	public Image getStatPanel(int w) {
		//DrawUtils.drawTextFont(g, sigIRC.panel.userFont, parent.position.getX(), parent.position.getY()+26, Color.BLACK, "Values: "+readIntFromMemory(MemoryOffset.DLC_ITEM1)+","+readIntFromMemory(MemoryOffset.DLC_ITEM2)+","+readIntFromMemory(MemoryOffset.DLC_ITEM3)+","+readIntFromMemory(MemoryOffset.DLC_ITEM4));
		BufferedImage tmp = new BufferedImage(400,175,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = tmp.createGraphics();
		final int border=20;
		final int width=(int)(tmp.getWidth()-border*2);
		final int spacing=width/5;
		for (int i=0;i<5;i++) {
			Image img = RabiRaceModule.image_map.get("easter_egg.png");
			Color col = (rainbowEggCount>i)?RabiRaceModule.rainbowcycler.getCycleColor():new Color(0,0,0,192);
			DrawUtils.drawImage(g2, img, (int)(border+i*spacing-img.getWidth(sigIRC.panel)/4),(int)(36),col,sigIRC.panel);
		}
		int size = key_items.size();
		final int icon_size = 24;
		int count = 0;
		try {
			for (MemoryData data : key_items.keySet()) {
				if (key_items.get(data)<0) {
					Image img = data.getImage().getScaledInstance(icon_size, icon_size, Image.SCALE_DEFAULT);
					if (size*icon_size<width) {
						DrawUtils.drawImage(g2, img, (int)(+border+((count++)*icon_size)), (int)(+96+8), new Color(0,0,0,128), sigIRC.panel);
					} else {
						DrawUtils.drawImage(g2, img, (int)(+border+((width/size)*(count++))), (int)(+96+8), new Color(0,0,0,128), sigIRC.panel);
					}
				} else {
					if (size*icon_size<width) {
						g2.drawImage(data.getImage(), (int)(+border+((count++)*icon_size)), (int)(+96+8), (int)icon_size, (int)icon_size, sigIRC.panel);
					} else {
						g2.drawImage(data.getImage(), (int)(+border+((width/size)*(count++))), (int)(+96+8), (int)icon_size, (int)icon_size, sigIRC.panel);
					}
				}
			}
			count=0;
			size = badges.size();
			for (MemoryData data : badges.keySet()) {
				if (size*icon_size<width) {
					g2.drawImage(data.getImage(), (int)(+border+((count++)*icon_size)), (int)(+96+32), (int)icon_size, (int)icon_size, sigIRC.panel);
				} else {
					g2.drawImage(data.getImage(), (int)(+border+((width/size)*(count++))), (int)(+96+32), (int)icon_size, (int)icon_size, sigIRC.panel);
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
			//g2.drawImage(RabiRaceModule.image_map.get("bunny_strike.png"),(int)(+border+(i++)*(spacing)-img2.getWidth(sigIRC.panel)/4),(int)(+96+56), (int)icon_size, (int)icon_size, sigIRC.panel);
			for (Image img : imgs) {
				g2.drawImage(img,(int)(+border+((i)*(spacing))-icon_size/2),(int)(+96+56), (int)icon_size, (int)icon_size, sigIRC.panel);
				DrawUtils.drawCenteredOutlineText(g2, sigIRC.panel.programFont, (int)((+border+((i)*(spacing))-icon_size/2)+(spacing/2)+4), (int)(+96+56+icon_size+12), 1, Color.WHITE, Color.BLUE, Integer.toString(amts[i++]));
			}
		} catch (ConcurrentModificationException e) {
			
		}
		return tmp.getScaledInstance(w, -1, Image.SCALE_AREA_AVERAGING);
		//g.drawImage(tmp, (int)parent.position.getX(), (int)parent.position.getY(), 120, 64, sigIRC.panel);
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

	public static void DrawMultiPanel(Graphics g, int x, int y, int w, List<Profile> players) {
		int cols = calculateMultiPanelView(players.size()).x;
		int rows = calculateMultiPanelView(players.size()).y;
		
		int xx = 0;
		int yy = 0;
		
		for (Profile p : players) {
			Image panel = p.getStatPanel(w);
			Image panel2 = p.getStatText(w);
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
