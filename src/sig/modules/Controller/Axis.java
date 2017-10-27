package sig.modules.Controller;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import sig.sigIRC;
import sig.modules.ControllerModule;

public class Axis extends Element{
	List<Integer> identifiers = new ArrayList<Integer>();
	boolean twoWayAxis = false; //True = 4-way, False = 2-way
	Color backgroundColor=Color.BLACK,indicatorColor=Color.WHITE;
	Controller parent_controller;
	ControllerModule parent;
	double range1,range2; //Range of motion.
	int orientation; //0=Left-to-Right, 1=Right-to-Left, 2=Bottom-to-Top, 3=Top-to-Bottom
	boolean visible=false;
	boolean x_invert,y_invert,axis_invert;
	
	/**
	 * 4-way axis Constructor.
	 */
	public Axis(Rectangle2D.Double rect,
			Controller parent_controller,
			Integer identifier,
			Integer identifier2,
			Color background_color,
			Color indicator_color,
			boolean x_invert,
			boolean y_invert,
			boolean axis_invert,
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
		this.x_invert = x_invert;
		this.y_invert = y_invert;
		this.axis_invert = axis_invert;
	}

	/**
	 * 2-way axis Constructor.
	 */
	public Axis(Rectangle2D.Double rect,
			Controller parent_controller,
			Integer identifier,
			double starting_range,
			double ending_range,
			int orientation,
			Color background_color,
			Color indicator_color,
			boolean x_invert,
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
		this.x_invert = x_invert;
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

	public Color getSelectionColor() {
		return backgroundColor;
	}
	
	public boolean is_Xinverted() {
		return x_invert;
	}

	public boolean is_Yinverted() {
		return y_invert;
	}

	public boolean is_Axisinverted() {
		return axis_invert;
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

	public List<Integer> getIdentifiers() {
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
        	if (a.identifiers.size()>=1 && a.identifiers.get(0)!=-1) {
        		val=a.parent_controller.getAxisValue(a.identifiers.get(0))*((a.x_invert)?-1:1);
        		//val=a.parent_controller.getComponent(a.identifiers.get(0)).getPollData();
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
        	if (a.identifiers.size()>0 && a.identifiers.get(0)!=null) {
        		xval = a.parent_controller.getAxisValue(a.identifiers.get(0))*((a.x_invert)?-1:1);
        	}
        	if (a.identifiers.size()>1 && a.identifiers.get(1)!=null) {
        		yval = a.parent_controller.getAxisValue(a.identifiers.get(1))*((a.y_invert)?-1:1);
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
	

	
	public String getSaveString() {
		StringBuilder sb = new StringBuilder();
		sb.append(pct_x);sb.append(",");
		sb.append(pct_y);sb.append(",");
		sb.append(pct_width);sb.append(",");
		sb.append(pct_height);sb.append(",");
		sb.append(twoWayAxis);sb.append(",");
		if (twoWayAxis) {
			sb.append((identifiers.size()>0 &&
					identifiers.get(0)!=null)?identifiers.get(0):"null");sb.append(",");
			sb.append(range1);sb.append(",");
			sb.append(range2);sb.append(",");
			sb.append(orientation);sb.append(",");	
		} else {
			sb.append((identifiers.size()>0 &&
					identifiers.get(0)!=null)?identifiers.get(0):"null");sb.append(",");
			sb.append((identifiers.size()>1 &&
					identifiers.get(1)!=null)?identifiers.get(1):"null");sb.append(",");
		}
		sb.append(backgroundColor.getRed());sb.append(",");
		sb.append(backgroundColor.getGreen());sb.append(",");
		sb.append(backgroundColor.getBlue());sb.append(",");
		sb.append(backgroundColor.getAlpha());sb.append(",");
		sb.append(indicatorColor.getRed());sb.append(",");
		sb.append(indicatorColor.getGreen());sb.append(",");
		sb.append(indicatorColor.getBlue());sb.append(",");
		sb.append(indicatorColor.getAlpha());sb.append(",");
		sb.append(x_invert);sb.append(",");
		if (!twoWayAxis) {
			sb.append(y_invert);sb.append(",");
			sb.append(axis_invert);
		}
		return sb.toString();
	}
	
	public static Axis loadFromString(String s, Controller controller, ControllerModule module) {
		String[] split = s.split(",");
		int i=0;
		Rectangle2D.Double rect = new Rectangle2D.Double(Double.parseDouble(split[i++]), Double.parseDouble(split[i++]), Double.parseDouble(split[i++]), Double.parseDouble(split[i++]));
		boolean twoway = Boolean.parseBoolean(split[i++]);
		if (twoway) {
			return new Axis(rect,controller,
					Integer.parseInt(split[i++]),
					Double.parseDouble(split[i++]),
					Double.parseDouble(split[i++]),
					Integer.parseInt(split[i++]),
					new Color(Integer.parseInt(split[i++]),Integer.parseInt(split[i++]),Integer.parseInt(split[i++]),Integer.parseInt(split[i++])),
					new Color(Integer.parseInt(split[i++]),Integer.parseInt(split[i++]),Integer.parseInt(split[i++]),Integer.parseInt(split[i++])),
					Boolean.parseBoolean(split[i++]),
					module
					);
		} else {
			return new Axis(rect,controller,
					Integer.parseInt(split[i++]),
					Integer.parseInt(split[i++]),
					new Color(Integer.parseInt(split[i++]),Integer.parseInt(split[i++]),Integer.parseInt(split[i++]),Integer.parseInt(split[i++])),
					new Color(Integer.parseInt(split[i++]),Integer.parseInt(split[i++]),Integer.parseInt(split[i++]),Integer.parseInt(split[i++])),
					Boolean.parseBoolean(split[i++]),
					Boolean.parseBoolean(split[i++]),
					Boolean.parseBoolean(split[i++]),
					module
					);
		}
				/*Double.parseDouble(split[i++]),
				Double.parseDouble(split[i++]),
				Double.parseDouble(split[i++]),
				Double.parseDouble(split[i++]),
				controller,
				GrabIdentifierFromString(split[i++],controller),
				Float.parseFloat(split[i++]),
				new Color(
						Integer.parseInt(split[i++]),
						Integer.parseInt(split[i++]),
						Integer.parseInt(split[i++]),
						Integer.parseInt(split[i++])
						),
				module,
				Boolean.parseBoolean(split[i++]));*/
	}
}
