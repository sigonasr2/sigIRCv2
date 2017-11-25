package sig;
import java.util.Random;

public class TextRow {
	private int maxX = 0; //Defines the greatest X position of all the messages in this row.
	private int ypos = 0;
	final int MESSAGE_SEPARATION=200; 
	private int scrollSpd = sigIRC.BASESCROLLSPD;
	
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
	
	public int getScrollSpd() {
		return scrollSpd;
	}
	
	public void updateRow(ScrollingText text) {
		text.setX(maxX+sigIRC.panel.getWidth()+MESSAGE_SEPARATION);
		text.setY(ypos);
		text.setTextRow(this);
		maxX+=text.getStringWidth()+MESSAGE_SEPARATION;
	}
	
	public void update() {
		scrollSpd = DetermineScrollSpd();
		if (maxX>0) {
			maxX-=scrollSpd;
		}
	}
	
	private int DetermineScrollSpd() {
		return maxX/Math.max(600,sigIRC.windowWidth)+(sigIRC.chatScrollSpd*2);
	}

	public static TextRow PickRandomTextRow(String username) {
		Random r = new Random();
		r.setSeed(username.hashCode());
		int randomnumb = r.nextInt(sigIRC.rowobj.size());
		return sigIRC.rowobj.get(randomnumb);
	}
}
