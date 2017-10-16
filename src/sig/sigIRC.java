package sig;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.handlers.ChannelResponseHandler;
import com.mb3364.twitch.api.handlers.StreamResponseHandler;
import com.mb3364.twitch.api.models.Channel;
import com.mb3364.twitch.api.models.Stream;

import sig.modules.ChatLogModule;
import sig.modules.TouhouMotherModule;
import sig.modules.TwitchModule;
import sig.modules.ChatLog.ChatLogMessage;
import sig.modules.ChatLog.ChatLogTwitchEmote;
import sig.utils.FileUtils;
import sig.utils.TextUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class sigIRC{
	public static MyPanel panel = null;
	public static ColorPanel colorpanel = null;
	public static List<ScrollingText> textobj = new ArrayList<ScrollingText>();
	public static List<TextRow> rowobj = new ArrayList<TextRow>();
	public static List<Emoticon> emoticons = new ArrayList<Emoticon>();
	public static List<Emoticon> emoticon_queue = new ArrayList<Emoticon>();
	public static List<TwitchEmote> twitchemoticons = new ArrayList<TwitchEmote>();
	public static List<ChatLogTwitchEmote> chatlogtwitchemoticons = new ArrayList<ChatLogTwitchEmote>();
	public static List<CustomSound> customsounds = new ArrayList<CustomSound>();
	public static List<Module> modules = new ArrayList<Module>();
	static UpdateEvent updater = new UpdateEvent();
	static Timer programClock = new Timer(32,updater);
	final public static int BASESCROLLSPD = 4;
	final public static int ROWSEPARATION = 64;
	final public static String BASEDIR = "./"; 
	final public static String WINDOWTITLE = "sigIRCv2"; 
	public static ConfigFile config;
	static String server;
	static String nickname;
	public static String channel;
	public static boolean authenticated=false;
	public static int lastPlayedDing=0;
	final public static int DINGTIMER=150;
	static boolean dingEnabled=true;
	static int dingThreshold;
	static Color backgroundcol;
	public static BackgroundColorButton button;
	public static JFrame window;
	static boolean overlayMode=false;
	static boolean showWindowControls=false;
	static int windowX=0;
	static int windowY=0;
	static int windowWidth=0;
	static int windowHeight=0;
	static int chatRows=3;
	static int chatScrollSpd=4;
	static int rowSpacing=64;
	static String messageFont="Gill Sans Ultra Bold Condensed";
	static String usernameFont="GillSansMTStd-Book";
	static String touhoumotherConsoleFont="Agency FB Bold";
	static boolean touhoumothermodule_enabled=false;
	static boolean twitchmodule_enabled=true;
	static boolean chatlogmodule_enabled=true;
	static boolean downloadsComplete=false;
	static boolean hardwareAcceleration=true;
	static boolean playedoAuthSoundOnce=false;
	public static int twitchmodule_width=500;
	public static int twitchmodule_height=200;
	public static int twitchmodule_X=320;
	public static int twitchmodule_Y=312;
	public static String twitchmodule_follower_img="sigIRC/glaceon_follower.png";
	public static boolean twitchmodule_follower_img_animation=true;
	public static int twitchmodule_followerText_centerX=292;
	public static int twitchmodule_followerText_Y=42;
	public static int touhoumothermodule_width=320;
	public static int touhoumothermodule_height=312;
	public static int touhoumothermodule_X=0;
	public static int touhoumothermodule_Y=312;
	public static int chatlogmodule_width=320;
	public static int chatlogmodule_height=312;
	public static int chatlogmodule_X=0;
	public static int chatlogmodule_Y=312;
	public static int chatlogMessageHistory=50;
	public static String twitchmodule_newfollowerImgBackgroundColor="90,90,90";
	public static String twitchmodule_newfollowerShadowTextColor="26,90,150";
	public static String twitchmodule_newfollowerTextColor="255,255,255";
	public static int twitchmodule_newfollowerImgLogoSize=32;
	public static boolean testMode=false;
	public final static String TWITCHEMOTEURL = "https://static-cdn.jtvnw.net/emoticons/v1/";
	public final static String SUBEMOTELISTFILE = "sigIRC/subemotes.json";
	public static long channel_id = -1;
	public static int lastSubEmoteUpdate = -1;
	
	static int lastWindowX = 0;
	static int lastWindowY = 0;

	public static Twitch manager = new Twitch();
	
	public static void main(String[] args) {
		config = InitializeConfigurationFile();
		
		server = config.getProperty("server");
		nickname = config.getProperty("nickname");
		channel = config.getProperty("channel");
		overlayMode = config.getBoolean("overlayMode", false);
		showWindowControls = config.getBoolean("showWindowControls", true);
		windowX = config.getInteger("windowX", 0);
		windowY = config.getInteger("windowY", 0);
		windowWidth = config.getInteger("windowWidth", (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth());
		windowHeight = config.getInteger("windowHeight", (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight());
		chatRows = config.getInteger("chatRows", 3);
		chatScrollSpd = config.getInteger("chatScrollSpd", 4);
		rowSpacing = config.getInteger("rowSpacing", 64);
		dingThreshold = Integer.parseInt(config.getProperty("dingThreshold"));
		backgroundcol = new Color(Integer.parseInt(config.getProperty("backgroundColor")));
		messageFont = config.getProperty("messageFont","Gill Sans Ultra Bold Condensed");
		usernameFont = config.getProperty("usernameFont","Gill Sans");
		touhoumotherConsoleFont = config.getProperty("touhoumotherConsoleFont","Agency FB Bold");
		touhoumothermodule_enabled = config.getBoolean("Module_touhoumother_Enabled",false);
		twitchmodule_enabled = config.getBoolean("Module_twitch_Enabled",true);
		chatlogmodule_enabled = config.getBoolean("Module_chatlog_Enabled",true);
		twitchmodule_width = config.getInteger("TWITCH_module_width",500);
		twitchmodule_height = config.getInteger("TWITCH_module_height",200);
		twitchmodule_follower_img = config.getProperty("TWITCH_module_follower_img","sigIRC/glaceon_follower.png");
		twitchmodule_follower_img_animation = config.getBoolean("TWITCH_module_follower_img_animation",true);
		twitchmodule_followerText_centerX = config.getInteger("TWITCH_module_followerText_centerX",292);
		twitchmodule_followerText_Y = config.getInteger("TWITCH_module_followerText_Y",42);
		twitchmodule_newfollowerImgLogoSize = config.getInteger("TWITCH_module_newFollowerImgLogoSize",32);
		twitchmodule_newfollowerImgBackgroundColor = config.getProperty("TWITCH_module_newFollowerImgBackgroundColor","90,90,90");
		twitchmodule_newfollowerShadowTextColor = config.getProperty("TWITCH_module_newFollowerShadowTextColor","26,90,150");
		twitchmodule_newfollowerTextColor = config.getProperty("TWITCH_module_newFollowerTextColor","255,255,255");
		twitchmodule_X = config.getInteger("TWITCH_module_X",320);
		twitchmodule_Y = config.getInteger("TWITCH_module_Y",312);
		testMode = config.getBoolean("Testing_Mode",false);
		touhoumothermodule_X = config.getInteger("TOUHOUMOTHER_module_X",0);
		touhoumothermodule_Y = config.getInteger("TOUHOUMOTHER_module_Y",312);
		touhoumothermodule_width = config.getInteger("TOUHOUMOTHER_module_width",320);
		touhoumothermodule_height = config.getInteger("TOUHOUMOTHER_module_height",312);
		chatlogmodule_X = config.getInteger("CHATLOG_module_X",0);
		chatlogmodule_Y = config.getInteger("CHATLOG_module_Y",312);
		chatlogmodule_width = config.getInteger("CHATLOG_module_width",320);
		chatlogmodule_height = config.getInteger("CHATLOG_module_height",312);
		chatlogMessageHistory = config.getInteger("CHATLOG_module_MessageHistory",50);
		hardwareAcceleration = config.getBoolean("hardware_acceleration",true);
		lastSubEmoteUpdate = config.getInteger("lastSubEmote_APIUpdate",Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
		manager.setClientId("o4c2x0l3e82scgar4hpxg6m5dfjbem");
		//System.out.println(manager.auth().hasAccessToken());

		DownloadAllRequiredDependencies();
		
		String[] filedata = FileUtils.readFromFile(BASEDIR+"sigIRC/oauthToken.txt");
		
		final String oauth = filedata[0];
		
		WriteBreakToLogFile();
		programClock.start();
		
		InitializeRows(chatRows);
		InitializeCustomSounds();
		
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                window = createAndShowGUI();
        		InitializeModules();
        		//System.out.println("Modules initialized.");
        		performTwitchEmoteUpdate();
        		//System.out.println("Twitch emote update done.");
        		downloadsComplete=true;
            }
        });
		InitializeIRCConnection(server, nickname, channel, oauth);
	}

	private static ConfigFile InitializeConfigurationFile() {
		ConfigFile.configureDefaultConfiguration();
		final String configname = "sigIRCv2.conf";
		File config = new File(BASEDIR+configname);
		ConfigFile conf = new ConfigFile(configname);
		if (!config.exists()) {
			ConfigFile.setAllDefaultProperties(conf);
			conf.saveProperties();
		}
		return conf; 
	}

	public static void DownloadAllRequiredDependencies() {
		FileManager manager = new FileManager("sigIRC/oauthToken.txt"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/Emotes/",true); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/Emotes/subscribers.txt"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/logs/",true); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/sounds/",true); manager.verifyAndFetchFileFromServer();
		//manager = new FileManager("sigIRC/sounds/Glaceon_cry.wav"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/follower_sounds/Glaceon_cry.wav"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/follower_sounds/README.txt"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/record"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/glaceon_follower.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/sigIRCicon.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/icon_down_arrow.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/icon_follower_count.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/icon_up_arrow.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/icon_uptime.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/icon_viewers_count.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/icon_views_count.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/message_separator.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("kill.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("memory"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("swap.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("update.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("backcolor.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("drag_bar.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("WSplits"); manager.verifyAndFetchFileFromServer();
		System.out.println("Downloaded Dependencies.");
	}

	private static void InitializeModules() {
		try {
			Module.IMG_DRAGBAR = ImageIO.read(new File(sigIRC.BASEDIR+"drag_bar.png"));
			Module.MSG_SEPARATOR = ImageIO.read(new File(sigIRC.BASEDIR+"sigIRC/message_separator.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (touhoumothermodule_enabled) {
			modules.add(new TouhouMotherModule(
					new Rectangle(touhoumothermodule_X,touhoumothermodule_Y,touhoumothermodule_width,touhoumothermodule_height),
					"Touhou Mother"
					));
		}
		if (twitchmodule_enabled) {
			modules.add(new TwitchModule(
					new Rectangle(twitchmodule_X,twitchmodule_Y,twitchmodule_width,twitchmodule_height),
					"Twitch"
					));
		}
		if (chatlogmodule_enabled) {
			modules.add(new ChatLogModule(
					new Rectangle(chatlogmodule_X,chatlogmodule_Y,chatlogmodule_width,chatlogmodule_height),
					"Chat Log"
					));
		}
	}

	private static void InitializeCustomSounds() {
		customsounds.add(new CustomSound("monkeyman5876", "Howler Monkeys Howling (Very Funny)-Pia8ku7jUNg.wav"));
		customsounds.add(new CustomSound("kuroplz", "Kuroyukihime Burst Link !-tv6wMw7KU9o.wav"));
		customsounds.add(new CustomSound("samusaran458", "Samus Appears - Metroid Prime [OST]-G8frLXCHtqM.wav"));
	}

	public static void InitializeIRCConnection(final String server,final String nickname,final String channel,final String oauth) {
		Socket socket;
		try {
			socket = new Socket(server, 6667);
	        BufferedWriter writer = new BufferedWriter(
	                new OutputStreamWriter(socket.getOutputStream( )));
	        BufferedReader reader = new BufferedReader(
	                new InputStreamReader(socket.getInputStream( )));
	        
	        // Log on to the server.
	        writer.write("PASS " + oauth + "\r\n");
	        writer.write("NICK " + nickname + "\r\n");
	        writer.flush( );
	        if (VerifyLogin(reader)) {
            	//panel.addMessage("We are now logged in.");
		        writer.write("JOIN " + channel + "\r\n");
		        writer.flush();
		        runIRCLoop(channel, writer, reader);
	        }
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        InitializeIRCConnection(server,nickname,channel,oauth);
	}

	public static void WriteBreakToLogFile() {
		Calendar cal = Calendar.getInstance();
		File file = new File(BASEDIR+"logs/log_"+(cal.get(Calendar.MONTH)+1)+"_"+cal.get(Calendar.DAY_OF_MONTH)+"_"+cal.get(Calendar.YEAR)+".txt");
		if (file.exists()) {
			FileUtils.logToFile("\n---------------------------\n", BASEDIR+"logs/log_"+(cal.get(Calendar.MONTH)+1)+"_"+cal.get(Calendar.DAY_OF_MONTH)+"_"+cal.get(Calendar.YEAR)+".txt");
		}
	}

	private static void downloadSubEmotes(JSONObject emotes, String channelName) {
		manager.channels().get(channelName, new ChannelResponseHandler() {

			@Override
			public void onFailure(Throwable arg0) {
			}

			@Override
			public void onFailure(int arg0, String arg1, String arg2) {
			}

			@Override
			public void onSuccess(Channel arg0) {
				if (arg0!=null) {
					channel_id=arg0.getId();
					JSONObject channel = emotes.getJSONObject(Long.toString(channel_id));
					JSONArray arr = channel.getJSONArray("emotes");
					//System.out.println("Channel: "+channel);
					for (int i=0;i<arr.length();i++) {
						JSONObject emote = arr.getJSONObject(i);
						int id = emote.getInt("id");
						String name = emote.getString("code");
						//System.out.println("Emote "+(i+1)+" has id "+id+" and code "+name+".");
						try {
							emoticon_queue.add(new SubEmoticon(name, new URL(TWITCHEMOTEURL+id+"/1.0"), channelName));
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}
					}
				}
			}

		});
		//TwitchModule.streamOnline=true;
		//return true;
	}

	private static void performTwitchEmoteUpdate() {
		try {
			JSONObject twitchemotes = FileUtils.readJsonFromUrl("https://twitchemotes.com/api_cache/v3/global.json");
			System.out.println("Twitch emote Json read.");
			for (String emotes : twitchemotes.keySet()) {
				JSONObject emote = twitchemotes.getJSONObject(emotes);
				int id = emote.getInt("id");
				String name = emote.getString("code");
				emoticons.add(new Emoticon(name, new URL(TWITCHEMOTEURL+id+"/1.0")));
				System.out.println("Emote "+id+" with name "+name);
			}
			Thread downloadThread = new Thread(){
				public void run() {
					JSONObject subemotes = null;
					try {
						File filer = new File(SUBEMOTELISTFILE);
						if (!filer.exists()) {
							System.out.println("Local copy of Sub emotes not found. Downloading in background...");
							subemotes = FileUtils.readJsonFromUrl("https://twitchemotes.com/api_cache/v3/subscriber.json",SUBEMOTELISTFILE,true);
						} else {
							if (lastSubEmoteUpdate == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
								System.out.println("Using local copy of Sub emote JSON.");
								subemotes = FileUtils.readJsonFromFile(SUBEMOTELISTFILE);
							} else {
								System.out.println("Local copy of Sub emote JSON out-of-date! Re-downloading in background...");
								subemotes = FileUtils.readJsonFromFile(SUBEMOTELISTFILE);
								new Thread(){
									public void run() {
										try {
											FileUtils.readJsonFromUrl("https://twitchemotes.com/api_cache/v3/subscriber.json",SUBEMOTELISTFILE,true);
										} catch (JSONException e) {
											e.printStackTrace();
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								}.start();
							}
						}
					} catch (JSONException | IOException e) {
						e.printStackTrace();
					}
					lastSubEmoteUpdate = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
					config.setInteger("lastSubEmote_APIUpdate", lastSubEmoteUpdate);
					//System.out.println("Subscriber object: "+subemotes);
					String[] sub_emotes = FileUtils.readFromFile(sigIRC.BASEDIR+"sigIRC/Emotes/subscribers.txt");
					//System.out.println("Sub emotes read.");
					for (String s : sub_emotes) {
						if (s.length()>0) {
							s=s.trim();
							System.out.println("Got sub emote info for "+s);
							downloadSubEmotes(subemotes,s);
						}
					}
					subemotes=null;
				}
			};
			downloadThread.start();
			/*JSONObject emotelist = twitchemotes.getJSONObject("emotes");
			JSONObject templatelist = twitchemotes.getJSONObject("template");
			String templateurl = templatelist.getString("small");
			for (String emotes : emotelist.keySet()) {
				JSONObject emote = emotelist.getJSONObject(emotes);
				int id = emote.getInt("image_id");
				String emoteurl = templateurl.replace("{image_id}", ""+id);
				emoticons.add(new Emoticon(emotes, new URL(emoteurl)));
			}*/
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		emoticons.add(new Emoticon(":)","1"));
		emoticons.add(new Emoticon(":(","2"));
		emoticons.add(new Emoticon(":o","3"));
		emoticons.add(new Emoticon(":O","3"));
		emoticons.add(new Emoticon(":z","4"));
		emoticons.add(new Emoticon(":Z","4"));
		emoticons.add(new Emoticon("B)","5"));
		emoticons.add(new Emoticon(":\\","6"));
		emoticons.add(new Emoticon(":/","6"));
		emoticons.add(new Emoticon(";)","7"));
		emoticons.add(new Emoticon(";p","8"));
		emoticons.add(new Emoticon(";P","8"));
		emoticons.add(new Emoticon(":p","9"));
		emoticons.add(new Emoticon(":P","9"));
		emoticons.add(new Emoticon("R)","10"));
		emoticons.add(new Emoticon("o_O","20"));
		emoticons.add(new Emoticon("O_o","20"));
		emoticons.add(new Emoticon(":D","11"));
		emoticons.add(new Emoticon(">(","12"));
		emoticons.add(new Emoticon("<3","13"));
	}

	/*private static void DefineEmoticons() {
		//emoticons.add(new Emoticon(sigIRC.BASEDIR+"Emotes/;).png"));
		File folder = new File(sigIRC.BASEDIR+"Emotes/");
		for (File f : folder.listFiles()) {
			emoticons.add(new Emoticon(f.getAbsolutePath()));
		}
	}*/

	private static void InitializeRows(int rowcount) {
		for (int i=0;i<rowcount;i++) {
			rowobj.add(new TextRow(32+rowSpacing*i));
		}
	}

	public static void runIRCLoop(String channel, BufferedWriter writer, BufferedReader reader) throws IOException {
		String line;
		while ((line = reader.readLine( )) != null) {
		    if (line.toLowerCase( ).startsWith("PING ")) {
		        // We must respond to PINGs to avoid being disconnected.
		        writer.write("PONG " + line.substring(5) + "\r\n");
		        writer.write("PRIVMSG " + channel + " :I got pinged!\r\n");
		        writer.flush( );
		    } 
		    else {
		        // Print the raw line received by the bot.
		    	if (!authenticated && line.contains("372 "+nickname.toLowerCase())) {
		    		authenticated=true;
		    	} else
		    	if (MessageIsAllowed(line)) {
		    		String filteredMessage = FilterMessage(line);
		    		panel.addMessage(filteredMessage);
		    	}
		    }
		}
	}
	
    private static String FilterMessage(String line) {
    	final String hostcutoff_str = "sigonitori :";
    	System.out.println("Original Message: "+line);
		String username = line.substring(1, line.indexOf("!"));
		String cutstring = channel+" :";
		String message = line.substring(line.indexOf(cutstring)+cutstring.length(), line.length());
		if (username.equalsIgnoreCase("jtv")) {
			message = line.substring(line.indexOf(hostcutoff_str)+hostcutoff_str.length(), line.length());
		}
		return username+": "+ message;
	}

	private static boolean MessageIsAllowed(String line) {
		if (line.contains("PRIVMSG") && downloadsComplete) {
			return true;
		} else {
			return false;
		}
	}

	private static JFrame createAndShowGUI() {
		if (sigIRC.overlayMode && sigIRC.showWindowControls) {
			JFrame.setDefaultLookAndFeelDecorated(true);
		}
		System.setProperty("sun.java2d.opengl", Boolean.toString(sigIRC.hardwareAcceleration));
        JFrame f = new JFrame("sigIRCv2");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		if (sigIRC.overlayMode && !sigIRC.showWindowControls) {
			f.setUndecorated(true);
		}
        sigIRC.panel = new MyPanel();
        if (sigIRC.overlayMode) { 
        	sigIRC.panel.setOpaque(false);
        }
        sigIRC.panel.setBackground(sigIRC.backgroundcol);
        colorpanel = new ColorPanel();
        f.add(colorpanel);
        f.add(sigIRC.panel);
        f.pack();
        f.setVisible(true);
        f.setLocation(windowX, windowY);
        f.setSize(windowWidth, windowHeight);
        
        try {
			f.setIconImage(ImageIO.read(new File(sigIRC.BASEDIR+"/sigIRC/sigIRCicon.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}

        button = new BackgroundColorButton(new File(sigIRC.BASEDIR+"backcolor.png"),panel.getX()+panel.getWidth()-96,64+rowobj.size()*rowSpacing);
        if (sigIRC.overlayMode) {
        	f.setBackground(new Color(0,0,0,0));
            f.setAlwaysOnTop(true);
        }
        //f.setOpacity(0.5f);
        f.addWindowListener(sigIRC.panel);
        return f;
    }

	public static boolean VerifyLogin(BufferedReader reader) throws IOException {
		String line = null;
		while ((line = reader.readLine( )) != null) {
		    if (line.indexOf("004") >= 0) {
		        return true;
		    }
		    else if (line.indexOf("433") >= 0) {
		        return false;
		    }
		}
		return false;
	}
	
	public static void createEmoticon(Emoticon emote, ScrollingText textref, int x, int y) {
		twitchemoticons.add(new TwitchEmote(emote,textref,x,y));
	}
	
	public static void createEmoticon(Emoticon emote, ChatLogMessage textref, int x, int y) {
		chatlogtwitchemoticons.add(new ChatLogTwitchEmote(emote,textref,x,y));
	}
}
