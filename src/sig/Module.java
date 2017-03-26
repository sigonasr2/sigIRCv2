package sig;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;

public class Module {
	protected Rectangle2D bounds;
	protected boolean enabled;
	protected String name;

	public Module(Rectangle2D bounds, String moduleName) {
		this.bounds = bounds;
		this.name = moduleName;
		this.enabled=true;
	}
	
	public Module(Rectangle2D bounds, String moduleName, boolean enabled) {
		this.bounds = bounds;
		this.name = moduleName;
		this.enabled=enabled;
	}
	
	public void mousePressed(MouseEvent ev) {
	}
	
	public void run() {
	}
	
	public void draw(Graphics g) {
		sigIRC.panel.repaint(bounds.getBounds());
	}

	public void mouseWheel(MouseWheelEvent ev) {
	}
}
