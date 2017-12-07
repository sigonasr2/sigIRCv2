package sig.modules.ChatLog;

import java.awt.Graphics;

import javax.swing.SwingUtilities;

import sig.Emoticon;
import sig.Module;
import sig.ScrollingText;
import sig.sigIRC;

public class ChatLogTwitchEmote {
	Emoticon emote;
	int x=0; //X Offset
	int y=0; //Y Offset
	ChatLogMessage text;
	
	public ChatLogTwitchEmote(Emoticon emote, ChatLogMessage textref, int x, int y) {
		this.emote=emote;
		this.x=x;
		this.y=y+24-emote.getImage().getHeight();
		this.text = textref;
	}
	
	public boolean run() {
		//this.x-=paint.TEXTSCROLLSPD;
		/*if (textRefIsVisible()) {
			SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	            	sigIRC.panel.repaint(
						Math.max(x,0), 
						Math.max(y, 0), 
						Math.min(sigIRC.panel.getWidth()-x,emote.getImage().getWidth()), 
						Math.min(sigIRC.panel.getHeight()-y,emote.getImage().getHeight()));
			    }  
			});
		}*/
		if (text==null || !text.active) {
			return false;
		} else {
			return true;
		}
	}

	public void draw(Graphics g) {
		if (WithinBounds((int)(text.position.getX()+x), (int)(text.position.getY()+y-Module.WINDOW_EXTRA_BORDER), emote.getImage().getWidth(), emote.getImage().getHeight())) {
			//g.drawString("Emote", (int)(text.position.getX()+x), (int)(text.position.getY()+y));
			//System.out.println("Emote Pos: "+(int)(text.position.getX()+x)+","+(int)(text.position.getY()+y));
			g.drawImage(emote.getImage(), (int)(text.position.getX()+x), (int)(text.position.getY()+y-Module.WINDOW_EXTRA_BORDER), text.refModule.panel);
		}
	}

	private boolean WithinBounds(double x, double y, double w, double h) {
		if (x<text.refModule.panel.getWidth() && x+w>0 && y<text.refModule.panel.getHeight() && y+h>0) {
			return true;
		}
		return false;
	}

	public boolean textRefIsVisible() {
		return text.isVisible();
	}
}
