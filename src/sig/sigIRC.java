package sig;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.json.JSONException;
import org.json.JSONObject;

import sig.modules.TouhouMotherModule;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class sigIRC{
	public static MyPanel panel = null;
	public static List<ScrollingText> textobj = new ArrayList<ScrollingText>();
	public static List<TextRow> rowobj = new ArrayList<TextRow>();
	public static List<Emoticon> emoticons = new ArrayList<Emoticon>();
	public static List<TwitchEmote> twitchemoticons = new ArrayList<TwitchEmote>();
	public static List<CustomSound> customsounds = new ArrayList<CustomSound>();
	public static List<Module> modules = new ArrayList<Module>();
	static UpdateEvent updater = new UpdateEvent();
	static Timer programClock = new Timer(32,updater);
	final public static int TEXTSCROLLSPD = 4;
	final public static int ROWSEPARATION = 64;
	final public static String BASEDIR = "D:\\Documents\\Touhou Mother Speedrun\\sigIRC\\"; 
	final public static String WINDOWTITLE = "sigIRCv2"; 
	
	public static void main(String[] args) {
		String server = "irc.chat.twitch.tv";
		String nickname = "SigoNitori";
		String channel = "#sigonitori";
		
		String[] filedata = FileUtils.readFromFile(BASEDIR+"oauthToken.txt");
		
		String oauth = filedata[0];
		
		WriteBreakToLogFile();

		programClock.start();
		
		InitializeRows(3);
		InitializeCustomSounds();
		
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();

        		InitializeModules();
        		performTwitchEmoteUpdate();
            }
        });
		InitializeIRCConnection(server, nickname, channel, oauth);
	}

	private static void InitializeModules() {
		modules.add(new TouhouMotherModule(
				new Rectangle(0,panel.getHeight()/2,320,panel.getHeight()/2),
				"Touhou Mother"
				));
	}

	private static void InitializeCustomSounds() {
		customsounds.add(new CustomSound("monkeyman5876", "Howler Monkeys Howling (Very Funny)-Pia8ku7jUNg.wav"));
		customsounds.add(new CustomSound("kuroplz", "Kuroyukihime Burst Link !-tv6wMw7KU9o.wav"));
		customsounds.add(new CustomSound("samusaran458", "Samus Appears - Metroid Prime [OST]-G8frLXCHtqM.wav"));
	}

	public static void InitializeIRCConnection(String server, String nickname, String channel, String oauth) {
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
		File file = new File(BASEDIR+"logs\\log_"+(cal.get(Calendar.MONTH)+1)+"_"+cal.get(Calendar.DAY_OF_MONTH)+"_"+cal.get(Calendar.YEAR)+".txt");
		if (file.exists()) {
			FileUtils.logToFile("\n---------------------------\n", BASEDIR+"logs\\log_"+(cal.get(Calendar.MONTH)+1)+"_"+cal.get(Calendar.DAY_OF_MONTH)+"_"+cal.get(Calendar.YEAR)+".txt");
		}
	}

	private static void performTwitchEmoteUpdate() {
		try {
			JSONObject twitchemotes = FileUtils.readJsonFromUrl("https://twitchemotes.com/api_cache/v2/global.json");
			JSONObject emotelist = twitchemotes.getJSONObject("emotes");
			JSONObject templatelist = twitchemotes.getJSONObject("template");
			String templateurl = templatelist.getString("small");
			for (String emotes : emotelist.keySet()) {
				JSONObject emote = emotelist.getJSONObject(emotes);
				int id = emote.getInt("image_id");
				String emoteurl = templateurl.replace("{image_id}", ""+id);
				emoticons.add(new Emoticon(emotes, new URL(emoteurl)));
			}
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
		//emoticons.add(new Emoticon(sigIRC.BASEDIR+"Emotes\\;).png"));
		File folder = new File(sigIRC.BASEDIR+"Emotes\\");
		for (File f : folder.listFiles()) {
			emoticons.add(new Emoticon(f.getAbsolutePath()));
		}
	}*/

	private static void InitializeRows(int rowcount) {
		for (int i=0;i<rowcount;i++) {
			rowobj.add(new TextRow(32+ROWSEPARATION*i));
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
		    	if (MessageIsAllowed(line)) {
		    		String filteredMessage = FilterMessage(line);
		    		panel.addMessage(filteredMessage);
		    	}
		    }
		}
	}
	
    private static String FilterMessage(String line) {
    	System.out.println("Original Message: "+line);
		String username = line.substring(1, line.indexOf("!"));
		String cutstring = "#sigonitori :";
		String message = line.substring(line.indexOf(cutstring)+cutstring.length(), line.length());
		return username+": "+ message;
	}

	private static boolean MessageIsAllowed(String line) {
		if (line.contains("PRIVMSG")) {
			return true;
		} else {
			return false;
		}
	}

	private static void createAndShowGUI() {
        JFrame f = new JFrame("sigIRCv2");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        sigIRC.panel = new MyPanel();
        sigIRC.panel.setBackground(Color.CYAN);
        f.add(sigIRC.panel);
        f.pack();
        f.setVisible(true);
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
}