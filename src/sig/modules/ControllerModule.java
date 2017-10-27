package sig.modules;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.glfw.GLFW;

import sig.Module;
import sig.sigIRC;
import sig.modules.Controller.Axis;
import sig.modules.Controller.Button;
import sig.modules.Controller.ClickableButton;
import sig.modules.Controller.Component;
import sig.modules.Controller.ControlConfigurationWindow;
import sig.modules.Controller.Controller;
import sig.modules.Controller.EditMode;
import sig.modules.Controller.Identifier;
import sig.modules.Controller.Type;
import sig.modules.Controller.clickablebutton.AddClickableButton;
import sig.modules.Controller.clickablebutton.CopyClickableButton;
import sig.utils.DrawUtils;
import sig.utils.FileUtils;

public class ControllerModule extends Module{
	public final static String CONTROLLERPATH = sigIRC.BASEDIR+"sigIRC/controller/";
	List<Controller> controllers = new ArrayList<Controller>();
	Image controller_img;
	double imgratio = 1;
	List<Button> buttons = new ArrayList<Button>();
	List<Axis> axes = new ArrayList<Axis>();
	List<ClickableButton> click_buttons = new ArrayList<ClickableButton>();
	EditMode MODE = EditMode.DEFAULT;
	String status = "";
	Point start_drag,end_drag;
	Rectangle2D.Double stored_rect;
	int stored_controller_button;
	float stored_controller_value;
	Color buttoncol;
	Controller controller;
	ControlConfigurationWindow configure_window;
	Axis temporary_axis=null;
	int mouseclickwait_timer=0;

