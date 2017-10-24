package sig.modules.Controller;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import net.java.games.input.Component.Identifier;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import sig.sigIRC;
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
	
	public Button(Rectangle2D.Double rect, Controller parent_controller, Identifier button_identifier, Color col, ControllerModule module) {
		this(rect.getX(),rect.getY(),rect.getWidth(),rect.getHeight(),parent_controller,button_identifier,col,module,false);
	}
	
	public Button(Rectangle2D.Double rect, Controller parent_controller, Identifier button_identifier, Color col, ControllerModule module, boolean square) {
		this(rect.getX(),rect.getY(),rect.getWidth(),rect.getHeight(),parent_controller,button_identifier,col,module,square);
	}
	
	public Button(double pct_x, double pct_y, double pct_width, double pct_height, Controller parent_controller, Identifier button_identifier, Color col, ControllerModule module) {
		this(pct_x,pct_y,pct_width,pct_height,parent_controller,button_identifier,col,module,false);
	}
	
	public Button(double pct_x, double pct_y, double pct_width, double pct_height, Controller parent_controller, Identifier button_identifier, Color col, ControllerModule module, boolean square) {
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
						+parent.getControllerImage().getWidth(sigIRC.panel)*pct_x)
					,(int)(parent.getPosition().getY()
						+parent.getControllerImage().getHeight(sigIRC.panel)*pct_y)
					,(int)(parent.getControllerImage().getWidth(sigIRC.panel)*pct_width),
					(int)(parent.getControllerImage().getHeight(sigIRC.panel)*pct_height));
			g.setColor(col_identity);
		}
	}
	
	public String getSaveString() {
		StringBuilder sb = new StringBuilder();
		sb.append(pct_x);sb.append(",");
		sb.append(pct_y);sb.append(",");
		sb.append(pct_width);sb.append(",");
		sb.append(pct_height);sb.append(",");
		sb.append(ident.getName());sb.append(",");
		sb.append(pressed_col.getRed());sb.append(",");
		sb.append(pressed_col.getGreen());sb.append(",");
		sb.append(pressed_col.getBlue());sb.append(",");
		sb.append(pressed_col.getAlpha());sb.append(",");
		sb.append(square);
		return sb.toString();
	}
	
	public static Button loadFromString(String s, Controller controller, ControllerModule module) {
		String[] split = s.split(",");
		return new Button(
				Double.parseDouble(split[0]),
				Double.parseDouble(split[1]),
				Double.parseDouble(split[2]),
				Double.parseDouble(split[3]),
				controller,
				GrabIdentifierFromString(split[4],controller),
				new Color(
						Integer.parseInt(split[5]),
						Integer.parseInt(split[6]),
						Integer.parseInt(split[7]),
						Integer.parseInt(split[8])
						),
				module,
				Boolean.parseBoolean(split[9]));
	}

	private static Identifier GrabIdentifierFromString(String string, Controller controller) {
		for (Component cp : controller.getComponents()) {
			Identifier id = cp.getIdentifier();
			if (id.getName().equals(string)) {
				return id;
			}
		}
		return null;
	}
}
