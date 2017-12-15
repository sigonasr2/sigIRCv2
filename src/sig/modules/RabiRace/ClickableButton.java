package sig.modules.RabiRace;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import sig.Module;
import sig.sigIRC;
import sig.modules.ControllerModule;
import sig.modules.RabiRaceModule;
import sig.utils.DrawUtils;
import sig.utils.TextUtils;

public class ClickableButton {
	public int x,y,width,height;
	protected String label;
	protected RabiRaceModule module;
	
	public ClickableButton(Rectangle position, String button_label, RabiRaceModule parent_module) {
		this.x = (int)position.getX();
		this.y = (int)position.getY();
		this.width = (int)position.getWidth();
		this.height = (int)position.getHeight();
		this.label=button_label;
		this.module = parent_module;
	}
	
	public void onClickEvent(MouseEvent ev) {
		/*if (mouseInsideBounds(ev)) {
			//System.out.println("Click performed!");
		}*/
	}
	
	public void setButtonLabel(String text) {
		this.label = text;
	}

	public boolean mouseInsideBounds(MouseEvent ev) {
		return ev.getX()>=0+x && ev.getX()<=0+x+width &&
				ev.getY()>=0+y && ev.getY()<=0+y+height;
	}
	
	public void draw(Graphics g) {
		Color color_identity = g.getColor();
		g.setColor(Color.WHITE);
		g.drawRect((int)0+x, 
				(int)0+y, width, height);
		g.setColor(Color.BLACK);
		g.fillRect((int)0+x+1, 
				(int)0+y+1, width-1, height-1);
		DrawUtils.drawTextFont(g, sigIRC.userFont, 0+x-TextUtils.calculateStringBoundsFont(label, sigIRC.userFont).getWidth()/2+width/2, 0+y+height-1, Color.WHITE, label);
		g.setColor(color_identity);
	}
}
