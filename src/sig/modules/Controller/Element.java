package sig.modules.Controller;

import java.awt.Color;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

import sig.sigIRC;
import sig.modules.ControllerModule;

public class Element {
	protected double pct_x = 0;
	protected double pct_y = 0;
	protected double pct_width = 0;
	protected double pct_height = 0;
	
	public Rectangle2D.Double getBounds() {
		return new Rectangle2D.Double(pct_x,pct_y,pct_width,pct_height);
	}
	public Rectangle2D.Double getPixelBounds(Image controller_img) {
		return new Rectangle2D.Double(pct_x*controller_img.getWidth(sigIRC.panel),pct_y*controller_img.getHeight(sigIRC.panel),pct_width*controller_img.getWidth(sigIRC.panel),pct_height*controller_img.getHeight(sigIRC.panel));
	}
	
	public void setBounds(Rectangle2D.Double rect) {
		 this.pct_x = rect.getX();
		 this.pct_y = rect.getY();
		 this.pct_width = rect.getWidth();
		 this.pct_height = rect.getHeight();
	}
	
	public Color getElementColor() {
		if (this instanceof Button) {
			Button b = (Button)this;
			return b.getSelectionColor();
		} else
		if (this instanceof Axis) {
			Axis a = (Axis)this;
			return a.getSelectionColor();
		}
		return null;
	}
	public void remove(ControllerModule module) {
		if (this instanceof Button) {
			module.getButtons().remove(this);
			module.SaveButtonData();
		} else
		if (this instanceof Axis) {
			module.getAxes().remove(this);
			module.SaveAxisData();
		}
	}
}
