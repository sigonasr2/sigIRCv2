package sig;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.SwingUtilities;

import sig.utils.DrawUtils;
import sig.utils.TextUtils;

public class Module {
	protected Rectangle2D bounds;
	protected boolean enabled;
	protected String name;
	public static BufferedImage IMG_DRAGBAR;
	public static boolean inDragZone=false;
	
	final protected int titleHeight;
	
	Point dragOffset;
	boolean dragging=false;

	public Module(Rectangle2D bounds, String moduleName) {
		this.bounds = bounds;
		this.name = moduleName;
		this.enabled=true;
		
		this.titleHeight = (int)TextUtils.calculateStringBoundsFont(this.name, sigIRC.panel.userFont).getHeight();
	}
	
	public Module(Rectangle2D bounds, String moduleName, boolean enabled) {
		this(bounds, moduleName);
		this.enabled=enabled;
	}
	
	protected void mouseModuleMousePress(MouseEvent ev) {
		int mouseX = ev.getX();
		int mouseY = ev.getY();
		//System.out.println(mouseX + "," + mouseY);
		enableWindowDrag(mouseX,mouseY);
		mousePressed(ev);
	}
	
	private void enableWindowDrag(int mouseX, int mouseY) {
		if (!dragging && inDragBounds(mouseX,mouseY)) {
			//Enable dragging.
			dragOffset = new Point((int)bounds.getX() - mouseX,(int)bounds.getY()-mouseY);
			dragging=true;
		}
	}
	
	public boolean inDragBounds(int x, int y) {
		return x>=bounds.getX() && x<=bounds.getX()+bounds.getWidth() &&
				y>=(int)bounds.getY()-Module.IMG_DRAGBAR.getHeight() &&
				y<=(int)bounds.getY();
	}

	public void mousePressed(MouseEvent ev) {
	}

	public void mouseReleased(MouseEvent ev) {
		if (dragging) {
			dragging=false;
		}
	}
	
	protected void moduleRun() {
		dragWindow();
		modifyCursor();
		run();
	}

	private void modifyCursor() {
		int cursortype = sigIRC.panel.getCursor().getType();
		if (inDragZone &&
				cursortype!=Cursor.MOVE_CURSOR) {
			sigIRC.panel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
		} else 
		if (!inDragZone && cursortype!=Cursor.DEFAULT_CURSOR) {
			sigIRC.panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	private void dragWindow() {
		if (dragging) {
			sigIRC.panel.repaint(getDrawBounds().getBounds());
			int mouseX = sigIRC.panel.lastMouseX+(int)dragOffset.getX();
			int mouseY = sigIRC.panel.lastMouseY+(int)dragOffset.getY();
			int oldX = (int)bounds.getX();
			int oldY = (int)bounds.getY();
			bounds = new Rectangle(mouseX, mouseY,(int)bounds.getWidth(),(int)bounds.getHeight());
			//System.out.println(sigIRC.panel.lastMouseX+","+sigIRC.panel.lastMouseY);
			ModuleDragEvent(oldX,oldY,mouseX,mouseY);
		}
		if (inDragBounds(sigIRC.panel.lastMouseX,sigIRC.panel.lastMouseY)) {
			inDragZone=true;
			//System.out.println("In Drag Zone for Module "+name);
			//sigIRC.panel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
		} /*else
		if (sigIRC.panel.getCursor().getType()==Cursor.MOVE_CURSOR) {
			sigIRC.panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}*/
	}
	
	public void run() {
	}
	
	public void draw(Graphics g) {
		drawModuleHeader(g);
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	sigIRC.panel.repaint(getDrawBounds().getBounds());
            }  
        });
	}

	private void drawModuleHeader(Graphics g) {
		g.drawImage(Module.IMG_DRAGBAR, 
			(int)bounds.getX()+2, 
			(int)bounds.getY()-Module.IMG_DRAGBAR.getHeight(),
			(int)bounds.getWidth()-4,
			Module.IMG_DRAGBAR.getHeight(),
			sigIRC.panel);
		DrawUtils.drawTextFont(g, sigIRC.panel.smallFont, (int)bounds.getX(), (int)bounds.getY()-titleHeight/2+4, Color.BLACK, this.name);
	}
	
	private Rectangle2D getDrawBounds() {
		Rectangle2D drawBounds = new Rectangle((int)bounds.getX(),(int)bounds.getY()-titleHeight+3,(int)bounds.getWidth(),(int)bounds.getHeight()+titleHeight);
		return drawBounds;
	}
	
	public void ModuleDragEvent(int oldX, int oldY, int newX, int newY) {
		
	}

	public void mouseWheel(MouseWheelEvent ev) {
	}

	public void keypressed(KeyEvent ev) {
		
	}

	public void keyreleased(KeyEvent ev) {
	}
}
