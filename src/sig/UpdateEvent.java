package sig;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateEvent implements ActionListener{
	final static int MSGTIMER = 300;
	final static int AUTOSAVETIMER = 600;
	int last_authentication_msg = MSGTIMER;
	int last_autosave = AUTOSAVETIMER;
	long lasttime = System.currentTimeMillis();
	int avgfps = 30;
	int counter = 0;
	
	@Override
	public void actionPerformed(ActionEvent ev) {
	    UpdateScrollingText();
	    UpdateAuthenticationCountdownMessage();
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
					sigIRC.panel.addMessage("SYSTEM: This is a test message for your testing convenience. Kappa",!sigIRC.playedoAuthSoundOnce);
				}
			}
			if (last_autosave<AUTOSAVETIMER) {
				last_authentication_msg++;
			} else
			if (last_autosave>=AUTOSAVETIMER) {
				last_autosave=0;
				sigIRC.windowX = sigIRC.window.getX(); 
				sigIRC.windowY = sigIRC.window.getY();
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
	}

	private void ProcessTextRows() {
		for (TextRow tr : sigIRC.rowobj) {
			tr.update();
		}
		sigIRC.dingEnabled = (sigIRC.textobj.size()<=sigIRC.dingThreshold);
		//System.out.println(sigIRC.textobj.size()+"/"+sigIRC.dingThreshold+sigIRC.dingEnabled);
	}
}
