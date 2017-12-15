package sig.modules;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import sig.CustomSound;
import sig.Module;
import sig.TextRow;
import sig.sigIRC;
import sig.utils.FileUtils;
import sig.windows.ProgramWindow;

public class ScrollingChatModule extends Module{

	final static int MSGTIMER = 300;
	final static int AUTOSAVETIMER = 600;
	int last_authentication_msg = MSGTIMER;
	int last_autosave = AUTOSAVETIMER;  
	public static ScrollingChatModule module;

	public ScrollingChatModule(Rectangle bounds, String moduleName) {
		super(bounds, moduleName);
		
		ScrollingChatModule.module = this;
		String[] filedata = FileUtils.readFromFile(sigIRC.BASEDIR+"sigIRC/oauthToken.txt");

		for (int i=0;i<sigIRC.chatRows;i++) {
			sigIRC.rowobj.add(new TextRow(32+sigIRC.ROWSEPARATION*i));
		}
	    //UpdateSubEmoticons();
	}

	public void run() {
		super.run();
	    UpdateScrollingText();
	    UpdateAuthenticationCountdownMessage();
		//System.out.println("Called.");
	}


	private void UpdateSubEmoticons() {
		if (!sigIRC.downloadedSubEmotes &&
				sigIRC.disableChatMessages &&
				sigIRC.subchannelCount==sigIRC.subchannelIds.size()) {
			Thread downloadThread = new Thread(){
				public void run() {
						JSONObject data = GetSubEmoteJson();
						sigIRC.downloadSubEmotes(data);
						sigIRC.subEmotesCompleted=true;
					}
				};
			downloadThread.start();
			sigIRC.downloadedSubEmotes=true;
		}
	}
	
	public void ApplyConfigWindowProperties() {
		sigIRC.scrollingchatmodule_X=(int)position.getX();
		sigIRC.scrollingchatmodule_Y=(int)position.getY();
		sigIRC.scrollingchatmodule_width=(int)position.getWidth();
		sigIRC.scrollingchatmodule_height=(int)position.getHeight();
		sigIRC.config.setInteger("SCROLLINGCHAT_module_X", sigIRC.scrollingchatmodule_X);
		sigIRC.config.setInteger("SCROLLINGCHAT_module_Y", sigIRC.scrollingchatmodule_Y);
		sigIRC.config.setInteger("SCROLLINGCHAT_module_width", sigIRC.scrollingchatmodule_width);
		sigIRC.config.setInteger("SCROLLINGCHAT_module_height", sigIRC.scrollingchatmodule_height);
		sigIRC.config.saveProperties();
	}
	
	public void draw(Graphics g) {
		super.draw(g);
		for (int i=0;i<sigIRC.textobj.size();i++) {
        	if (sigIRC.textobj.get(i).isActive()) {
        		if (sigIRC.overlayMode) {
	        		if (!sigIRC.textobj.get(i).intersects((int)(ProgramWindow.frame.lastMouseX-position.getX()),(int)(ProgramWindow.frame.lastMouseY-position.getY()))) {
	        			sigIRC.textobj.get(i).setVisible(true);
	        			sigIRC.textobj.get(i).draw(g);
	        		} else {
	        			//System.out.println("Setting to False.");
	        			sigIRC.textobj.get(i).setVisible(false);
	        		}
        		} else {
        			sigIRC.textobj.get(i).draw(g);
        		}
        	}
        }
        for (int i=0;i<sigIRC.twitchemoticons.size();i++) {
        	if (sigIRC.twitchemoticons.get(i).isActive() &&
        			sigIRC.twitchemoticons.get(i).textRefIsVisible()) {
        		sigIRC.twitchemoticons.get(i).draw(g);
        	} else {
        		break;
        	}
        }
	}

