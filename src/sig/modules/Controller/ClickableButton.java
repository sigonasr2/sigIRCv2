package sig.modules.Controller;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import sig.sigIRC;
import sig.modules.ControllerModule;
import sig.utils.DrawUtils;
import sig.utils.TextUtils;

public class ClickableButton {
	int x,y,width,height;
	String label;
	ControllerModule module;
	
	public ClickableButton(Rectangle position, String button_label, ControllerModule parent_module) {
		this.x = (int)position.getX();
		this.y = (int)position.getY();
		this.width = (int)position.getWidth();
		this.height = (int)position.getHeight();
		this.label=button_label;
		this.module = parent_module;
	}
	
	public void onClickEvent(MouseEvent ev) {
		if (mouseInsideBounds(ev)) {
			System.out.println("Click performed!");
		}
	}

	private boolean mouseInsideBounds(MouseEvent ev) {
		return ev.getX()>=x && ev.getX()<=x+width &&
				ev.getY()>=y && ev.getY()<=y+height;
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
