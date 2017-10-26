package sig.modules.Controller;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Component.Identifier;
import sig.sigIRC;
import sig.modules.ControllerModule;

public class Axis {
	List<Identifier> identifiers = new ArrayList<Identifier>();
	boolean twoWayAxis = false; //True = 4-way, False = 2-way
	Color backgroundColor=Color.BLACK,indicatorColor=Color.WHITE;
	double pct_x = 0;
	double pct_y = 0;
	double pct_width = 0;
	double pct_height = 0;
	Controller parent_controller;
	ControllerModule parent;
	double range1,range2; //Range of motion.
	int orientation; //0=Left-to-Right, 1=Right-to-Left, 2=Bottom-to-Top, 3=Top-to-Bottom
	boolean visible=false;
	
	/**
	 * 4-way axis Constructor.
	 */
	public Axis(Rectangle2D.Double rect,
			Controller parent_controller,
			Identifier identifier,
			Identifier identifier2,
			Color background_color,
			Color indicator_color,
			ControllerModule module) {
		this.twoWayAxis=false;
		this.pct_x = rect.getX();
		this.pct_y = rect.getY();
		this.pct_width=rect.getWidth();
		this.pct_height=rect.getHeight();
		if (identifier!=null) {
			identifiers.add(identifier);
		}
		if (identifier2!=null) {
			identifiers.add(identifier2);
		}
		this.parent_controller = parent_controller;
		this.parent = module;
		this.backgroundColor = background_color;
		this.indicatorColor = indicator_color;
	}

	/**
	 * 2-way axis Constructor.
	 */
	public Axis(Rectangle2D.Double rect,
			Controller parent_controller,
			Identifier identifier,
			double starting_range,
			double ending_range,
			int orientation,
			Color background_color,
			Color indicator_color,
			ControllerModule module) {
		this.twoWayAxis=true;
		this.pct_x = rect.getX();
		this.pct_y = rect.getY();
		this.pct_width=rect.getWidth();
		this.pct_height=rect.getHeight();
		if (identifier!=null) {
			identifiers.add(identifier);
		}
		this.parent_controller = parent_controller;
		this.parent = module;
		this.range1 = starting_range;
		this.range2 = ending_range;
		this.orientation = orientation;
		this.backgroundColor = background_color;
		this.indicatorColor = indicator_color;
	}
	
	public void draw(Graphics g) {
		if (visible) {
			GetAxisDisplay(g,this,
					parent.getPosition().getX()+pct_x*parent.getControllerImage().getWidth(sigIRC.panel),
					parent.getPosition().getY()+pct_y*parent.getControllerImage().getHeight(sigIRC.panel),
					pct_width*parent.getControllerImage().getWidth(sigIRC.panel),
					pct_height*parent.getControllerImage().getHeight(sigIRC.panel));
		}
	}
	
	public void setupBoundsRectangle(Rectangle2D.Double rect) {
		this.pct_x = rect.getX();
		this.pct_y = rect.getY();
		this.pct_width=rect.getWidth();
		this.pct_height=rect.getHeight();
	}
	
	public boolean isTwoWayAxis() {
		return twoWayAxis;
	}
	
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public List<Identifier> getIdentifiers() {
		return identifiers;
	}

	public Color getIndicatorColor() {
		return indicatorColor;
	}

	public double getPctX() {
		return pct_x;
	}

	public double getPctY() {
		return pct_y;
	}

	public double getPctWidth() {
		return pct_width;
	}

	public double getPctHeight() {
		return pct_height;
	}

	public Controller getController() {
		return parent_controller;
	}

	public ControllerModule getModule() {
		return parent;
	}

	public double getRange1() {
		return range1;
	}

	public double getRange2() {
		return range2;
	}

	public int getOrientation() {
		return orientation;
	}

	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible=visible;
	}
	
	public static void GetAxisDisplay(Graphics g, Axis a, double x, double y, double xscale, double yscale) {
		if (a.twoWayAxis) {
        	Color color_identity = g.getColor();
        	g.setColor(a.backgroundColor);
        	g.fillRect((int)x, (int)y, (int)xscale, (int)yscale);
        	g.setColor(a.indicatorColor);
        	double val = 0;
        	if (a.identifiers.size()>=1) {
        		val=a.parent_controller.getComponent(a.identifiers.get(0)).getPollData();
        	}
        	double val1 = a.range1;
        	double val2 = a.range2;
        	double range_of_motion = (Math.abs(val1)+Math.abs(val2));
        	double smallest_val = 0;
        	if (val1<0 || val2<0) {
        		smallest_val = Math.abs(Math.min(val1, val2));
        	} 
        	double area_covered_x = (int)(((val+smallest_val)/range_of_motion)*xscale);
        	double area_covered_y = (int)(((val+smallest_val)/range_of_motion)*yscale);
        	switch (a.orientation) {
	        	case 0:{
	        		g.fillRect((int)x, (int)y, (int)area_covered_x, (int)yscale);
	        	}break;
	        	case 1:{
	        		g.fillRect((int)(xscale-area_covered_x+x), (int)y, (int)area_covered_x, (int)yscale);
	        	}break;
	        	case 2:{
	        		g.fillRect((int)x, (int)(yscale-area_covered_y+y), (int)xscale, (int)area_covered_y);
	        	}break;
	        	case 3:{
	        		g.fillRect((int)x, (int)y, (int)xscale, (int)area_covered_y);
	        	}break;
	    	}
        	g.setColor(color_identity);
        } else {
        	double xval=0;
        	double yval=0;
        	for (int i=0;i<a.identifiers.size();i++) {
        		Identifier ident = a.identifiers.get(i);
        		if (ident.getName().contains("x") ||
        				ident.getName().contains("X")) {
        			xval = a.parent_controller.getComponent(ident).getPollData();
        		} else
        		if (ident.getName().contains("y") ||
        				ident.getName().contains("Y")) {
        			yval = a.parent_controller.getComponent(ident).getPollData();
        		}  
        	}
        	Color color_identity = g.getColor();
        	g.setColor(a.backgroundColor);
    		g.fillOval((int)x, (int)y, (int)xscale, (int)yscale);
        	g.setColor(a.indicatorColor);
        	for (int i=-1;i<2;i++) {
            	for (int j=-1;j<2;j++) {
            		g.drawOval((int)(((xval+1)*12*(xscale/32d))+i+x), (int)(((yval+1)*12*(yscale/32d))+j+y), (int)(8d*(xscale/32d)), (int)(8d*(yscale/32d)));
            	}
        	}
        	g.setColor(color_identity);
        }
	}
}