	private JSONObject GetSubEmoteJson() {
		JSONObject subemotes = null;
		try {
			File filer = new File(sigIRC.SUBEMOTELISTFILE);
			if (!filer.exists()) {
				System.out.println("Local copy of Sub emotes not found. Downloading in background...");
				subemotes = FileUtils.readJsonFromUrlWithFilter("https://twitchemotes.com/api_cache/v3/subscriber.json",sigIRC.subchannelIds,sigIRC.SUBEMOTELISTFILE,true);
			} else {
				if (sigIRC.lastSubEmoteUpdate == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
					System.out.println("Using local copy of Sub emote JSON.");
					subemotes = FileUtils.readJsonFromFileWithFilter(sigIRC.SUBEMOTELISTFILE,sigIRC.subchannelIds);
				} else {
					System.out.println("Local copy of Sub emote JSON out-of-date! Re-downloading in background...");
					subemotes = FileUtils.readJsonFromFileWithFilter(sigIRC.SUBEMOTELISTFILE,sigIRC.subchannelIds);
					new Thread(){
						public void run() {
							try {
								FileUtils.readJsonFromUrlWithFilter("https://twitchemotes.com/api_cache/v3/subscriber.json",sigIRC.subchannelIds,sigIRC.SUBEMOTELISTFILE,true);
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
		sigIRC.lastSubEmoteUpdate = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
		sigIRC.config.setInteger("lastSubEmote_APIUpdate", sigIRC.lastSubEmoteUpdate);
		return subemotes;
	}

	private void UpdateAuthenticationCountdownMessage() {
		if (sigIRC.downloadsComplete) {
			if ((!sigIRC.authenticated || sigIRC.testMode) && last_authentication_msg<MSGTIMER) {
				last_authentication_msg++;
			} else
			if ((!sigIRC.authenticated || sigIRC.testMode) && last_authentication_msg>=MSGTIMER) {
				last_authentication_msg=0;
				if (!sigIRC.authenticated && !sigIRC.testMode) {
					sigIRC.panel.addMessage("SYSTEM: Your oauthToken was not successful. Please go to the sigIRC folder and make sure your oauthToken.txt file is correct!!! SwiftRage",!sigIRC.playedoAuthSoundOnce);
					if (!sigIRC.playedoAuthSoundOnce) {
						sigIRC.playedoAuthSoundOnce=true;
					}
				} else {
					sigIRC.panel.addMessage("SYSTEM: This is a test message for your testing convenience. mikansBox",!sigIRC.playedoAuthSoundOnce);
				}
			}
			if (last_autosave<AUTOSAVETIMER) {
				last_authentication_msg++;
			} else
			if (last_autosave>=AUTOSAVETIMER) {
				last_autosave=0;
				sigIRC.windowX = (int)sigIRC.window.getLocationOnScreen().getX(); 
				sigIRC.windowY = (int)sigIRC.window.getLocationOnScreen().getY();
				sigIRC.windowWidth = sigIRC.window.getWidth(); 
				sigIRC.windowHeight = sigIRC.window.getHeight();
				sigIRC.config.setInteger("windowX", sigIRC.windowX);
				sigIRC.config.setInteger("windowY", sigIRC.windowY);
				sigIRC.config.setInteger("windowWidth", sigIRC.windowWidth);
				sigIRC.config.setInteger("windowHeight", sigIRC.windowHeight);
				sigIRC.config.saveProperties();
			}
			if (sigIRC.lastPlayedDing>0) {
				sigIRC.lastPlayedDing--;
			}
		}
	}
	
	public void UpdateScrollingText() {
		for (int i=0;i<sigIRC.twitchemoticons.size();i++) {
	    	boolean keep = sigIRC.twitchemoticons.get(i).run();
	    	if (!keep) {
	    		sigIRC.twitchemoticons.remove(i--);
	    	}
	    }
		for (int i=0;i<sigIRC.textobj.size();i++) {
			//System.out.println(sigIRC.textobj.get(i).getX()+","+sigIRC.textobj.get(i).getY());
	    	boolean keep = sigIRC.textobj.get(i).run();
	    	if (!keep) {
	    		sigIRC.textobj.remove(i--);
	    	}
	    }
		ProcessTextRows();
		for (CustomSound cs : sigIRC.customsounds) {
			if (!cs.isSoundAvailable()) {
				cs.decreaseCooldown(1);
			}
		}
	}

	private void ProcessTextRows() {
		for (TextRow tr : sigIRC.rowobj) {
			tr.update();
		}
		sigIRC.dingEnabled = (sigIRC.textobj.size()<=sigIRC.dingThreshold);
		//System.out.println(sigIRC.textobj.size()+"/"+sigIRC.dingThreshold+sigIRC.dingEnabled);
	}
}
