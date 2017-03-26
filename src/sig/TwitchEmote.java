package sig;
import java.awt.Graphics;

public class TwitchEmote {
	Emoticon emote;
	int x=0; //X Offset
	int y=0; //Y Offset
	ScrollingText text;
	
	public TwitchEmote(Emoticon emote, ScrollingText textref, int x, int y) {
		this.emote=emote;
		this.x=x;
		this.y=y+32-emote.getImage().getHeight();
		this.text = textref;
	}
	
	public boolean run() {
		//this.x-=paint.TEXTSCROLLSPD;
		sigIRC.panel.repaint(
				Math.max(x,0), 
				Math.max(y, 0), 
				Math.min(sigIRC.panel.getWidth()-x,emote.getImage().getWidth())+1, 
				Math.min(sigIRC.panel.getHeight()-y,emote.getImage().getHeight())+1);
		if (x+emote.getImage().getWidth()<0) {
			return false;
		} else {
			return true;
		}
	}

	public void draw(Graphics g) {
		g.drawImage(emote.getImage(), (int)(text.getX()+x), (int)(text.getY()+y), sigIRC.panel);
	}
}
