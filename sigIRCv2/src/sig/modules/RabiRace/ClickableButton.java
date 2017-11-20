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
	protected int x,y,width,height;
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
		return ev.getX()>=module.getPosition().getX()+x && ev.getX()<=module.getPosition().getX()+x+width &&
				ev.getY()>=module.getPosition().getY()+y && ev.getY()<=module.getPosition().getY()+y+height;
	}
	
	public void draw(Graphics g) {
		Color color_identity = g.getColor();
		g.setColor(Color.WHITE);
		g.drawRect((int)module.getPosition().getX()+x, 
				(int)module.getPosition().getY()+y, width, height);
		g.setColor(Color.BLACK);
		g.fillRect((int)module.getPosition().getX()+x+1, 
				(int)module.getPosition().getY()+y+1, width-1, height-1);
		DrawUtils.drawTextFont(g, sigIRC.panel.userFont, module.getPosition().getX()+x-TextUtils.calculateStringBoundsFont(label, sigIRC.panel.userFont).getWidth()/2+width/2, module.getPosition().getY()+y+height-1, Color.WHITE, label);
		g.setColor(color_identity);
	}
}
