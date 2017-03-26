package sig;
import java.util.Random;

public class TextRow {
	private int maxX = 0; //Defines the greatest X position of all the messages in this row.
	private int ypos = 0;
	final int MESSAGE_SEPARATION=200; 
	
	public TextRow(int ypos) {
		this.ypos=ypos;
	}

	public int getY() {
		return ypos;
	}

	public void setY(int ypos) {
		this.ypos = ypos;
	}

	public int getMaxX() {
		return maxX;
	}
	
	public void updateRow(ScrollingText text) {
		text.setX(maxX+sigIRC.panel.getWidth()+MESSAGE_SEPARATION);
		text.setY(ypos);
		maxX+=text.getStringWidth()+MESSAGE_SEPARATION;
	}
	
	public void update() {
		if (maxX>0) {
			maxX-=sigIRC.TEXTSCROLLSPD;
		}
	}
	
	public static TextRow PickRandomTextRow(String username) {
		Random r = new Random();
		r.setSeed(username.hashCode());
		int randomnumb = r.nextInt(sigIRC.rowobj.size());
		return sigIRC.rowobj.get(randomnumb);
	}
}
