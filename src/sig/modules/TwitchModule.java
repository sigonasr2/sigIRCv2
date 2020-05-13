package sig.modules;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sig.Module;
import sig.sigIRC;
import sig.modules.Twitch.Announcement;
import sig.modules.Twitch.FancyNumber;
import sig.modules.Twitch.Follower;
import sig.utils.DrawUtils;
import sig.utils.FileUtils;
import sig.utils.SoundUtils;
import sig.utils.TextUtils;
import sig.utils.TimeUtils;

public class TwitchModule extends Module{
	public String console="Twitch module goes here.";
	final public static String USERDIR = sigIRC.BASEDIR+"sigIRC/users/"; 
	final public static String SOUNDSDIR = sigIRC.BASEDIR+"sigIRC/follower_sounds/"; 
	final public static String FOLLOWERQUEUEFILE = USERDIR+"followers.txt";
	public static boolean streamOnline = false;
	static BufferedImage follower_img; 
	BufferedImage followerUserLogo;
	List<Announcement> follower_queue = new ArrayList<Announcement>();
	final static int FOLLOWERCHECKTIMER = 900;
	int lastFollowerCheck=300;
	final static int FOLLOWERANNOUNCEMENTTIME = 300;
	int lastFollowerAnnouncement=0;
	//User announcedFollowerUser;
	String[] followersounds = new String[]{"Glaceon_cry.wav"};
	FancyNumber viewers_numb;
	FancyNumber followers_numb;
	FancyNumber views_numb;
	ZonedDateTime uptime;
	String currentlyPlaying=" ";
	final public static int ARROWTIMER = 3000;
	public static BufferedImage UPARROWIMAGE;
	public static BufferedImage DOWNARROWIMAGE;
	public static BufferedImage UPTIMEIMAGE;
	public static long myTwitchChannelID = 0;

	public TwitchModule(Rectangle2D bounds, String moduleName) {
		this(bounds,moduleName,true);
	}

	public TwitchModule(Rectangle2D bounds, String moduleName, boolean enabled) {
		super(bounds, moduleName, enabled);
		Initialize();
	}
	
	private void Initialize() {
		boolean firstTime = false;
		InitializeImages();
		InitializeStatistics();
		InitializeFollowerSounds();
		firstTime = CreateUserFolder();
		if (firstTime) {
			CreateFollowerQueueLog();
		}
		ClearFollowerAnnouncerQueue();
		
		
		//myTwitchChannelID = getMyChannelID();
		
		/*manager.streams().get("theduckishot", new StreamResponseHandler() {

			@Override
			public void onFailure(Throwable arg0) {
			}

			@Override
			public void onFailure(int arg0, String arg1, String arg2) {
				System.out.println(arg0+","+arg1+","+arg2);
			}

			@Override
			public void onSuccess(Stream arg0) {
				//System.out.println("Stream data is available! "+arg0);
				if (arg0==null) {
					System.out.println("Stream is offline.");
				} else {
					System.out.println("Stream is online.");
				}
			}
			
		});*/
		/*manager.channels().getFollows(TextUtils.getActualChannelName(), new ChannelFollowsResponseHandler() {
				@Override
				public void onSuccess(int total, java.util.List<ChannelFollow> follows) {
					//System.out.println("Successfully found followers for channel "+sigIRC.channel+". Total: "+total);
					//console = "Last Follower: "+follows.get(0).getUser().getDisplayName();
					follows.get(0).
				}

				@Override
				public void onFailure(Throwable arg0) {
				}

				@Override
				public void onFailure(int arg0, String arg1, String arg2) {
					System.out.println(arg0+","+arg1+","+arg2);
				}
			}
		);*/
		/*SwingUtilities.invokeLater(new Runnable() {
            public void run() {
		System.out.println("Follower Queue Size: "+follower_queue.size()+", Contents: "+follower_queue);
            }
		});*/
	}
	
	public static void loadModule() {
		sigIRC.modules.add(new TwitchModule(
				new Rectangle(sigIRC.twitchmodule_X,sigIRC.twitchmodule_Y,sigIRC.twitchmodule_width,sigIRC.twitchmodule_height),
				"Twitch"
				));
		sigIRC.twitchmodule_enabled=true;
	}
	public static void unloadModule() {
		for (int i=0;i<sigIRC.modules.size();i++) {
			if (sigIRC.modules.get(i) instanceof TwitchModule) {
				sigIRC.modules.remove(sigIRC.modules.get(i));
			}
		}
		sigIRC.twitchmodule_enabled=false;
	}

