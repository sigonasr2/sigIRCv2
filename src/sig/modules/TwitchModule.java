package sig.modules;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.handlers.ChannelFollowsResponseHandler;
import com.mb3364.twitch.api.handlers.StreamResponseHandler;
import com.mb3364.twitch.api.models.ChannelFollow;
import com.mb3364.twitch.api.models.Stream;
import com.mb3364.twitch.api.models.User;

import sig.Module;
import sig.sigIRC;
import sig.modules.Twitch.Announcement;
import sig.utils.DrawUtils;
import sig.utils.FileUtils;
import sig.utils.SoundUtils;
import sig.utils.TextUtils;

public class TwitchModule extends Module{
	public String console="Twitch module goes here.";
	Twitch manager = new Twitch();
	final public static String USERDIR = sigIRC.BASEDIR+"sigIRC/users/"; 
	final public static String SOUNDSDIR = sigIRC.BASEDIR+"sigIRC/sounds/"; 
	final public static String FOLLOWERQUEUEFILE = USERDIR+"followers.txt";
	public static boolean streamOnline = false;
	static BufferedImage follower_img; 
	BufferedImage followerUserLogo;
	List<Announcement> follower_queue = new ArrayList<Announcement>();
	final static int FOLLOWERCHECKTIMER = 900;
	int lastFollowerCheck=300;
	final static int FOLLOWERANNOUNCEMENTTIME = 300;
	int lastFollowerAnnouncement=0;
	User announcedFollowerUser;
	String[] followersounds = new String[]{"Pokemon Cries - #471 Glaceon.wav"};

	public TwitchModule(Rectangle2D bounds, String moduleName) {
		this(bounds,moduleName,true);
	}

	public TwitchModule(Rectangle2D bounds, String moduleName, boolean enabled) {
		super(bounds, moduleName, enabled);
		Initialize();
	}
	
