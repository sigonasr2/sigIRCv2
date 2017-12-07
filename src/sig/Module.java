package sig;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import sig.utils.DrawUtils;
import sig.utils.TextUtils;
import sig.windows.ProgramWindow;

public class Module extends JFrame implements ComponentListener, WindowListener, KeyListener{
	public ListenerPanel panel;
	public Rectangle position;
	protected boolean enabled;
	protected String name;
	public static BufferedImage IMG_DRAGBAR;
	public static BufferedImage MSG_SEPARATOR;
	public static boolean inDragZone=false;
	final public static int WINDOW_EXTRA_BORDER = 32; //Number of pixels that the border takes up (Reduces the size of the window) 
	
	final protected int titleHeight;
	
	Point dragOffset;
	boolean dragging=false;
	public static boolean DRAGGING=false;
	public Graphics myGraphics;
	long lasttime = System.currentTimeMillis();
	float avgfps = sigIRC.framerate;
	int counter = 0;
	int avgcount = 10;
	int[] sum = new int[10];
	int windowUpdateCounter = 30;
	
	public Module(Rectangle bounds, String moduleName) {
		
		this.addComponentListener(this);
		this.addWindowListener(this);
		this.addKeyListener(this);
		
		this.position = bounds;
		this.name = moduleName;
		this.enabled=true;
		this.setVisible(true);
		this.setTitle(moduleName);
		panel = new ListenerPanel(this){
		    public void paintComponent(Graphics g) {
		    	super.paintComponent(g);
		    	draw(g);
		    }
		};
		this.setLocation((int)position.getX(), (int)position.getY());
		
		this.titleHeight = (int)TextUtils.calculateStringBoundsFont(this.name, sigIRC.userFont).getHeight();
		
		
		this.setSize((int)position.getWidth(), (int)position.getHeight());
		panel.setSize(this.getSize());
		
		this.add(panel);
		//this.pack();

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleWithFixedDelay(()->{
			updateFPSCounter();
			run();
			panel.repaint();
		},(long)((1d/(sigIRC.framerate+1))*1000),(long)((1d/(sigIRC.framerate+1))*1000),TimeUnit.MILLISECONDS);
	}
	
	public Module(Rectangle bounds, String moduleName, boolean enabled) {
		this(bounds, moduleName);
		this.enabled=enabled;
	}
	


	public void updateFPSCounter() {
		float val = 1000f/(System.currentTimeMillis()-lasttime);
		sum[counter++ % sum.length] = (int)val;
		avgfps = (float)sum(sum)/sum.length;
		this.setTitle(name+" - "+(int)Math.round(avgfps)+" FPS");
		lasttime=System.currentTimeMillis();
	}
	
	private int sum(int[] array) {
		int val = 0;
		for (int i=0;i<array.length;i++) {
			val+=array[i];
		}
		return val;
	}
	
	public void ApplyConfigWindowProperties() {
	}
	
	public void SaveConfig() {
		
	}
	
	protected void moduleRun() {
		run();
	}
	
	public Rectangle2D getPosition() {
		return position;
	}
	
	public void run() {
	}
	
	public void draw(Graphics g) {
		//g.fillRect(0, 0, (int)position.getWidth(), (int)position.getHeight());
		//DrawUtils.drawText(g, 0, 16, Color.WHITE, "Test");
	}
	
	public void ModuleDragEvent(int oldX, int oldY, int newX, int newY) {
		
	}
	
	public void windowClosed(WindowEvent ev) {
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		UpdatePosition(e);
	}

	private void UpdatePosition(ComponentEvent e) {
		position = new Rectangle((int)e.getComponent().getLocationOnScreen().getX(),(int)e.getComponent().getLocationOnScreen().getY(),e.getComponent().getWidth(),e.getComponent().getHeight());
		//System.out.println(position);
		ApplyConfigWindowProperties();
		sigIRC.configNeedsUpdating = System.currentTimeMillis();
	}

	@Override
	public void componentResized(ComponentEvent e) {
		UpdatePosition(e);
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent e) {
	}
}