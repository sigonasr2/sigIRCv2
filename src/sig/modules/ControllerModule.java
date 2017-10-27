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
import sig.modules.Controller.Element;
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
	Element selectedElement;
	boolean dragging=false;
	int resizing_direction=0;
	/*1=North
		3=North-east
		2=East
		6=South-east
		4=South
		12=South-west
		8=West
		9=North-west*/
	Point resize_refpoint;
	boolean resizing=false;
	double xoffset=0,yoffset=0;
	final static int RESIZE_BORDER = 5;

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
				0,(int)position.getHeight()-21,96,20),"Add Button",this));
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
	
	public void setStoredRectangle(Rectangle2D.Double rect) {
		this.stored_rect=rect;
	}
	
	public Rectangle2D.Double getStoredRectangle() {
		return stored_rect;
	}
	
	public void ApplyConfigWindowProperties() {
		sigIRC.controllermodule_X=(int)position.getX();
		sigIRC.controllermodule_Y=(int)position.getY();
		sigIRC.config.setInteger("CONTROLLER_module_X", sigIRC.controllermodule_X);
		sigIRC.config.setInteger("CONTROLLER_module_Y", sigIRC.controllermodule_Y);
	}
	
	public void mousePressed(MouseEvent ev) {
		if (dragging || resizing) {
			return;
		}
		if (mouseInsideBounds(ev)) {
			Point mouse_position = new Point((int)(ev.getX()-getPosition().getX()),(int)(ev.getY()-getPosition().getY()));
			switch (MODE) {
				case DRAGSELECTION:
				case DRAGAXISSELECTION:{
					if (start_drag==null) {
						start_drag = mouse_position;
					}
				}break;
			}	
			if (MODE==EditMode.DEFAULT) {
				for (ClickableButton cb : click_buttons) {
					cb.onClickEvent(ev);
				}
			}
			if (selectedElement!=null && resizing_direction!=0 && !resizing && extendBoundaries(selectedElement.getPixelBounds(controller_img),3).contains(mouse_position)) {
				resizing=true;
				resize_refpoint=mouse_position;
			} else
			if (selectedElement!=null && !resizing && selectedElement.getPixelBounds(controller_img).contains(mouse_position)) {
				dragging=true;
				xoffset = selectedElement.getPixelBounds(controller_img).getX()-mouse_position.getX();
				yoffset = selectedElement.getPixelBounds(controller_img).getY()-mouse_position.getY();
			} else {
				selectedElement=null;
				for (Element e : buttons) {
					//System.out.println("Checking bounds "+e.getPixelBounds(controller_img));
					if (e.getPixelBounds(controller_img).contains(mouse_position)) {
						selectedElement = e;
						break;
					}
				}
				if (selectedElement==null) {
					for (Element e : axes) {
						//System.out.println("Checking bounds "+e.getPixelBounds(controller_img));
						if (e.getPixelBounds(controller_img).contains(mouse_position)) {
							selectedElement = e;
							break;
						}
					}
				}
			}
			//System.out.println("Selected element "+selectedElement+". Mouse Point: "+ev.getPoint());
		}
		super.mousePressed(ev);
	}
	
	public void mouseReleased(MouseEvent ev) {
		if (resizing) {
			Point mouse_position = new Point((int)(ev.getX()-getPosition().getX()),(int)(ev.getY()-getPosition().getY()));
			PerformResize(mouse_position);
			resizing=false;
			resizing_direction=0;
			SaveElementData();
			return;
		}
		if (dragging) {
			Point mouse_position = new Point((int)(ev.getX()-getPosition().getX()),(int)(ev.getY()-getPosition().getY()));
			selectedElement.setBounds(new Rectangle2D.Double((mouse_position.getX()+xoffset)/controller_img.getWidth(sigIRC.panel), 
					(mouse_position.getY()+yoffset)/controller_img.getHeight(sigIRC.panel), 
					selectedElement.getBounds().getWidth(), 
					selectedElement.getBounds().getHeight()));
			dragging=false;
			SaveElementData();
			return;
		}
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
				case POSITIONSELECTION:{
					Point mouse_click = new Point((int)(ev.getX()-getPosition().getX()),(int)(ev.getY()-getPosition().getY()));
					stored_rect = new Rectangle2D.Double(
							(mouse_click.getX()-(stored_rect.getWidth()*controller_img.getWidth(sigIRC.panel))/2)/controller_img.getWidth(sigIRC.panel), 
							(mouse_click.getY()-(stored_rect.getHeight()*controller_img.getHeight(sigIRC.panel))/2)/controller_img.getHeight(sigIRC.panel), 
							stored_rect.getWidth(), 
							stored_rect.getHeight());
					MODE=EditMode.BUTTONSET;
				}break;
			}
		}
	}

	private void PerformResize(Point mouse_position) {
		switch (resizing_direction) {
			case 1:{
				AdjustY(mouse_position);
			}break;
			case 2:{
				AdjustWidth(mouse_position);
			}break;
			case 3:{
				AdjustWidth(mouse_position);
				AdjustY(mouse_position);
			}break;
			case 6:{
				AdjustWidth(mouse_position);
				AdjustHeight(mouse_position);
			}break;
			case 4:{
				AdjustHeight(mouse_position);
			}break;
			case 12:{
				AdjustX(mouse_position);
				AdjustHeight(mouse_position);
			}break;
			case 8:{
				AdjustX(mouse_position);
			}break;
			case 9:{
				AdjustX(mouse_position);
				AdjustY(mouse_position);
			}break;
		}
		resize_refpoint=mouse_position;
	}

	private void AdjustY(Point mouse_position) {
		selectedElement.setBounds(new Rectangle2D.Double(selectedElement.getBounds().getX(), 
				(mouse_position.getY()-resize_refpoint.getY()+selectedElement.getPixelBounds(controller_img).getY())/controller_img.getHeight(sigIRC.panel), 
				selectedElement.getBounds().getWidth(), 
				(resize_refpoint.getY()-mouse_position.getY()+selectedElement.getPixelBounds(controller_img).getHeight())/controller_img.getHeight(sigIRC.panel)));
	}

	private void AdjustX(Point mouse_position) {
		selectedElement.setBounds(new Rectangle2D.Double((mouse_position.getX()-resize_refpoint.getX()+selectedElement.getPixelBounds(controller_img).getX())/controller_img.getWidth(sigIRC.panel), 
				selectedElement.getBounds().getY(), 
				(resize_refpoint.getX()-mouse_position.getX()+selectedElement.getPixelBounds(controller_img).getWidth())/controller_img.getWidth(sigIRC.panel), 
				selectedElement.getBounds().getHeight()));
	}

	private void AdjustHeight(Point mouse_position) {
		selectedElement.setBounds(new Rectangle2D.Double(selectedElement.getBounds().getX(), 
				selectedElement.getBounds().getY(), 
				selectedElement.getBounds().getWidth(), 
				(mouse_position.getY()-resize_refpoint.getY()+selectedElement.getPixelBounds(controller_img).getHeight())/controller_img.getHeight(sigIRC.panel)));
	}

	private void AdjustWidth(Point mouse_position) {
		selectedElement.setBounds(new Rectangle2D.Double(selectedElement.getBounds().getX(), 
				selectedElement.getBounds().getY(), 
				(mouse_position.getX()-resize_refpoint.getX()+selectedElement.getPixelBounds(controller_img).getWidth())/controller_img.getWidth(sigIRC.panel), 
				selectedElement.getBounds().getHeight()));
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
	
	Rectangle2D.Double extendBoundaries(Rectangle2D.Double rect, double amt) {
		return new Rectangle2D.Double(rect.getX()-amt, rect.getY()-amt, rect.getWidth()+amt*2, rect.getHeight()+amt*2);
	}

	public void run() {
		Point mouse_position = new Point((int)(sigIRC.panel.lastMouseX-getPosition().getX()),(int)(sigIRC.panel.lastMouseY-getPosition().getY()));
		if (resizing) {
			PerformResize(mouse_position);
		}
		if (dragging) {
			selectedElement.setBounds(new Rectangle2D.Double((mouse_position.getX()+xoffset)/controller_img.getWidth(sigIRC.panel), 
					(mouse_position.getY()+yoffset)/controller_img.getHeight(sigIRC.panel), 
					selectedElement.getBounds().getWidth(), 
					selectedElement.getBounds().getHeight()));
		}
		if (selectedElement!=null && extendBoundaries(selectedElement.getPixelBounds(controller_img),3).contains(mouse_position)) {
			if (!resizing) {
				resizing_direction=0;
				if (mouse_position.getY()-selectedElement.getPixelBounds(controller_img).getY()<=RESIZE_BORDER &&
						mouse_position.getY()-selectedElement.getPixelBounds(controller_img).getY()>=-RESIZE_BORDER) {
					resizing_direction+=1;
				} else
				if (mouse_position.getY()-(selectedElement.getPixelBounds(controller_img).getY()+selectedElement.getPixelBounds(controller_img).getHeight())<=RESIZE_BORDER &&
					mouse_position.getY()-(selectedElement.getPixelBounds(controller_img).getY()+selectedElement.getPixelBounds(controller_img).getHeight())>=-RESIZE_BORDER) {
						resizing_direction+=4;
				}
				if (mouse_position.getX()-selectedElement.getPixelBounds(controller_img).getX()<=RESIZE_BORDER &&
						mouse_position.getX()-selectedElement.getPixelBounds(controller_img).getX()>=-RESIZE_BORDER) {
					resizing_direction+=8;
				} else
				if (mouse_position.getX()-(selectedElement.getPixelBounds(controller_img).getX()+selectedElement.getPixelBounds(controller_img).getWidth())<=RESIZE_BORDER &&
					mouse_position.getX()-(selectedElement.getPixelBounds(controller_img).getX()+selectedElement.getPixelBounds(controller_img).getWidth())>=-RESIZE_BORDER) {
						resizing_direction+=2;
				}
			}
			switch (resizing_direction) {
				case 1:{
					int cursortype = sigIRC.panel.getCursor().getType();
					if (cursortype!=Cursor.N_RESIZE_CURSOR) {
						sigIRC.panel.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
					}
				}break;
				case 2:{
					int cursortype = sigIRC.panel.getCursor().getType();
					if (cursortype!=Cursor.E_RESIZE_CURSOR) {
						sigIRC.panel.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
					}
				}break;
				case 3:{
					int cursortype = sigIRC.panel.getCursor().getType();
					if (cursortype!=Cursor.NE_RESIZE_CURSOR) {
						sigIRC.panel.setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
					}
				}break;
				case 6:{
					int cursortype = sigIRC.panel.getCursor().getType();
					if (cursortype!=Cursor.SE_RESIZE_CURSOR) {
						sigIRC.panel.setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
					}
				}break;
				case 4:{
					int cursortype = sigIRC.panel.getCursor().getType();
					if (cursortype!=Cursor.S_RESIZE_CURSOR) {
						sigIRC.panel.setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
					}
				}break;
				case 12:{
					int cursortype = sigIRC.panel.getCursor().getType();
					if (cursortype!=Cursor.SW_RESIZE_CURSOR) {
						sigIRC.panel.setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
					}
				}break;
				case 8:{
					int cursortype = sigIRC.panel.getCursor().getType();
					if (cursortype!=Cursor.W_RESIZE_CURSOR) {
						sigIRC.panel.setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
					}
				}break;
				case 9:{
					int cursortype = sigIRC.panel.getCursor().getType();
					if (cursortype!=Cursor.NW_RESIZE_CURSOR) {
						sigIRC.panel.setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
					}
				}break;
				default:
					int cursortype = sigIRC.panel.getCursor().getType();
					if (cursortype!=Cursor.DEFAULT_CURSOR) {
						sigIRC.panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					}
			}
		}
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
		if (resizing_direction==0) {
			if (selectedElement!=null && selectedElement.getPixelBounds(controller_img).contains(mouse_position)) {
				int cursortype = sigIRC.panel.getCursor().getType();
				if (cursortype!=Cursor.MOVE_CURSOR) {
					sigIRC.panel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
				}
			} else 
			if (selectedElement!=null) {
				int cursortype = sigIRC.panel.getCursor().getType();
				if (cursortype!=Cursor.DEFAULT_CURSOR) {
					sigIRC.panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
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
			case POSITIONSELECTION:{
				status="Click where you want this new button placed.";
				int cursortype = sigIRC.panel.getCursor().getType();
				if (cursortype!=Cursor.CROSSHAIR_CURSOR) {
					sigIRC.panel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				}
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
		for (ClickableButton cb : click_buttons) {
			cb.draw(g);
		}
		for (Axis a : axes) {
			a.draw(g);
		}
		for (Button b : buttons) {
			b.draw(g);
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
		if (MODE==EditMode.POSITIONSELECTION) {
			Color color_identity = g.getColor();
			g.setColor(Color.GRAY);
			int width = (int)((stored_rect.getWidth()*controller_img.getWidth(sigIRC.panel)));
			int height = (int)((stored_rect.getHeight()*controller_img.getHeight(sigIRC.panel)));
			g.fillOval(
			sigIRC.panel.lastMouseX-width/2, 
			sigIRC.panel.lastMouseY-height/2,
					Math.abs(width), Math.abs(height));
			g.setColor(color_identity);
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
		
		if (selectedElement!=null) {
			Rectangle2D.Double rect = selectedElement.getPixelBounds(controller_img);
			Color color_identity = g.getColor();
			g.setColor(DrawUtils.invertColor(selectedElement.getElementColor()));
			for (int i=-1;i<2;i++) {
				for (int j=-1;j<2;j++) {
					g.draw3DRect((int)(rect.getX()+position.getX())+i, (int)(rect.getY()+position.getY())+j, (int)rect.getWidth(), (int)rect.getHeight(), true);
				}
			}
			g.setColor(color_identity);
		}

		DrawUtils.drawText(g, position.getX(), position.getY()+8, Color.BLACK, status);
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
		SaveAxisData();
	}

	private void SaveAxisData() {
		StringBuilder sb = new StringBuilder();
		for (Axis a : axes) {
			sb.append(a.getSaveString()+"\n");
		}
		FileUtils.writetoFile(new String[]{sb.toString()}, CONTROLLERPATH+"axis_data.txt");
	}

	private void AddButton() {
		buttons.add(new Button(stored_rect,controller,stored_controller_button,(byte)stored_controller_value,buttoncol,this));
		SaveButtonData();
	}

	private void SaveButtonData() {
		StringBuilder sb = new StringBuilder();
		for (Button b : buttons) {
			sb.append(b.getSaveString()+"\n");
		}
		FileUtils.writetoFile(new String[]{sb.toString()}, CONTROLLERPATH+"button_data.txt");
	}
	
	private void SaveElementData() {
		SaveButtonData();
		SaveAxisData();
	}

	private Color PopupColorPanel() {
		Color col=null;
		do {
			col=sigIRC.colorpanel.getBackgroundColor();
		} while (col==null);
		return col;
	}
}