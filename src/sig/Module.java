package sig;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import sig.utils.DrawUtils;
import sig.utils.TextUtils;

public class Module extends JFrame{
	public JPanel panel;
	public Rectangle position;
	protected boolean enabled;
	protected String name;
	public static BufferedImage IMG_DRAGBAR;
	public static BufferedImage MSG_SEPARATOR;
	public static boolean inDragZone=false;
	
	final protected int titleHeight;
	
	Point dragOffset;
	boolean dragging=false;
	public static boolean DRAGGING=false;
	public Graphics myGraphics;

	public Module(Rectangle bounds, String moduleName) {
		this.position = bounds;
		this.name = moduleName;
		this.enabled=true;
		this.setVisible(true);
		panel = new JPanel(){
		    public void paintComponent(Graphics g) {
		    	super.paintComponent(g);
		    	draw(g);
		    }
		};
		
		this.titleHeight = (int)TextUtils.calculateStringBoundsFont(this.name, sigIRC.userFont).getHeight();
		
		this.setSize((int)position.getWidth(), (int)position.getHeight());
		panel.setSize(this.getSize());
		
		this.add(panel);

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleWithFixedDelay(()->{
			panel.repaint();
		},(long)((1d/sigIRC.framerate)*1000),(long)((1d/sigIRC.framerate)*1000),TimeUnit.MILLISECONDS);
	}
	
	public Module(Rectangle bounds, String moduleName, boolean enabled) {
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
		if (!sigIRC.overlayMode && !dragging && inDragBounds(mouseX,mouseY) && !DRAGGING) {
			//Enable dragging.
			dragOffset = new Point((int)position.getX() - mouseX,(int)position.getY()-mouseY);
			dragging=DRAGGING=true;
		}
	}
	
	public boolean inDragBounds(int x, int y) {
		return x>=position.getX() && x<=position.getX()+position.getWidth() &&
				y>=(int)position.getY()-Module.IMG_DRAGBAR.getHeight() &&
				y<=(int)position.getY();
	}

	public void mousePressed(MouseEvent ev) {
	}
	
	public void ApplyConfigWindowProperties() {
	}
	
	public void SaveConfig() {
		
	}

	public void mouseReleased(MouseEvent ev) {
		if (dragging) {
			dragging=DRAGGING=false;
			ApplyConfigWindowProperties();
			sigIRC.config.saveProperties();
		}
	}
	
	protected void moduleRun() {
		dragWindow();
		modifyCursor();
		run();
	}

	private void modifyCursor() {
		if (!sigIRC.overlayMode) {
			int cursortype = sigIRC.panel.getCursor().getType();
			if (inDragZone &&
					cursortype!=Cursor.MOVE_CURSOR) {
				sigIRC.panel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
			} else 
			if (!inDragZone && cursortype!=Cursor.DEFAULT_CURSOR) {
				sigIRC.panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}

	private void dragWindow() {
		if (dragging) {
			//sigIRC.panel.repaint(getDrawBounds().getBounds());
			int mouseX = sigIRC.panel.lastMouseX+(int)dragOffset.getX();
			int mouseY = sigIRC.panel.lastMouseY+(int)dragOffset.getY();
			int oldX = (int)position.getX();
			int oldY = (int)position.getY();
			position = new Rectangle((int)Math.min(Math.max(0,mouseX),sigIRC.window.getWidth()-position.getWidth()), (int)Math.min(Math.max(titleHeight,mouseY),sigIRC.window.getHeight()-position.getHeight()-titleHeight*2),(int)position.getWidth(),(int)position.getHeight());
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
	
	public Rectangle2D getPosition() {
		return position;
	}
	
	public void run() {
	}
	
	public void draw(Graphics g) {
		g.fillRect(0, 0, (int)position.getWidth(), (int)position.getHeight());
		DrawUtils.drawText(g, 0, 16, Color.WHITE, "Test");
		System.out.println("Test");
	}

	private void drawModuleHeader(Graphics g) {
		if (!sigIRC.overlayMode) {
			g.drawImage(Module.IMG_DRAGBAR, 
				(int)position.getX()+2, 
				(int)position.getY()-Module.IMG_DRAGBAR.getHeight(),
				(int)position.getWidth()-4,
				Module.IMG_DRAGBAR.getHeight(),
				sigIRC.panel);
			DrawUtils.drawTextFont(g, sigIRC.smallFont, (int)position.getX(), (int)position.getY()-titleHeight/2+4, Color.BLACK, this.name);
			//g.fillRect((int)position.getX(), (int)position.getY(), (int)position.getWidth(), (int)position.getHeight());
		}
	}
	
	private Rectangle2D getDrawBounds() {
		Rectangle2D drawBounds = new Rectangle((int)position.getX()-2,(int)position.getY()-titleHeight+3-1,(int)position.getWidth()+2,(int)position.getHeight()+titleHeight+1);
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
	
	public void windowClosed(WindowEvent ev) {
		
	}
}
