package sig.windows;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import sig.Emoticon;
import sig.FileManager;
import sig.sigIRC;
import sig.modules.RabiRace.Avatar;
import sig.modules.RabiRace.MemoryData;
import sig.utils.FileUtils;

public class LoadingDialog extends JFrame{
	JProgressBar bar = new JProgressBar();
	List<FileManager> managers = new ArrayList<FileManager>();
	FakeFileManager programUpdate = new FakeFileManager("_FAKE_");
	FakeFileManager twitchEmoteUpdate = new FakeFileManager("_FAKE_");
	public JPanel panel;
	public static List<TwitchEmoteDownload> emotes = new ArrayList<TwitchEmoteDownload>();
	
	public LoadingDialog() {
		sigIRC.loadingdialog = this;
		panel = new JPanel();
		
		managers.add(new FileManager("sigIRC/oauthToken.txt"));
		managers.add(new FileManager("sigIRC/Emotes/",true));
		managers.add(new FileManager("sigIRC/subscribers.txt"));
		managers.add(new FileManager("sigIRC/logs/",true));
		managers.add(new FileManager("sigIRC/sounds/",true));
		managers.add(new FileManager("sigIRC/rabi-ribi/",true));
		managers.add(new FileManager("sigIRC/rabi-ribi/unknown.png"));
		managers.add(new FileManager("sigIRC/rabi-ribi/characters",true));
		managers.add(new FileManager("sigIRC/rabi-ribi/items",true));
		//managers.add(new FileManager("sigIRC/sounds/Glaceon_cry.wav")
		File follower_sounds_folder = new File(sigIRC.BASEDIR+"sigIRC/follower_sounds");
		if (!follower_sounds_folder.exists()) {
			managers.add(new FileManager("sigIRC/follower_sounds/Glaceon_cry.wav"));
			managers.add(new FileManager("sigIRC/follower_sounds/README.txt"));
		}
		managers.add(new FileManager("sigIRC/record"));
		managers.add(new FileManager("sigIRC/glaceon_follower.png"));
		managers.add(new FileManager("sigIRC/sigIRCicon.png"));
		managers.add(new FileManager("sigIRC/icon_down_arrow.png"));
		managers.add(new FileManager("sigIRC/icon_follower_count.png"));
		managers.add(new FileManager("sigIRC/icon_up_arrow.png"));
		managers.add(new FileManager("sigIRC/icon_uptime.png"));
		managers.add(new FileManager("sigIRC/icon_viewers_count.png"));
		managers.add(new FileManager("sigIRC/icon_views_count.png"));
		managers.add(new FileManager("sigIRC/message_separator.png"));
		managers.add(new FileManager("sigIRC/controller/2-way_axis.png"));
		managers.add(new FileManager("sigIRC/controller/4-way_axis.png"));
		managers.add(new FileManager("sigIRC/controller/controller_overlay.png"));
		managers.add(new FileManager("sigIRC/controller/controller_template.png"));
		managers.add(new FileManager("sigIRC/CP_Font.ttf"));
		managers.add(new FileManager("kill.png"));
		managers.add(new FileManager("memory"));
		managers.add(new FileManager("swap.png"));
		managers.add(new FileManager("update.png"));
		managers.add(new FileManager("backcolor.png"));
		managers.add(new FileManager("drag_bar.png"));
		managers.add(new FileManager("sigIRC/Emotes/1.png"));
		managers.add(new FileManager("sigIRC/Emotes/2.png"));
		managers.add(new FileManager("sigIRC/Emotes/3.png"));
		managers.add(new FileManager("sigIRC/Emotes/4.png"));
		managers.add(new FileManager("sigIRC/Emotes/5.png"));
		managers.add(new FileManager("sigIRC/Emotes/6.png"));
		managers.add(new FileManager("sigIRC/Emotes/7.png"));
		managers.add(new FileManager("sigIRC/Emotes/8.png"));
		managers.add(new FileManager("sigIRC/Emotes/9.png"));
		managers.add(new FileManager("sigIRC/Emotes/10.png"));
		managers.add(new FileManager("sigIRC/Emotes/11.png"));
		managers.add(new FileManager("sigIRC/Emotes/12.png"));
		managers.add(new FileManager("sigIRC/Emotes/13.png"));
		managers.add(new FileManager("sigIRC/Emotes/20.png"));
		managers.add(programUpdate);
		
		if (sigIRC.rabiracemodule_enabled) {
			for (MemoryData data : MemoryData.values()) {
				//Attempt to fetch from server.
				managers.add(new FileManager("sigIRC/rabi-ribi/items/"+data.img_path));
			}
			for (Avatar avatar : Avatar.values()) {
				managers.add(new FileManager("sigIRC/rabi-ribi/characters/"+avatar.fileName));
			}
			managers.add(new FileManager("sigIRC/rabi-ribi/items/easter_egg.png"));
			managers.add(new FileManager("sigIRC/rabi-ribi/items/health_up.png"));
			managers.add(new FileManager("sigIRC/rabi-ribi/items/mana_up.png"));
			managers.add(new FileManager("sigIRC/rabi-ribi/items/regen_up.png"));
			managers.add(new FileManager("sigIRC/rabi-ribi/items/pack_up.png"));
			managers.add(new FileManager("sigIRC/rabi-ribi/items/attack_up.png"));
		}
		
		if (!sigIRC.offlineMode) {
			JSONObject twitchemotes;
			try {
				twitchemotes = FileUtils.readJsonFromUrl("https://twitchemotes.com/api_cache/v3/global.json");
				System.out.println("Twitch emote Json read.");
				if (twitchemotes!=null) {
					for (String emotes : twitchemotes.keySet()) {
						JSONObject emote = twitchemotes.getJSONObject(emotes);
						int id = emote.getInt("id");
						String name = emote.getString("code");
						LoadingDialog.emotes.add(new TwitchEmoteDownload(name,id));
						managers.add(new FakeFileManager("_FAKE_"));
						//emoticons.add(new Emoticon(name, new URL(TWITCHEMOTEURL+id+"/1.0")));
						System.out.println("Emote "+id+" with name "+name);
					}
				}
			} catch (NullPointerException | JSONException | IOException e) {
				sigIRC.offlineMode=true;
				e.printStackTrace();
			}
		}
		
		bar.setValue(0);
		bar.setMaximum(managers.size());
		bar.setPreferredSize(new Dimension(240,24));
		bar.setString("Downloading resources... (0/"+managers.size()+")");
		bar.setStringPainted(true);
		
		panel.setSize(new Dimension(260,30));
		
		panel.add(bar);
		
		this.add(panel);

		this.setSize(new Dimension(260,36));
		this.setLocationByPlatform(true);
		this.setMinimumSize(new Dimension(260, 36));
		this.setMaximumSize(new Dimension(260, 36));
		this.setUndecorated(true);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.setFocusable(false);		
		
		for (FileManager manager : managers) {
			if (manager.verifyAndFetchFileFromServer()) {
				bar.setValue(bar.getValue()+1);
				UpdateBar();
			}
		}
		
		for (TwitchEmoteDownload d : emotes) {
			try {
				if (!sigIRC.offlineMode) {
					d.download();
				}
				bar.setValue(bar.getValue()+1);
				UpdateBar();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}

		if (!sigIRC.offlineMode) {
			sigIRC.prepareTwitchEmoteUpdate();
		}
		twitchEmoteUpdate.setDone();
		bar.setValue(bar.getValue()+1);
		UpdateBar();
		if (!sigIRC.offlineMode) {
			sigIRC.DownloadProgramUpdate();
		}
		programUpdate.setDone();
		bar.setValue(bar.getValue()+1);
		UpdateBar();
		
		sigIRC.emoticons.add(new Emoticon(":)","1"));
		sigIRC.emoticons.add(new Emoticon(":(","2"));
		sigIRC.emoticons.add(new Emoticon(":o","3"));
		sigIRC.emoticons.add(new Emoticon(":O","3"));
		sigIRC.emoticons.add(new Emoticon(":z","4"));
		sigIRC.emoticons.add(new Emoticon(":Z","4"));
		sigIRC.emoticons.add(new Emoticon("B)","5"));
		sigIRC.emoticons.add(new Emoticon(":\\","6"));
		sigIRC.emoticons.add(new Emoticon(":/","6"));
		sigIRC.emoticons.add(new Emoticon(";)","7"));
		sigIRC.emoticons.add(new Emoticon(";p","8"));
		sigIRC.emoticons.add(new Emoticon(";P","8"));
		sigIRC.emoticons.add(new Emoticon(":p","9"));
		sigIRC.emoticons.add(new Emoticon(":P","9"));
		sigIRC.emoticons.add(new Emoticon("R)","10"));
		sigIRC.emoticons.add(new Emoticon("o_O","20"));
		sigIRC.emoticons.add(new Emoticon("O_o","20"));
		sigIRC.emoticons.add(new Emoticon(":D","11"));
		sigIRC.emoticons.add(new Emoticon(">(","12"));
		sigIRC.emoticons.add(new Emoticon("<3","13"));
		
		//Load is done. Start up the panel.
		sigIRC.window = new ProgramWindow();
		this.setVisible(false);
	}

	private void UpdateBar() {
		bar.setString("Downloading resources... ("+bar.getValue()+"/"+managers.size()+")");
	}
}

class FakeFileManager extends FileManager{
	boolean done=false;
	public FakeFileManager(String location) {
		super(location);
	}
	public void setDone() {
		done=true;
	}
}