	public ControllerModule(Rectangle2D bounds, String moduleName) {
		super(bounds, moduleName);
		if (!GLFW.glfwInit()) {
			System.out.println("Failed to initialize GLFW!");
		} else {
			System.out.println("Successfully initialized GLFW.");
		}
		List<Controller> ca = new ArrayList<Controller>();
		for (int i=0;i<GLFW.GLFW_JOYSTICK_LAST;i++) {
			//System.out.println("Joystick "+i+": "+GLFW.glfwGetJoystickName(i));
			if (GLFW.glfwGetJoystickName(i)!=null) {
				Controller c = new Controller(i);
				ca.add(c);
				System.out.println("Recognized "+GLFW.glfwGetJoystickName(i)+": "+c.outputAxes()+","+c.outputButtons());
			}
		}
		controllers.addAll(ca);
		try {
			controller_img = ImageIO.read(new File(CONTROLLERPATH+"controller_template.png")).getScaledInstance((int)position.getWidth(), -1, 0);
			//System.out.println("Size of controller: "+controller_img.getWidth(sigIRC.panel)+","+controller_img.getHeight(sigIRC.panel));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//buttons.add(new Button(0.1,0.05,0.1,0.05,controllers.get(0),Identifier.Button._3,Color.RED,this));
		LoadButtonAndAxisData();
		click_buttons.add(new AddClickableButton(new Rectangle(
				0,(int)position.getHeight()-41,96,20),"Add Button",this));
		click_buttons.add(new CopyClickableButton(new Rectangle(
				97,(int)position.getHeight()-41,96,20),"Copy Button",this));
		click_buttons.add(new ClickableButton(new Rectangle(
				0,(int)position.getHeight()-20,96,20),"Delete Button",this));
		click_buttons.add(new ClickableButton(new Rectangle(
				97,(int)position.getHeight()-20,96,20),"Edit Button",this));
	}
	
	public List<Controller> getControllers() {
		return controllers;
	}

	public void resetDragPoints() {
		this.start_drag=null;
		this.end_drag=null;
	}
	
	public void setMouseWaitTimer(int ticks) {
		this.mouseclickwait_timer=ticks;
	}
	
	public Axis getTemporaryAxis() {
		return temporary_axis;
	}
	
	public void setTemporaryAxis(Axis a) {
		this.temporary_axis=a;
	}
	
	public void setDragPoints(Point startpoint,Point endpoint) {
		this.start_drag=startpoint;
		this.end_drag=endpoint;
	}
	
	public ControlConfigurationWindow getConfigurationWindow() {
		return configure_window;
	}
	
	public EditMode getMode() {
		return MODE;
	}
	
	public void setMode(EditMode mode) {
		MODE = mode;
	}
	
	public void ApplyConfigWindowProperties() {
		sigIRC.controllermodule_X=(int)position.getX();
		sigIRC.controllermodule_Y=(int)position.getY();
		sigIRC.config.setInteger("CONTROLLER_module_X", sigIRC.controllermodule_X);
		sigIRC.config.setInteger("CONTROLLER_module_Y", sigIRC.controllermodule_Y);
	}
	
	public void mousePressed(MouseEvent ev) {
		if (mouseInsideBounds(ev)) {
			switch (MODE) {
				case DRAGSELECTION:
				case DRAGAXISSELECTION:{
					if (start_drag==null) {
						start_drag = new Point((int)(ev.getX()-getPosition().getX()),(int)(ev.getY()-getPosition().getY()));
					}
				}break;
			}	
			if (MODE==EditMode.DEFAULT) {
				for (ClickableButton cb : click_buttons) {
					cb.onClickEvent(ev);
				}
			}
		}
		super.mousePressed(ev);
	}
	
	public void mouseReleased(MouseEvent ev) {
		super.mouseReleased(ev);
		if (mouseInsideBounds(ev)) {
			switch (MODE) {
				case DRAGSELECTION:
				case DRAGAXISSELECTION:
				{
					if (start_drag!=null) {
						end_drag = new Point((int)(ev.getX()-getPosition().getX()),(int)(ev.getY()-getPosition().getY()));
						double width = (end_drag.getX()-start_drag.getX())/controller_img.getWidth(sigIRC.panel);
						double height = (end_drag.getY()-start_drag.getY())/controller_img.getHeight(sigIRC.panel);
						stored_rect = new Rectangle2D.Double(
								(width<0)?end_drag.getX()/controller_img.getWidth(sigIRC.panel):start_drag.getX()/controller_img.getWidth(sigIRC.panel),
								(height<0)?end_drag.getY()/controller_img.getHeight(sigIRC.panel):start_drag.getY()/controller_img.getHeight(sigIRC.panel),
								Math.abs((end_drag.getX()-start_drag.getX())/controller_img.getWidth(sigIRC.panel)),
								Math.abs((end_drag.getY()-start_drag.getY())/controller_img.getHeight(sigIRC.panel)));
						//buttons.add(new Button(pct_rect.getX(),pct_rect.getY(),pct_rect.getWidth(),pct_rect.getHeight(),controllers.get(0),Identifier.Button._3,Color.RED,this));
						//resetDragPoints();
						sigIRC.panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						if (MODE==EditMode.DRAGSELECTION) {
							MODE=EditMode.BUTTONSET;
						} else 
						if (MODE==EditMode.DRAGAXISSELECTION) {
							AddAxis();
							MODE=EditMode.DEFAULT;
						}
					}
				}
			}
		}
	}

	protected boolean mouseInsideBounds(MouseEvent ev) {
		return mouseclickwait_timer<=0 && ev.getX()>=getPosition().getX() && ev.getX()<=getPosition().getX()+getPosition().getWidth() &&
				ev.getY()>=getPosition().getY() && ev.getY()<=getPosition().getY()+getPosition().getHeight();
	}
	
	public Rectangle2D getPosition() {
		return position;
	}
	
	public void setConfigureWindow(ControlConfigurationWindow window) {
		this.configure_window=window;
	}

	public void run() {
		for (Controller c : controllers) {
			//System.out.println("Data for "+c.getName()+" ("+c.getType()+"):");
			c.poll();
			/*for (Component cp : c.getComponents()) {
				if (!cp.isAnalog()) {
					if (cp.getPollData()!=0) {
						//System.out.println("Button "+cp.getIdentifier()+" held down!");
						FileUtils.logToFile("Button "+cp.getIdentifier()+" held down!", CONTROLLERPATH+"test");
					}
				}
			}*/
		}
		if (mouseclickwait_timer>0) {
			mouseclickwait_timer--;
		}
		switch (MODE) {
			case DRAGSELECTION:{
				int cursortype = sigIRC.panel.getCursor().getType();
				if (cursortype!=Cursor.CROSSHAIR_CURSOR) {
					sigIRC.panel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				}
				status="Drag a button onto the controller template.";
			}break;
			case BUTTONSET:{
				status="Press controller button to set button";
			}break;
			case COLORSET:{
				status="Select a color from the panel.";
			}break;
			case DRAGAXISSELECTION:{
				int cursortype = sigIRC.panel.getCursor().getType();
				if (cursortype!=Cursor.CROSSHAIR_CURSOR) {
					sigIRC.panel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				}
				status="Drag the axis onto the controller template.";
			}break;
			default:{
				status="";
			}
		}
		super.run();
		if (MODE==EditMode.BUTTONSET) {
			stored_controller_button=-1;
			for (Controller c : controllers) {
				for (int i=0;i<c.getButtons().length;i++) {
					byte b = c.getButtonValue(i);
					if (b!=(byte)0) {
						stored_controller_button = i;
						stored_controller_value = b;
						controller=c;
						MODE=EditMode.COLORSET;
						buttoncol = PopupColorPanel();
						AddButton();
						MODE=EditMode.DEFAULT;
						break;
					}
				}
				if (stored_controller_button!=-1) {
					break;
				}
			}
		}
		if (configure_window!=null) {
			configure_window.run();
		}
	}
	
	public Image getControllerImage() {
		return controller_img;
	}
	
	public void draw(Graphics g) {
		super.draw(g);
		if (controllers.size()>0) {
			//System.out.println(controllers.get(0).outputAxes()+","+controllers.get(0).outputButtons());
			for (int i=0;i<controllers.get(0).getComponents().length;i++) {
				Component cp = controllers.get(0).getComponents()[i];
				/*if (!cp.isAnalog()) {
					if (cp.getPollData()!=0) {
						//System.out.println("Button "+cp.getIdentifier()+" held down! Value: "+cp.getPollData());
						//DrawUtils.drawText(g,position.getX(),position.getY(),Color.BLACK,"Button "+cp.getIdentifier()+" held down!");
					}
				}*/
			}
			g.drawImage(controller_img, (int)(position.getX()), (int)(position.getY()), sigIRC.panel);
		}
		DrawUtils.drawText(g, position.getX(), position.getY()+8, Color.BLACK, status);
		for (Button b : buttons) {
			b.draw(g);
		}
		for (Axis a : axes) {
			a.draw(g);
		}
		for (ClickableButton cb : click_buttons) {
			cb.draw(g);
		}
		if (MODE==EditMode.DRAGSELECTION) {
			if (start_drag!=null) {
				Color color_identity = g.getColor();
				g.setColor(Color.GRAY);
				int width = sigIRC.panel.lastMouseX-
						((int)position.getX()+(int)start_drag.getX());
				int height = sigIRC.panel.lastMouseY-
						((int)position.getY()+(int)start_drag.getY());
				g.fillOval(
				(width<0)?sigIRC.panel.lastMouseX:(int)position.getX()+(int)start_drag.getX(), 
				(height<0)?sigIRC.panel.lastMouseY:(int)position.getY()+(int)start_drag.getY(),
						Math.abs(width), Math.abs(height));
				g.setColor(color_identity);
			}
		} else
		if (MODE==EditMode.DRAGAXISSELECTION) {
			if (start_drag!=null) {
				Color color_identity = g.getColor();
				g.setColor(temporary_axis.getBackgroundColor());
				int width = sigIRC.panel.lastMouseX-
						((int)position.getX()+(int)start_drag.getX());
				int height = sigIRC.panel.lastMouseY-
						((int)position.getY()+(int)start_drag.getY());
				if (temporary_axis.isTwoWayAxis()) {
					g.fillRect(
					(width<0)?sigIRC.panel.lastMouseX:(int)position.getX()+(int)start_drag.getX(), 
					(height<0)?sigIRC.panel.lastMouseY:(int)position.getY()+(int)start_drag.getY(),
							Math.abs(width), Math.abs(height));
				} else {
					g.fillOval(
					(width<0)?sigIRC.panel.lastMouseX:(int)position.getX()+(int)start_drag.getX(), 
					(height<0)?sigIRC.panel.lastMouseY:(int)position.getY()+(int)start_drag.getY(),
							Math.abs(width), Math.abs(height));
				}
				g.setColor(color_identity);
			}
		}
	}

	private void LoadButtonAndAxisData() {
		String[] buttondata = FileUtils.readFromFile(CONTROLLERPATH+"button_data.txt");
		if (controllers.size()>0) {
			for (String s : buttondata) {
				if (s.length()>0) {
					buttons.add(Button.loadFromString(s, controllers.get(0), this));
				}
			}
		}
		String[] axisdata = FileUtils.readFromFile(CONTROLLERPATH+"axis_data.txt");
		if (controllers.size()>0) {
			for (String s : axisdata) {
				if (s.length()>0) {
					//System.out.println("Creating new axis using string "+s+".");
					Axis a = Axis.loadFromString(s, controllers.get(0), this);
					a.setVisible(true);
					axes.add(a);
				}
			}
		}
	}

	private void AddAxis() {
		temporary_axis.setupBoundsRectangle(stored_rect);
		temporary_axis.setVisible(true);
		axes.add(temporary_axis);
		temporary_axis=null;
		StringBuilder sb = new StringBuilder();
		for (Axis a : axes) {
			sb.append(a.getSaveString()+"\n");
		}
		FileUtils.writetoFile(new String[]{sb.toString()}, CONTROLLERPATH+"axis_data.txt");
	}

	private void AddButton() {
		buttons.add(new Button(stored_rect,controller,stored_controller_button,(byte)stored_controller_value,buttoncol,this));
		StringBuilder sb = new StringBuilder();
		for (Button b : buttons) {
			sb.append(b.getSaveString()+"\n");
		}
		FileUtils.writetoFile(new String[]{sb.toString()}, CONTROLLERPATH+"button_data.txt");
	}

	private Color PopupColorPanel() {
		Color col=null;
		do {
			col=sigIRC.colorpanel.getBackgroundColor();
		} while (col==null);
		return col;
	}
}