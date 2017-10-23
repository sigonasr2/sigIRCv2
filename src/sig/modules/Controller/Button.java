package sig.modules.Controller;

import java.awt.Color;
import java.awt.Graphics;

import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import sig.modules.ControllerModule;

public class Button {
	double pct_x = 0;
	double pct_y = 0;
	double pct_width = 0;
	double pct_height = 0;
	Identifier ident;
	Controller parent_controller;
	Color pressed_col;
	ControllerModule parent;
	boolean square;
	
	public Button(double pct_x, double pct_width, double pct_y, double pct_height, Controller parent_controller, Identifier button_identifier, Color col, ControllerModule module) {
		this(pct_x,pct_width,pct_y,pct_height,parent_controller,button_identifier,col,module,false);
	}
	
	public Button(double pct_x, double pct_width, double pct_y, double pct_height, Controller parent_controller, Identifier button_identifier, Color col, ControllerModule module, boolean square) {
		this.pct_x = pct_x;
		this.pct_y = pct_y;
		this.pct_width=pct_width;
		this.pct_height=pct_height;
		this.parent_controller=parent_controller;
		this.ident = button_identifier;
		this.pressed_col=col;
		this.parent = module;
		this.square = square;
	}
	
	public void draw(Graphics g) {
		if (parent_controller.getComponent(ident).getPollData()==1) {
			Color col_identity = g.getColor();
			g.setColor(pressed_col);
			g.fillOval((int)(parent.getPosition().getX()
						+parent.getPosition().getWidth()*pct_x)
					,(int)(parent.getPosition().getY()
						+parent.getPosition().getWidth()*pct_y)
					,(int)(parent.getPosition().getWidth()*pct_width),
					(int)(parent.getPosition().getWidth()*pct_height));
			g.setColor(col_identity);
		}
	}
}
