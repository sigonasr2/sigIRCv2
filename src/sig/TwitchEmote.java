package sig;
import java.awt.Graphics;

import javax.swing.SwingUtilities;

public class TwitchEmote {
	Emoticon emote;
	int x=0; //X Offset
	int y=0; //Y Offset
	ScrollingText text;
	boolean active=true;
	
	public TwitchEmote(Emoticon emote, ScrollingText textref, int x, int y) {
		this.emote=emote;
		this.x=x;
		this.y=y+32-emote.getImage().getHeight();
		this.text = textref;
	}
	
	public boolean run() {
		//this.x-=paint.TEXTSCROLLSPD;
		/*SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	sigIRC.panel.repaint(
					Math.max(x,0), 
					Math.max(y, 0), 
					Math.min(sigIRC.panel.getWidth()-x,emote.getImage().getWidth()), 
					Math.min(sigIRC.panel.getHeight()-y,emote.getImage().getHeight()));
		    }  
		});*/
		if (x+emote.getImage().getWidth()<0 || text==null || !text.isActive()) {
			active=false;
			return false;
		} else {
			return true;
		}
	}

	public void draw(Graphics g) {
		if (WithinBounds((int)(text.getX()+x), (int)(text.getY()+y), emote.getImage().getWidth(), emote.getImage().getHeight())) {
			g.drawImage(emote.getImage(), (int)(text.getX()+x), (int)(text.getY()+y), sigIRC.panel);
		}
	}
	
	public boolean isActive() {
		return active;
	}

	private boolean WithinBounds(double x, double y, double w, double h) {
		if (x<sigIRC.panel.getWidth() && x+w>0 && y<sigIRC.panel.getHeight() && y+h>0) {
			return true;
		}
		return false;
	}

	public boolean textRefIsVisible() {
		return text.isVisible();
	}
}
