package sig.modules.Controller;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import sig.sigIRC;
import sig.modules.ControllerModule;

public class Button extends Element{
	int ident;
	byte value;
	Controller parent_controller;
	Color pressed_col;
	ControllerModule parent;
	boolean square;
	
	public Button(Rectangle2D.Double rect, Controller parent_controller, int button_identifier, byte button_val, Color col, ControllerModule module) {
		this(rect.getX(),rect.getY(),rect.getWidth(),rect.getHeight(),parent_controller,button_identifier,button_val,col,module,false);
	}
	
	public Button(Rectangle2D.Double rect, Controller parent_controller, int button_identifier, byte button_val, Color col, ControllerModule module, boolean square) {
		this(rect.getX(),rect.getY(),rect.getWidth(),rect.getHeight(),parent_controller,button_identifier,button_val,col,module,square);
	}
	
	public Button(double pct_x, double pct_y, double pct_width, double pct_height, Controller parent_controller, int button_identifier, byte button_val, Color col, ControllerModule module) {
		this(pct_x,pct_y,pct_width,pct_height,parent_controller,button_identifier,button_val,col,module,false);
	}
	
	public Button(double pct_x, double pct_y, double pct_width, double pct_height, Controller parent_controller, int button_identifier, byte button_val, Color col, ControllerModule module, boolean square) {
		this.pct_x = pct_x;
		this.pct_y = pct_y;
		this.pct_width=pct_width;
		this.pct_height=pct_height;
		this.parent_controller=parent_controller;
		this.ident = button_identifier;
		this.pressed_col=col;
		this.parent = module;
		this.square = square;
		this.value = button_val;
		module.setStoredRectangle(new Rectangle2D.Double(pct_x, pct_y, pct_width, pct_height));
	}
	
	public Color getSelectionColor() {
		return pressed_col;
	}
	
	public void draw(Graphics g) {
		if (parent_controller.getButtonValue(ident)==value) {
			Color col_identity = g.getColor();
			g.setColor(pressed_col);
			if (square) {
				g.fillRect((int)(parent.getPosition().getX()
						+parent.getControllerImage().getWidth(sigIRC.panel)*pct_x)
					,(int)(parent.getPosition().getY()
						+parent.getControllerImage().getHeight(sigIRC.panel)*pct_y)
					,(int)(parent.getControllerImage().getWidth(sigIRC.panel)*pct_width),
					(int)(parent.getControllerImage().getHeight(sigIRC.panel)*pct_height));
			} else {
				g.fillOval((int)(parent.getPosition().getX()
							+parent.getControllerImage().getWidth(sigIRC.panel)*pct_x)
						,(int)(parent.getPosition().getY()
							+parent.getControllerImage().getHeight(sigIRC.panel)*pct_y)
						,(int)(parent.getControllerImage().getWidth(sigIRC.panel)*pct_width),
						(int)(parent.getControllerImage().getHeight(sigIRC.panel)*pct_height));
			}
			g.setColor(col_identity);
		}
	}
	
	public String getSaveString() {
		StringBuilder sb = new StringBuilder();
		sb.append(pct_x);sb.append(",");
		sb.append(pct_y);sb.append(",");
		sb.append(pct_width);sb.append(",");
		sb.append(pct_height);sb.append(",");
		sb.append(ident);sb.append(",");
		sb.append(value);sb.append(",");
		sb.append(pressed_col.getRed());sb.append(",");
		sb.append(pressed_col.getGreen());sb.append(",");
		sb.append(pressed_col.getBlue());sb.append(",");
		sb.append(pressed_col.getAlpha());sb.append(",");
		sb.append(square);
		return sb.toString();
	}
	
	public static Button loadFromString(String s, Controller controller, ControllerModule module) {
		String[] split = s.split(",");
		int i=0;
		return new Button(
				Double.parseDouble(split[i++]),
				Double.parseDouble(split[i++]),
				Double.parseDouble(split[i++]),
				Double.parseDouble(split[i++]),
				controller,
				Integer.parseInt(split[i++]),
				Byte.parseByte(split[i++]),
				new Color(
						Integer.parseInt(split[i++]),
						Integer.parseInt(split[i++]),
						Integer.parseInt(split[i++]),
						Integer.parseInt(split[i++])
						),
				module,
				Boolean.parseBoolean(split[i++]));
	}
}
