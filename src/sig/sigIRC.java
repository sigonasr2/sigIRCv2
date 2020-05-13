package sig;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;

import sig.modules.BandoriModule;
import sig.modules.ChatLogModule;
import sig.modules.ControllerModule;
import sig.modules.DDRStepModule;
import sig.modules.RabiRaceModule;
import sig.modules.RabiRibiModule;
import sig.modules.TouhouMotherModule;
import sig.modules.TwitchModule;
import sig.modules.ChatLog.ChatLogMessage;
import sig.modules.ChatLog.ChatLogTwitchEmote;
import sig.modules.Controller.ControlConfigurationWindow;
import sig.modules.utils.MyKernel32;
import sig.modules.utils.PsapiTools;
import sig.utils.FileUtils;
import sig.utils.GithubUtils;
import sig.utils.MemoryUtils;
import sig.utils.TextUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

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
	static Timer programClock = new Timer(33,updater);
	final public static int BASESCROLLSPD = 4;
	final public static int ROWSEPARATION = 64;
	final public static String BASEDIR = "./"; 
	final public static String PROGRAM_UPDATE_FILE = sigIRC.BASEDIR+"sigIRC/updates/sigIRCv2.jar";
	final public static String WINDOWTITLE = "sigIRCv2"; 
	public final static String PROGRAM_EXECUTABLE_URL = "https://github.com/sigonasr2/sigIRCv2/raw/master/sigIRCv2.jar";
	public static ConfigFile config;
	static String server;
	public static String nickname;
	public static String channel;
	public static boolean authenticated=false;
	public static int lastPlayedDing=0;
	final public static int DINGTIMER=150;
	static boolean dingEnabled=true;
	static int dingThreshold;
	static Color backgroundcol;
	public static BackgroundColorButton button;
	public static ModuleSelectorButton modulebutton;
	public static JFrame window;
	static boolean overlayMode=false;
	public static boolean showWindowControls=false;
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
	static Integer messageFontSize = 24;
	static Integer usernameFontSize = 16;
	static Integer touhoumotherConsoleFontSize = 12;
	public static boolean touhoumothermodule_enabled=false;
	public static boolean twitchmodule_enabled=true;
	public static boolean chatlogmodule_enabled=true;
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
	public static int rabiribimodule_width=320;
	public static int rabiribimodule_height=312;
	public static int rabiribimodule_X=0;
	public static int rabiribimodule_Y=312;
	public static int bandorimodule_width=320;
	public static int bandorimodule_height=312;
	public static int bandorimodule_X=0;
	public static int bandorimodule_Y=312;
	public static int ddrstepmodule_width=320;
	public static int ddrstepmodule_height=312;
	public static int ddrstepmodule_X=0;
	public static int ddrstepmodule_Y=312;
	public static boolean ddrstepmodule_enabled=false;
	public static boolean bandorimodule_enabled=false;
	public static boolean rabiribimodule_enabled=false;
	public static int rabiracemodule_width=320;
	public static int rabiracemodule_height=312;
	public static int rabiracemodule_X=0;
	public static int rabiracemodule_Y=312;
	public static boolean rabiracemodule_enabled=false;
	public static int chatlogMessageHistory=50;
	public static boolean controllermodule_enabled=true;
	public static int controllermodule_width=320;
	public static int controllermodule_height=312;
	public static int controllermodule_X=0;
	public static int controllermodule_Y=312;
	public static String twitchmodule_newfollowerImgBackgroundColor="90,90,90";
	public static String twitchmodule_newfollowerShadowTextColor="26,90,150";
	public static String twitchmodule_newfollowerTextColor="255,255,255";
	public static String twitchmodule_newfollowerNameTextColor="0,0,0";
	public static String twitchmodule_newfollowerNameShadowColor="255,255,180";
	public static int twitchmodule_newfollowerNameShadowSize=2;
	public static String chatlogmodule_backgroundColor="195,195,195,255";
	public static int twitchmodule_newfollowerImgLogoSize=32;
	public static boolean testMode=false;
	public final static String TWITCHEMOTEURL = "https://static-cdn.jtvnw.net/emoticons/v1/";
	public final static String SUBEMOTELISTFILE = "sigIRC/subemotes.json";
	public static long channel_id = -1;
	public static int lastSubEmoteUpdate = -1;
	public static boolean autoUpdateProgram = true;
	public static Image programIcon;
	//final public static int MAX_CONNECTION_RETRIES = 100; 
	public static String CLIENTID = "";
	public static int retryCounter = 0;
	public static boolean newUpdateIsAvailable = false;
	public static long lastRetryTime = 0l;
	public static List<ModuleLinker> moduleList;
	
	public static int subchannelCount = 0;
	public static HashMap<Long,String> subchannelIds = new HashMap<Long,String>();
	
	public static boolean downloadedSubEmotes=false;
	public static boolean subEmotesCompleted=false;
	public static boolean disableChatMessages=false;
	
	static int lastWindowX = 0;
	static int lastWindowY = 0;
	public static String longString = "10988989d";
	public static ModuleSelector moduleSelectorWindow;
	public static String oauth;
	
	public static void main(String[] args) {
		
		String[] filedata = FileUtils.readFromFile(BASEDIR+"sigIRC/oauthToken.txt");
		
		oauth = filedata[0];
		//filedata = FileUtils.readFromFile("user_data");
		
		JSONObject data;
		try {
			FileUtils.downloadFileFromUrl("https://id.twitch.tv/oauth2/validate", "user_data");
			data = FileUtils.readJsonFromFile("user_data");
			sigIRC.CLIENTID = data.getString("client_id");
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		config = InitializeConfigurationFile();
		
		server = config.getProperty("server");
		nickname = config.getProperty("nickname");
		channel = config.getProperty("channel");
		

		try {
			channel_id = sigIRC.GetChannelID(channel.replace("#", ""));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
		usernameFont = config.getProperty("usernameFont","Segoe UI Semibold");
		touhoumotherConsoleFont = config.getProperty("touhoumotherConsoleFont","Agency FB Bold");
		messageFontSize = config.getInteger("messageFontSize",24);
		usernameFontSize = config.getInteger("usernameFontSize",16);
		touhoumotherConsoleFontSize = config.getInteger("touhouMotherConsoleFontSize",12);
		touhoumothermodule_enabled = config.getBoolean("Module_touhoumother_Enabled",false);
		controllermodule_enabled = config.getBoolean("Module_controller_Enabled",false);
		twitchmodule_enabled = config.getBoolean("Module_twitch_Enabled",true);
		chatlogmodule_enabled = config.getBoolean("Module_chatlog_Enabled",true);
		twitchmodule_width = config.getInteger("TWITCH_module_width",500);
		twitchmodule_height = config.getInteger("TWITCH_module_height",200);
		twitchmodule_follower_img = config.getProperty("TWITCH_module_follower_img","sigIRC/glaceon_follower.png");
		twitchmodule_follower_img_animation = config.getBoolean("TWITCH_module_follower_img_animation",true);
		twitchmodule_followerText_centerX = config.getInteger("TWITCH_module_followerText_centerX",292);
		twitchmodule_followerText_Y = config.getInteger("TWITCH_module_followerText_Y",84);
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
		/*rabiribimodule_X = config.getInteger("RABIRIBI_module_X",0);
		rabiribimodule_Y = config.getInteger("RABIRIBI_module_Y",312);
		rabiribimodule_width = config.getInteger("RABIRIBI_module_width",320);
		rabiribimodule_height = config.getInteger("RABIRIBI_module_height",312);
		rabiribimodule_enabled = config.getBoolean("Module_rabiribi_Enabled", false);*/
		bandorimodule_X = config.getInteger("BANDORI_module_X", 240);
		bandorimodule_Y = config.getInteger("BANDORI_module_Y", 0);
		bandorimodule_width = config.getInteger("BANDORI_module_width", 640);
		bandorimodule_height = config.getInteger("BANDORI_module_height", 120);
		bandorimodule_enabled = config.getBoolean("Module_bandori_Enabled", false);
		ddrstepmodule_X = config.getInteger("ddrstep_module_X", 240);
		ddrstepmodule_Y = config.getInteger("ddrstep_module_Y", 0);
		ddrstepmodule_width = config.getInteger("ddrstep_module_width", 640);
		ddrstepmodule_height = config.getInteger("ddrstep_module_height", 120);
		ddrstepmodule_enabled = config.getBoolean("Module_ddrstep_Enabled", false);
		rabiracemodule_X = config.getInteger("RABIRACE_module_X",0);
		rabiracemodule_Y = config.getInteger("RABIRACE_module_Y",312);
		rabiracemodule_width = config.getInteger("RABIRACE_module_width",320);
		rabiracemodule_height = config.getInteger("RABIRACE_module_height",312);
		rabiracemodule_enabled = config.getBoolean("Module_rabirace_Enabled", false);
		chatlogmodule_X = config.getInteger("CHATLOG_module_X",0);
		chatlogmodule_Y = config.getInteger("CHATLOG_module_Y",312);
		chatlogmodule_width = config.getInteger("CHATLOG_module_width",320);
		chatlogmodule_height = config.getInteger("CHATLOG_module_height",312);
		controllermodule_X = config.getInteger("CONTROLLER_module_X",0);
		controllermodule_Y = config.getInteger("CONTROLLER_module_Y",312);
		controllermodule_width = config.getInteger("CONTROLLER_module_width",320);
		controllermodule_height = config.getInteger("CONTROLLER_module_height",312);
		chatlogmodule_backgroundColor = config.getProperty("CHATLOG_module_BackgroundColor", "195,195,195,255");
		chatlogMessageHistory = config.getInteger("CHATLOG_module_MessageHistory",50);
		hardwareAcceleration = config.getBoolean("hardware_acceleration",true);
		autoUpdateProgram = config.getBoolean("Automatically_Update_Program", true);
		disableChatMessages = config.getBoolean("Disable_Chat_Messages", false);
		lastSubEmoteUpdate = config.getInteger("lastSubEmote_APIUpdate",Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
		twitchmodule_newfollowerNameTextColor = config.getProperty("TWITCH_module_newfollowerNameTextColor","0,0,0");
		twitchmodule_newfollowerNameShadowColor = config.getProperty("TWITCH_module_newfollowerNameShadowColor","255,255,180");
		twitchmodule_newfollowerNameShadowSize = config.getInteger("TWITCH_module_newfollowerNameShadowSize",2);
		//manager.setClientId("o4c2x0l3e82scgar4hpxg6m5dfjbem");
		//System.out.println(manager.auth().hasAccessToken());

		DownloadAllRequiredDependencies();
		/*try {
			org.apache.commons.io.FileUtils.copyURLToFile(new URL("https://bandori.party/api/"),new File("testing"));
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		Initialize();
		
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

	private static void Initialize() {
		try {
			programIcon = ImageIO.read(new File(sigIRC.BASEDIR+"/sigIRC/sigIRCicon.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		manager = new FileManager("sigIRC/subscribers.txt"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/logs/",true); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/sub_emotes/",true); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/sounds/",true); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/rabi-ribi/",true); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/rabi-ribi/unknown.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/rabi-ribi/characters",true); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/rabi-ribi/items",true); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("Boss Sprites/",true); manager.verifyAndFetchFileFromServer();
		//manager = new FileManager("sigIRC/sounds/Glaceon_cry.wav"); manager.verifyAndFetchFileFromServer();
		File follower_sounds_folder = new File(BASEDIR+"sigIRC/follower_sounds");
		if (!follower_sounds_folder.exists()) {
			manager = new FileManager("sigIRC/follower_sounds/Glaceon_cry.wav"); manager.verifyAndFetchFileFromServer();
			manager = new FileManager("sigIRC/follower_sounds/README.txt"); manager.verifyAndFetchFileFromServer();
		}
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
		manager = new FileManager("sigIRC/controller/2-way_axis.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/controller/4-way_axis.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/controller/controller_overlay.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/controller/controller_template.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/CP_Font.ttf"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/collect_item.wav"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("kill.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("memory"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("swap.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("update.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("backcolor.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("modules.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("drag_bar.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("map_icons.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/stamps1.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/stamps2_2.png"); manager.verifyAndFetchFileFromServer();
		manager = new FileManager("sigIRC/stamp_what_a_great_idea.png"); manager.verifyAndFetchFileFromServer();
		DownloadProgramUpdate();
		System.out.println("Downloaded Dependencies. ");
	}

	private static void DownloadProgramUpdate() {
		//System.out.println("Last commit size was "+GithubUtils.getSizeOfFileFromLatestGithubCommit("sigIRCv2.jar")+"B");
		if (autoUpdateProgram) {
			File updatedir = new File(sigIRC.BASEDIR+"sigIRC/updates/");
			updatedir.mkdirs();
			File controllerdir = new File(ControllerModule.CONTROLLERPATH);
			controllerdir.mkdirs();
			File programFile = new File(sigIRC.BASEDIR+"sigIRC/updates/sigIRCv2.jar");
			File currentProgramFile = new File(sigIRC.BASEDIR+"sigIRCv2.jar");
			System.out.println("File size is "+currentProgramFile.length());
			long fileSize = GithubUtils.getSizeOfFileFromLatestGithubCommit("sigIRCv2.jar");
			System.out.println("File size on Github is "+fileSize);
			if (fileSize!=programFile.length()) {
				System.out.println("File size on Github varies from currently running program... Downloading new program.");
				try {
					if (programFile.exists()) {
						programFile.delete();
					}
					org.apache.commons.io.FileUtils.copyURLToFile(new URL(sigIRC.PROGRAM_EXECUTABLE_URL),programFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				newUpdateIsAvailable = true;
			}
		}
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
		if (controllermodule_enabled)  {
			modules.add(new ControllerModule(
					new Rectangle(controllermodule_X,controllermodule_Y,controllermodule_width,controllermodule_height),
					"Controller"
					));
		}
		if (rabiribimodule_enabled)  {
			modules.add(new RabiRibiModule(
					new Rectangle(rabiribimodule_X,rabiribimodule_Y,rabiribimodule_width,rabiribimodule_height),
					"Rabi-Ribi"
					));
		}
		if (rabiracemodule_enabled)  {
			modules.add(new RabiRaceModule(
					new Rectangle(rabiracemodule_X,rabiracemodule_Y,rabiracemodule_width,rabiracemodule_height),
					"Rabi-Race"
					));
		}
		if (bandorimodule_enabled) {
			BandoriModule.loadModule();
		}
		if (ddrstepmodule_enabled) {
			modules.add(new DDRStepModule(
					new Rectangle(ddrstepmodule_X,ddrstepmodule_Y,ddrstepmodule_width,ddrstepmodule_height),
					"DDR Step"
					));
		}
		moduleSelectorWindow = new ModuleSelector();
	}

	private static void InitializeCustomSounds() {
		customsounds.add(new CustomSound("monkeyman5876", "Howler_Monkeys_Howling_Very_Funny.wav"));
		customsounds.add(new CustomSound("kuroplz", "Kuroyukihime_Burst_Link.wav"));
		customsounds.add(new CustomSound("samusaran458", "Samus_Appears_Metroid_Prime_OST.wav"));
	}

	public static void InitializeIRCConnection(final String server,final String nickname,final String channel,final String oauth) {
		Socket socket;
		retryCounter++;
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
		} catch (UnknownHostException | SocketException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        new java.util.Timer().schedule(new TimerTask() {
        	public void run() {
				InitializeIRCConnection(server,nickname,channel,oauth);
				sigIRC.panel.addMessage("SYSTEM: Lost connection. Trying to reconnect...");
				System.out.println("SYSTEM: Lost connection. Trying to reconnect...");
        	}
		},10000);
	}

	public static void WriteBreakToLogFile() {
		Calendar cal = Calendar.getInstance();
		File file = new File(BASEDIR+"logs/log_"+(cal.get(Calendar.MONTH)+1)+"_"+cal.get(Calendar.DAY_OF_MONTH)+"_"+cal.get(Calendar.YEAR)+".txt");
		if (file.exists()) {
			FileUtils.logToFile("\n---------------------------\n", BASEDIR+"logs/log_"+(cal.get(Calendar.MONTH)+1)+"_"+cal.get(Calendar.DAY_OF_MONTH)+"_"+cal.get(Calendar.YEAR)+".txt");
		}
	}
	
	
	private static void getSubChannels(String s) {
		try {
			Long id = GetChannelID(s);
			subchannelIds.put(id, s);
			//System.out.println("Got ID "+id+" for channel "+s);
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		//TwitchModule.streamOnline=true;
	}

	public static Long GetChannelID(String username) throws IOException {
		FileUtils.downloadFileFromUrl("https://api.twitch.tv/kraken/users?login="+username, "temp_connect");
		JSONObject j = FileUtils.readJsonFromFile("temp_connect");
		JSONArray a = j.getJSONArray("users");
		Long id = Long.parseLong(a.getJSONObject(0).getString("_id"));
		return id;
	}

	/*private static void getSubChannels(String channelName) {
		manager.channels().get(channelName, new ChannelResponseHandler() {
			@Override
			public void onFailure(Throwable arg0) {
			}

			@Override
			public void onFailure(int arg0, String arg1, String arg2) {
			}

			@Override
			public void onSuccess(Channel arg0) {
				subchannelIds.put(arg0.getId(),channelName);
				//System.out.println("Got ID "+arg0.getId()+" for channel "+channelName);
			}

		});
		//TwitchModule.streamOnline=true;
		//return true;
	}*/

	public static void downloadSubEmotes() {
		for (Long l : subchannelIds.keySet()) {
			JSONObject channel;
			try {
				channel = FileUtils.readJsonFromUrl("https://api.twitchemotes.com/api/v4/channels/"+l);
				//String channel = subchannelIds.get(l);
				JSONArray arr = channel.getJSONArray("emotes");
				//System.out.println("Channel: "+channel);
				for (int i=0;i<arr.length();i++) {
					JSONObject emote = arr.getJSONObject(i);
					int id = emote.getInt("id");
					String name = emote.getString("code");
					System.out.println("Emote "+(i+1)+" has id "+id+" and code "+name+".");
					try {
						emoticon_queue.add(new SubEmoticon(name, new URL(TWITCHEMOTEURL+id+"/1.0"), subchannelIds.get(l)));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			} catch (JSONException | IOException e1) {
				e1.printStackTrace();
			}
		}
		//TwitchModule.streamOnline=true;
		//return true;
	}

	private static void performTwitchEmoteUpdate() {
		try {
			JSONObject twitchemotes = FileUtils.readJsonFromUrl("https://api.twitchemotes.com/api/v4/channels/0"); //Channel 0 is global emotes.
			System.out.println("Twitch emote Json read.");
			JSONArray emotes = twitchemotes.getJSONArray("emotes");
			for (int i=0;i<emotes.length();i++) {
				JSONObject emote = emotes.getJSONObject(i);
				int id = emote.getInt("id");
				if (id>14) {
					String name = emote.getString("code");
					emoticons.add(new Emoticon(name, new URL(TWITCHEMOTEURL+id+"/1.0")));
					System.out.println("Emote "+id+" with name "+name);
				}
			}
			//System.out.println("Subscriber object: "+subemotes);
			String[] channel_names = FileUtils.readFromFile(sigIRC.BASEDIR+"sigIRC/subscribers.txt");
			subchannelCount = channel_names.length;
			//System.out.println("Expecting "+subchannelCount+" Channel IDs.");
			for (String s : channel_names) {
				if (s.length()>0) {
					s=s.trim();
					//System.out.println("Got sub emote info for "+s);
					//TODO Rewrite.
					getSubChannels(s);
				}
			}
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
		    		line = new String(line.getBytes(),"UTF-8");
		    		String filteredMessage = FilterMessage(line);
		    		panel.addMessage(filteredMessage);
		    		
		    	}
		    }
		}
	}
	
    private static String FilterMessage(String line) {
    	final String hostcutoff_str = "sigonitori :";
		String username = line.substring(1, line.indexOf("!"));
		String cutstring = channel+" :";
		String message = line.substring(line.indexOf(cutstring)+cutstring.length(), line.length());
		if (username.equalsIgnoreCase("jtv")) {
			message = line.substring(line.indexOf(hostcutoff_str)+hostcutoff_str.length(), line.length());
		}
		/*if (message.length()>0) {
			//message = "\uac00\uac01\uac02\u1100\u1101\u1102\u1117 1234567890 ";
			System.out.println(message);
		}*/
    	System.out.println(username+": "+ message);
		return username+": "+ message;
	}

	private static boolean MessageIsAllowed(String line) {
		if (line.contains("PRIVMSG") && downloadsComplete && !disableChatMessages) {
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
        f.setAutoRequestFocus(true);
        f.toFront();
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
        
       f.setIconImage(programIcon);

        button = new BackgroundColorButton(new File(sigIRC.BASEDIR+"backcolor.png"),panel.getX()+panel.getWidth()-96,64+rowobj.size()*rowSpacing);
        modulebutton = new ModuleSelectorButton(new File(sigIRC.BASEDIR+"modules.png"),panel.getX()+panel.getWidth()-96,64+rowobj.size()*rowSpacing);
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