	private void Initialize() {
		boolean firstTime = false;
		InitializeFollowerImage();
		firstTime = CreateUserFolder();
		if (firstTime) {
			CreateFollowerQueueLog();
		}
		manager.setClientId("o4c2x0l3e82scgar4hpxg6m5dfjbem");
		getFollowers(firstTime);
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
	
	public void run() {
		if (lastFollowerCheck--<=0) {
			lastFollowerCheck = FOLLOWERCHECKTIMER;
			getFollowers(false);
			popFollowerFromQueue();
		}
		if (lastFollowerAnnouncement>0) {
			lastFollowerAnnouncement--;
		}
		//System.out.println(lastFollowerCheck);
	}
	
	public void ApplyConfigWindowProperties() {
		sigIRC.twitchmodule_X=(int)bounds.getX();
		sigIRC.twitchmodule_Y=(int)bounds.getY();
		sigIRC.config.setInteger("TWITCH_module_X", sigIRC.twitchmodule_X);
		sigIRC.config.setInteger("TWITCH_module_Y", sigIRC.twitchmodule_Y);
	}

	private void popFollowerFromQueue() {
		if (follower_queue.size()>0) {
			if (isStreamOnline()) {
				//We have a follower to announce!
				Announcement a = follower_queue.remove(0);
				User user = a.getUser();
				if (user.getDisplayName().length()>0 &&
						!user.getDisplayName().contains("?")) {
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
			FileUtils.logToFile(Long.toString(a.getUser().getId()), FOLLOWERQUEUEFILE);
		}
		follower_queue.clear();
	}

	private void DisplayFollowerAnnouncement(User user, boolean useNickname) {
		lastFollowerAnnouncement = FOLLOWERANNOUNCEMENTTIME;
		if (!useNickname) {
			user.setDisplayName(user.getName());
		}
		announcedFollowerUser = user;
		String followerAnnouncement = user.getDisplayName()+" is now following the stream!";
		String userlogo = USERDIR+user.getId()+"_logo";
		if (!user.getLogo().equalsIgnoreCase("null")) {
			try {
				org.apache.commons.io.FileUtils.copyURLToFile(new URL(user.getLogo()),new File(userlogo));
				followerUserLogo = ImageIO.read(new File(userlogo));
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

	private void InitializeFollowerImage() {
		try {
			follower_img = ImageIO.read(new File(sigIRC.BASEDIR+sigIRC.twitchmodule_follower_img));
			System.out.println("Initialized Follower Image successfully.");
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

	private void getFollowers(boolean firstTime) {
		manager.channels().getFollows(TextUtils.getActualChannelName(), new ChannelFollowsResponseHandler() {
					@Override
					public void onSuccess(int total, java.util.List<ChannelFollow> follows) {
						//System.out.println("Successfully found followers for channel "+sigIRC.channel+". Total: "+total);
						//console = "Last Follower: "+follows.get(0).getUser().getDisplayName();
						for (ChannelFollow f : follows) {
							addFollower(f,isStreamOnline(),firstTime);
						}
					}
		
					@Override
					public void onFailure(Throwable arg0) {
					}
		
					@Override
					public void onFailure(int arg0, String arg1, String arg2) {
						System.out.println(arg0+","+arg1+","+arg2);
					}
				}
			);
		if (isStreamOnline()) {/*System.out.println("Stream is Online...Clearing Follower Queue.");*/ClearFollowerAnnouncerQueue();}
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

	private boolean isStreamOnline() {
		manager.streams().get(TextUtils.getActualChannelName(), new StreamResponseHandler() {

			@Override
			public void onFailure(Throwable arg0) {
				TwitchModule.streamOnline=false;
			}

			@Override
			public void onFailure(int arg0, String arg1, String arg2) {
				System.out.println(arg0+","+arg1+","+arg2);
				TwitchModule.streamOnline=false;
			}

			@Override
			public void onSuccess(Stream arg0) {
				//System.out.println("Stream data is available! "+arg0);
				if (arg0==null) {
					TwitchModule.streamOnline=false;
				} else {
						TwitchModule.streamOnline=true;
				}
			}
			
		});
		return TwitchModule.streamOnline;
		//return false;
	}

	protected void addFollower(ChannelFollow f, boolean streamOnline, boolean silent) {
		String filename = USERDIR+f.getUser().getId();
		File userProfile = new File(filename);
		if (!userProfile.exists()) {
			if (!silent) { //If we got in here, this isn't the initial follower setup, so we are good to go with announcing these followers.
				if (!streamOnline) {
					//Save their ID to a queue.
					FileUtils.logToFile(Long.toString(f.getUser().getId()), FOLLOWERQUEUEFILE);
				} else {
					//Announce it now.
					//System.out.println("Stream is online...");
					AnnounceFollower(f);
				}
			}
			CreateUserProfile(f, filename, userProfile);
		}
	}

	private void CreateUserProfile(ChannelFollow f, String filename, File userProfile) {
		try {
			userProfile.createNewFile();
			FileUtils.logToFile(DateFormat.getDateInstance().format(f.getCreatedAt()), filename);
			FileUtils.logToFile(f.getUser().getBio(), filename);
			FileUtils.logToFile(f.getUser().getDisplayName(), filename);
			FileUtils.logToFile(f.getUser().getLogo(), filename);
			FileUtils.logToFile(f.getUser().getName(), filename);
			FileUtils.logToFile(f.getUser().getType(), filename);
			FileUtils.logToFile(DateFormat.getDateInstance().format(f.getUser().getUpdatedAt()), filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void AnnounceFollower(ChannelFollow f) {
		//System.out.println("Thanks for following "+f.getUser().getDisplayName()+"!");
		AddFollowerToQueue(f.getUser());
	}

	private void AddFollowerToQueue(int id) {
		follower_queue.add(new Announcement(id));
	}

	private void AddFollowerToQueue(User user) {
		follower_queue.add(new Announcement(user));
	}

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
		FileUtils.logToFile(message, sigIRC.BASEDIR+"sigIRC/logs/log_"+(cal.get(Calendar.MONTH)+1)+"_"+cal.get(Calendar.DAY_OF_MONTH)+"_"+cal.get(Calendar.YEAR)+".txt");
	}

	public void draw(Graphics g){
		super.draw(g);
		//DrawUtils.drawText(g, bounds.getX(), bounds.getY()+24, Color.RED, console);
		DrawFollowerAnnouncement(g);
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
			g.drawImage(follower_img, (int)bounds.getX(), (int)bounds.getY()+canvasYOffset, (int)bounds.getX()+follower_img.getWidth()+canvasXOffset, (int)bounds.getY()+follower_img.getHeight(),
					-xAlteration, 0, follower_img.getWidth(), follower_img.getHeight()-yAlteration, sigIRC.panel);
			Rectangle2D usernameTextsize = TextUtils.calculateStringBoundsFont(announcedFollowerUser.getDisplayName(), sigIRC.panel.programFont);
			int textY = (int)bounds.getY()+sigIRC.twitchmodule_followerText_Y+yAlteration;
			int textX = (int)bounds.getX()+sigIRC.twitchmodule_followerText_centerX+xAlteration;
			if (textY<bounds.getY()+bounds.getHeight() && textX+usernameTextsize.getWidth()>bounds.getX()) {
				DrawUtils.drawCenteredText(g, sigIRC.panel.programFont, (int)bounds.getX()+sigIRC.twitchmodule_followerText_centerX+xAlteration, (int)bounds.getY()+sigIRC.twitchmodule_followerText_Y+yAlteration, Color.BLACK, announcedFollowerUser.getDisplayName());	
			}
			if (!announcedFollowerUser.getBio().equalsIgnoreCase("null")) {
				if (followerUserLogo!=null) {
					final int image_size = sigIRC.twitchmodule_newfollowerImgLogoSize;
					int img_startx = (int)(bounds.getX()+bounds.getWidth()-ticksPassed*3-(image_size+4));
					int img_starty = (int)(bounds.getY()+follower_img.getHeight()+2-image_size/2);
					//g.setColor(Color.BLACK);
					//g.drawRect(img_startx, img_starty, image_size, image_size);
					g.drawImage(followerUserLogo, img_startx, img_starty, img_startx+image_size, img_starty+image_size, 0, 0, followerUserLogo.getWidth(), followerUserLogo.getHeight(), TextUtils.convertStringToColor(sigIRC.twitchmodule_newfollowerImgBackgroundColor), sigIRC.panel);
				}
				DrawUtils.drawOutlineText(g, sigIRC.panel.userFont, bounds.getX()+bounds.getWidth()-ticksPassed*3, bounds.getY()+follower_img.getHeight()+2+8, 2, TextUtils.convertStringToColor(sigIRC.twitchmodule_newfollowerTextColor), TextUtils.convertStringToColor(sigIRC.twitchmodule_newfollowerShadowTextColor), announcedFollowerUser.getBio());
			}
		}
	}
}