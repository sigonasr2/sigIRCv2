package sig;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateEvent implements ActionListener{
	@Override
	public void actionPerformed(ActionEvent ev) {
	    UpdateScrollingText();
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
		for (TextRow tr : sigIRC.rowobj) {
			tr.update();
		}
		for (CustomSound cs : sigIRC.customsounds) {
			if (!cs.isSoundAvailable()) {
				cs.decreaseCooldown(1);
			}
		}
		for (Module m : sigIRC.modules) {
			m.run();
		}
	}
}
