package sig;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateEvent implements ActionListener{
	final static int MSGTIMER = 300;
	int last_authentication_msg = MSGTIMER;
	
	@Override
	public void actionPerformed(ActionEvent ev) {
	    UpdateScrollingText();
	    UpdateAuthenticationCountdownMessage();
	}

	private void UpdateAuthenticationCountdownMessage() {
		if (!sigIRC.authenticated && last_authentication_msg<MSGTIMER) {
			last_authentication_msg++;
		}
		if (!sigIRC.authenticated && last_authentication_msg==MSGTIMER) {
			last_authentication_msg=0;
			sigIRC.panel.addMessage("SYSTEM: Your oauthToken was not successful. Please go to the sigIRC folder and make sure your oauthToken.txt file is correct!!! SwiftRage");
		}
		if (sigIRC.lastPlayedDing>0) {
			sigIRC.lastPlayedDing--;
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
		for (Module m : sigIRC.modules) {
			m.run();
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