	private void InitializeFollowerSounds() {
		File follower_sounds_dir = new File(SOUNDSDIR);
		String[] files = filterFiles(follower_sounds_dir.list());
		followersounds = files;
		//System.out.println(Arrays.toString(followersounds));
	}

	private static String[] filterFiles(String[] files) {
		List<String> finallist = new ArrayList<String>();
		for (String file : files) {
			if (!file.equalsIgnoreCase("README.txt")) {
				File f = new File(SOUNDSDIR+file);
				if (!f.isDirectory()) {
					finallist.add(file);
				}
			}
		}
		return finallist.toArray(new String[finallist.size()]);
	}

	private void InitializeStatistics() {
		viewers_numb = new FancyNumber("icon_viewers_count.png",0);
		views_numb = new FancyNumber("icon_views_count.png",0);
		followers_numb = new FancyNumber("icon_follower_count.png",0);
	}

	public void run() {
		if (lastFollowerCheck--<=0) {
			lastFollowerCheck = FOLLOWERCHECKTIMER;
			//getFollowers(false);
			try {
				GetFollowerAndStreamData();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			popFollowerFromQueue();
			try {
				//System.out.println("Checking Followers...");
				JSONObject FollowerData = GetFollowerAndStreamData();
				followers_numb.updateValue(FollowerData.getInt("total"));
				//views_numb.updateValue(FollowerData.get);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
//			isStreamOnline(true);
		}
		if (lastFollowerAnnouncement>0) {
			lastFollowerAnnouncement--;
		}
		//System.out.println(lastFollowerCheck);
	}

		
		public JSONObject GetFollowerAndStreamData() throws IOException {
			FileUtils.downloadFileFromUrl("https://api.twitch.tv/helix/streams?user_id="+sigIRC.channel_id, "stream_info",true);
		JSONObject streamInfo = FileUtils.readJsonFromFile("stream_info");
		JSONArray streamData = streamInfo.getJSONArray("data");
		streamOnline = streamData.length()!=0;
		if (streamOnline) {
			JSONObject stream = streamData.getJSONObject(0);
			DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
	                .ofPattern("uuuu-MM-dd'T'HH:mm:ssz");
			uptime = ZonedDateTime.parse(stream.getString("started_at"),DATE_TIME_FORMATTER);
			viewers_numb.updateValue(stream.getInt("viewer_count"));
		}
		FileUtils.downloadFileFromUrl("https://api.twitch.tv/helix/users/follows?to_id="+sigIRC.channel_id, "temp_followers",true);
		JSONObject FollowerData = FileUtils.readJsonFromFile("temp_followers");
		JSONArray data = FollowerData.getJSONArray("data");
		FileUtils.downloadFileFromUrl("https://api.twitch.tv/helix/users?id="+sigIRC.channel_id, "channel_info",true);
		JSONObject channelData = FileUtils.readJsonFromFile("channel_info").getJSONArray("data").getJSONObject(0);
		views_numb.updateValue(channelData.getInt("view_count"));
		for (int i=0;i<data.length();i++) {
			JSONObject user = data.getJSONObject(i);
			addFollower(user.getString("from_name"),user.getLong("from_id"),streamOnline,false);
		}
		return FollowerData;
	}
	
	public void ApplyConfigWindowProperties() {
		sigIRC.twitchmodule_X=(int)position.getX();
		sigIRC.twitchmodule_Y=(int)position.getY();
		sigIRC.config.setInteger("TWITCH_module_X", sigIRC.twitchmodule_X);
		sigIRC.config.setInteger("TWITCH_module_Y", sigIRC.twitchmodule_Y);
	}

	private void popFollowerFromQueue() {
		if (sigIRC.testMode) {
			Follower user = new Follower("0","I am an awesome test subject.",
					new Date().toString(),
					"Test User"+((int)Math.random()*100000),
					"http://45.33.13.215/sigIRCv2/sigIRC/sigIRCicon.png",
					"Test User",
					"",
					new Date().toString());
			DisplayFollowerAnnouncement(user,true);
		} else
		if (follower_queue.size()>0) {
			if (streamOnline) {
				//We have a follower to announce!
				Announcement a = follower_queue.remove(0);
				Follower user = a.getUser();
				if (user.display_name.length()>0 &&
						!user.display_name.contains("?")) {
					DisplayFollowerAnnouncement(user,true);
				} else {
					DisplayFollowerAnnouncement(user,false);
				}
			} else {
				//Store away all remaining followers in the queue....We can't announce them right now.
				StoreRemainingFollowers();
			}
		}
	}
	
	public void windowClosed(WindowEvent ev) {
		StoreRemainingFollowers();
	}

	private void StoreRemainingFollowers() {
		for (Announcement a : follower_queue) {
			FileUtils.logToFile(Long.toString(a.getUser().id), FOLLOWERQUEUEFILE);
		}
		follower_queue.clear();
	}


	public static Follower announcedFollowerUser;
	
	private void DisplayFollowerAnnouncement(Follower user, boolean useNickname) {
		lastFollowerAnnouncement = FOLLOWERANNOUNCEMENTTIME;
		if (!useNickname) {
			user.display_name=user.name;
		}
		announcedFollowerUser = user;
		String followerAnnouncement = user.display_name+" is now following the stream!";
		String userlogo = USERDIR+user.id+"_logo";
		if (user.logo_url!=null && !user.logo_url.equalsIgnoreCase("null")) {
			try {
				org.apache.commons.io.FileUtils.copyURLToFile(new URL(user.logo_url),new File(userlogo));
				File logo = new File(userlogo);
				if (logo.exists()) {
					followerUserLogo = ImageIO.read(logo);
				} else {
					followerUserLogo=null;
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			followerUserLogo = null;
		}
		LogMessageToFile(followerAnnouncement);
		System.out.println(followerAnnouncement);
		SoundUtils.playSound(SOUNDSDIR+followersounds[(int)(Math.random()*followersounds.length)]);
	}

	private void InitializeImages() {
		try {
			follower_img = ImageIO.read(new File(sigIRC.BASEDIR+sigIRC.twitchmodule_follower_img));
			UPARROWIMAGE = ImageIO.read(new File(sigIRC.BASEDIR+"sigIRC/icon_up_arrow.png"));
			DOWNARROWIMAGE = ImageIO.read(new File(sigIRC.BASEDIR+"sigIRC/icon_down_arrow.png"));
			UPTIMEIMAGE = ImageIO.read(new File(sigIRC.BASEDIR+"sigIRC/icon_uptime.png"));
			//System.out.println("Initialized Follower Image successfully.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void CreateFollowerQueueLog() {
		String dir = FOLLOWERQUEUEFILE;
		File filer = new File(dir);
		if (!filer.exists()) {
			try {
				filer.createNewFile();
				System.out.println("Follower Queue Log does not exist. Creating in "+USERDIR+".");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
		
	private void ClearFollowerAnnouncerQueue() {
		String[] contents = FileUtils.readFromFile(FOLLOWERQUEUEFILE);
		//System.out.println("Contents: "+Arrays.toString(contents));
		FileUtils.deleteFile(FOLLOWERQUEUEFILE);
		for (String s : contents) {
			if (s.length()>0) {
				int twitchID = Integer.parseInt(s);
				AddFollowerToQueue(twitchID);
			} 
		}
	}

	protected void addFollower(String username, long id, boolean streamOnline, boolean silent) {
		String filename = USERDIR + Long.toString(id);
		File userProfile = new File(filename);
		if (!userProfile.exists()) {
			CreateUserProfile(username, id, filename, userProfile);
			if (!silent) { //If we got in here, this isn't the initial follower setup, so we are good to go with announcing these followers.
				if (!streamOnline) {
					//Save their ID to a queue.
					FileUtils.logToFile(Long.toString(id), FOLLOWERQUEUEFILE);
				} else {
					//Announce it now.
					//System.out.println("Stream is online...");
					AnnounceFollower(username,id);
				}
			}
		}
	}

	private void CreateUserProfile(String username, long id, String filename, File userProfile) {
		try {
			FileUtils.downloadFileFromUrl("https://api.twitch.tv/helix/users?id="+id, "user_info",true);
			System.out.println("File data: "+Arrays.toString(FileUtils.readFromFile("user_info")));
			JSONObject userdata = FileUtils.readJsonFromFile("user_info").getJSONArray("data").getJSONObject(0);
			userProfile.createNewFile();
			System.out.println("Profile Image URL: "+userdata.getString("profile_image_url"));
			FileUtils.logToFile(new Date().toString(), filename);
			FileUtils.logToFile(userdata.getString("description"), filename);
			FileUtils.logToFile(username, filename);
			FileUtils.logToFile(userdata.getString("profile_image_url"), filename);
			FileUtils.logToFile(userdata.getString("login"), filename);
			FileUtils.logToFile(userdata.getString("type"), filename);
			FileUtils.logToFile(new Date().toString(), filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void AnnounceFollower(String name,long id) {
		System.out.println("Thanks for following "+name+"!");
		AddFollowerToQueue(id);
	}

	private void AddFollowerToQueue(long id) {
		follower_queue.add(new Announcement(id));
	}

//	private void AddFollowerToQueue(User user) {
//		follower_queue.add(new Announcement(user));
//	}

	private boolean CreateUserFolder() {
		File userDir = new File(USERDIR);
		if (!userDir.exists()) {
			userDir.mkdir();
			System.out.println("Could not find Twitch User directory. Creating in "+USERDIR+".");
			return true;
		} else {
			return false;
		}
	}

	private void LogMessageToFile(String message) {
		Calendar cal = Calendar.getInstance();
		FileUtils.logToFile(message, sigIRC.BASEDIR+"sigIRC/logs/log_"+(cal.get(Calendar.MONTH)+1)+"_"+cal.get(Calendar.DAY_OF_MONTH)+"_"+cal.get(Calendar.YEAR)+".txt",true);
	}

	public void draw(Graphics g){
		super.draw(g);
		//DrawUtils.drawText(g, bounds.getX(), bounds.getY()+24, Color.RED, console);
		DrawFollowerAnnouncement(g);
		if (streamOnline) {
			DrawStatisticsBar(g);
		}
	}

	private void DrawStatisticsBar(Graphics g) {
		g.setColor(new Color(25,25,25));
		int xoffset = (int)position.getX()+4;
		int yoffset = (int)(position.getY()+follower_img.getHeight()+sigIRC.twitchmodule_newfollowerImgLogoSize);
		g.fillPolygon(new int[]{(int)position.getX(),(int)(position.getX()+position.getWidth()),(int)(position.getX()+position.getWidth()),(int)position.getX()}, 
				new int[]{yoffset-4,yoffset-4,yoffset+16,yoffset+16}, 
				4);
		if (currentlyPlaying!=null && currentlyPlaying.length()>0) {
			DrawUtils.drawOutlineText(g, sigIRC.panel.userFont, xoffset, yoffset+TextUtils.calculateStringBoundsFont(currentlyPlaying, sigIRC.panel.userFont).getHeight()/2+3, 2, g.getColor(), new Color(195,195,195), currentlyPlaying);xoffset+=TextUtils.calculateStringBoundsFont(currentlyPlaying, sigIRC.panel.userFont).getWidth()+16;
		}
		Rectangle offsets = DrawUptime(g, xoffset, yoffset);xoffset+=offsets.getWidth();
		offsets = views_numb.draw(g, xoffset, yoffset);xoffset+=offsets.getWidth();
		offsets = followers_numb.draw(g, xoffset, yoffset);xoffset+=offsets.getWidth();
		offsets = viewers_numb.draw(g, xoffset, yoffset);xoffset+=offsets.getWidth();
	}

	private Rectangle DrawUptime(Graphics g, int x, int y) {
		int xoffset = 0;
		int yoffset = 0;
		g.drawImage(UPTIMEIMAGE, x+xoffset, y+yoffset-2, sigIRC.panel);xoffset+=UPTIMEIMAGE.getWidth()+4;
		String timediff = TimeUtils.GetTimeDifferenceFromCurrentDate(Date.from(uptime.toInstant()));
		if (timediff.length()>0) {
			DrawUtils.drawTextFont(g, sigIRC.panel.userFont, x+xoffset, y+yoffset+TextUtils.calculateStringBoundsFont(timediff, sigIRC.panel.userFont).getHeight()/2+3,new Color(184,181,192),timediff);xoffset+=TextUtils.calculateStringBoundsFont(timediff, sigIRC.panel.userFont).getWidth()+12;
		}
		yoffset+=16;
		return new Rectangle(x,y,xoffset,yoffset);
	}

	private void DrawFollowerAnnouncement(Graphics g) {
		if (lastFollowerAnnouncement>0) {
			final int ticksPassed = FOLLOWERANNOUNCEMENTTIME-lastFollowerAnnouncement;
			final int yAlteration = 
					(sigIRC.twitchmodule_follower_img_animation) 
					? Math.max(0,(int)(200-(ticksPassed*(200d/30))))
					: 0;
			final int canvasYOffset = 
					(sigIRC.twitchmodule_follower_img_animation) 
					? Math.min(follower_img.getHeight(),yAlteration)
					: 0;
			final int xAlteration = 
					(sigIRC.twitchmodule_follower_img_animation) 
					? (ticksPassed>=270)?(int)(-((ticksPassed-270)*(500d/30))):0
					: 0;
			final int canvasXOffset = 
					(sigIRC.twitchmodule_follower_img_animation) 
					? Math.min(follower_img.getWidth(),xAlteration)
					: 0;
			//System.out.println(yAlteration);
			//g.drawImage(follower_img, (int)bounds.getX()+xAlteration, (int)bounds.getY()+yAlteration, sigIRC.panel);
			//g.drawImage(follower_img, (int)bounds.getX(), (int)bounds.getY(), , , sigIRC.panel)
			g.drawImage(follower_img, (int)position.getX(), (int)position.getY()+canvasYOffset, (int)position.getX()+follower_img.getWidth()+canvasXOffset, (int)position.getY()+follower_img.getHeight(),
					-xAlteration, 0, follower_img.getWidth(), follower_img.getHeight()-yAlteration, sigIRC.panel);
			Rectangle2D usernameTextsize = TextUtils.calculateStringBoundsFont(announcedFollowerUser.display_name, sigIRC.panel.programFont);
			int textY = (int)position.getY()+sigIRC.twitchmodule_followerText_Y+yAlteration;
			int textX = (int)position.getX()+sigIRC.twitchmodule_followerText_centerX+xAlteration;
			if (textY<position.getY()+position.getHeight() && textX+usernameTextsize.getWidth()>position.getX()) {
				//DrawUtils.drawCenteredText(g, sigIRC.panel.programFont, (int)position.getX()+sigIRC.twitchmodule_followerText_centerX+xAlteration, (int)position.getY()+sigIRC.twitchmodule_followerText_Y+yAlteration, Color.BLACK, announcedFollowerUser.display_name);
				DrawUtils.drawCenteredOutlineText(g, sigIRC.panel.programFont, (int)position.getX()+sigIRC.twitchmodule_followerText_centerX+xAlteration, (int)position.getY()+sigIRC.twitchmodule_followerText_Y+yAlteration, sigIRC.twitchmodule_newfollowerNameShadowSize, TextUtils.convertStringToColor(sigIRC.twitchmodule_newfollowerNameTextColor), TextUtils.convertStringToColor(sigIRC.twitchmodule_newfollowerNameShadowColor), announcedFollowerUser.display_name);
			}
			if (announcedFollowerUser.bio!=null && !announcedFollowerUser.bio.equalsIgnoreCase("null")) {
				if (followerUserLogo!=null) {
					final int image_size = sigIRC.twitchmodule_newfollowerImgLogoSize;
					int img_startx = (int)(position.getX()+position.getWidth()-ticksPassed*3-(image_size+4));
					int img_starty = (int)(position.getY()+follower_img.getHeight()+2-image_size/2);
					g.setColor(Color.BLACK);
					g.drawRect(img_startx, img_starty, image_size, image_size);
					g.drawImage(followerUserLogo, img_startx, img_starty, img_startx+image_size, img_starty+image_size, 0, 0, followerUserLogo.getWidth(), followerUserLogo.getHeight(), TextUtils.convertStringToColor(sigIRC.twitchmodule_newfollowerImgBackgroundColor), sigIRC.panel);
				}
				if (announcedFollowerUser.bio!=null && announcedFollowerUser.bio.length()>0) {
					DrawUtils.drawOutlineText(g, sigIRC.panel.userFont, position.getX()+position.getWidth()-ticksPassed*3, position.getY()+follower_img.getHeight()+2+8, 2, TextUtils.convertStringToColor(sigIRC.twitchmodule_newfollowerTextColor), TextUtils.convertStringToColor(sigIRC.twitchmodule_newfollowerShadowTextColor), announcedFollowerUser.bio);
				}
			}
		}
	}
}
