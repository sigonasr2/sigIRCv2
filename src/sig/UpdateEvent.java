package sig;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import sig.utils.FileUtils;

public class UpdateEvent implements ActionListener{
	final static int MSGTIMER = 300;
	final static int AUTOSAVETIMER = 600;
	int last_authentication_msg = MSGTIMER;
	int last_autosave = AUTOSAVETIMER;  
	long lasttime = System.currentTimeMillis();
	int avgfps = 30;
	int counter = 0;
	int windowUpdateCounter=30;
	
	@Override
	public void actionPerformed(ActionEvent ev) {
		if (ev!=null) {
		    UpdateScrollingText();
		    UpdateAuthenticationCountdownMessage();
		    UpdateWindowPosition();
		    UpdateSubEmoticons();
		}
	}

	private void UpdateSubEmoticons() {
		//System.out.println("Subchannels: "+sigIRC.subchannelCount+", "+sigIRC.subchannelIds.size());
		if (!sigIRC.downloadedSubEmotes &&
				!sigIRC.disableChatMessages &&
				sigIRC.subchannelCount==sigIRC.subchannelIds.size()) {
			Thread downloadThread = new Thread(){
				public void run() {
						//JSONObject data = GetSubEmoteJson();
						sigIRC.downloadSubEmotes();
						sigIRC.subEmotesCompleted=true;
					}
				};
			downloadThread.start();
			sigIRC.downloadedSubEmotes=true;
		}
	}

	private boolean GetSubEmotesNeedChecking() {
		/*JSONObject subemotes = null;
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
		}*/
		boolean needsUpdate = sigIRC.lastSubEmoteUpdate != Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
		sigIRC.lastSubEmoteUpdate = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
		sigIRC.config.setInteger("lastSubEmote_APIUpdate", sigIRC.lastSubEmoteUpdate);
		return needsUpdate;
	}

	private void UpdateWindowPosition() {
		if (windowUpdateCounter--<=0) {
			if (sigIRC.lastWindowX!=(int)sigIRC.window.getLocationOnScreen().getX() ||
					sigIRC.lastWindowY!=(int)sigIRC.window.getLocationOnScreen().getY()) {
				sigIRC.lastWindowX = (int)sigIRC.window.getLocationOnScreen().getX();
				sigIRC.lastWindowY = (int)sigIRC.window.getLocationOnScreen().getY();
				//Trigger Window Update.
				for (Component c : sigIRC.window.getComponents()) {
					MyPanel.UpdateComponent(c);
				}
			}
			windowUpdateCounter=30;
		}
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
			updateFPSCounter();
			sigIRC.window.setTitle("sigIRCv2 - "+(avgfps)+" FPS");
			lasttime=System.currentTimeMillis();
		}
	}

	public void updateFPSCounter() {
		if (System.currentTimeMillis()-lasttime>1000/avgfps) {
			//System.out.println("WARNING! Last update took "+(System.currentTimeMillis()-lasttime)+"ms! Lagging?");
			if (counter<30) {
				counter++;
			} else {
				counter=0;
				avgfps--;
			}
		} else {
			if (counter>-30) {
				counter--;
			} else {
				counter=0;
				avgfps++;
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
		Module.inDragZone=false;
		for (Module m : sigIRC.modules) {
			m.moduleRun();
		}
		for (int i=0;i<sigIRC.chatlogtwitchemoticons.size();i++) {
	    	boolean keep = sigIRC.chatlogtwitchemoticons.get(i).run();
	    	if (!keep) {
	    		sigIRC.chatlogtwitchemoticons.remove(i--);
	    	}
	    }
		sigIRC.panel.repaint();
	}

	private void ProcessTextRows() {
		for (TextRow tr : sigIRC.rowobj) {
			tr.update();
		}
		sigIRC.dingEnabled = (sigIRC.textobj.size()<=sigIRC.dingThreshold);
		//System.out.println(sigIRC.textobj.size()+"/"+sigIRC.dingThreshold+sigIRC.dingEnabled);
	}
}
