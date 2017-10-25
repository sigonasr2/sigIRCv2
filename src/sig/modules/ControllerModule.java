package sig.modules;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import net.java.games.input.Controller.Type;
import net.java.games.input.ControllerEnvironment;
import sig.Module;
import sig.sigIRC;
import sig.modules.Controller.Button;
import sig.modules.Controller.ClickableButton;
import sig.modules.Controller.ControlConfigurationWindow;
import sig.modules.Controller.EditMode;
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
	List<ClickableButton> click_buttons = new ArrayList<ClickableButton>();
	EditMode MODE = EditMode.DEFAULT;
	String status = "";
	Point start_drag,end_drag;
	Rectangle2D.Double stored_rect;
	Identifier stored_controller_button;
	float stored_controller_value;
	Color buttoncol;
	Controller controller;
	ControlConfigurationWindow configure_window;

	public ControllerModule(Rectangle2D bounds, String moduleName) {
		super(bounds, moduleName);
		Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();
		for (Controller c : ca) {
			if (c.getType()==Type.GAMEPAD) {
				controllers.add(c);
				System.out.println("Recognized "+c.getName()+": "+c.getType());
				//System.out.println("Components: ");
				/*for (Component cp : c.getComponents()) {
					System.out.println(" "+cp.getName()+" ("+cp.getIdentifier().getName()+")");
				}*/
			}
			//System.out.println(c.getName()+": "+c.getType());
		}
		try {
			controller_img = ImageIO.read(new File(CONTROLLERPATH+"controller_template.png")).getScaledInstance((int)position.getWidth(), -1, 0);
			//System.out.println("Size of controller: "+controller_img.getWidth(sigIRC.panel)+","+controller_img.getHeight(sigIRC.panel));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//buttons.add(new Button(0.1,0.05,0.1,0.05,controllers.get(0),Identifier.Button._3,Color.RED,this));
		LoadButtonData();
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
	
	public void setDragPoints(Point startpoint,Point endpoint) {
		this.start_drag=startpoint;
		this.end_drag=endpoint;
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
				case DRAGSELECTION:{
					if (start_drag==null) {
						start_drag = new Point((int)(ev.getX()-getPosition().getX()),(int)(ev.getY()-getPosition().getY()));
					}
				}break;
			}	
		}
		super.mousePressed(ev);
		if (MODE==EditMode.DEFAULT) {
			for (ClickableButton cb : click_buttons) {
				cb.onClickEvent(ev);
			}
		}
	}
	
	public void mouseReleased(MouseEvent ev) {
		super.mouseReleased(ev);
		if (mouseInsideBounds(ev)) {
			switch (MODE) {
				case DRAGSELECTION:{
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
						MODE=EditMode.BUTTONSET;
					}
				}
			}
		}
	}

	protected boolean mouseInsideBounds(MouseEvent ev) {
		return ev.getX()>=getPosition().getX() && ev.getX()<=getPosition().getX()+getPosition().getWidth() &&
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
			default:{
				status="";
			}
		}
		super.run();
		if (MODE==EditMode.BUTTONSET) {
			stored_controller_button=null;
			for (Controller c : controllers) {
				for (Component cp : c.getComponents()) {
					if (!cp.isAnalog() && cp.getPollData()!=0.0f) {
						stored_controller_button = cp.getIdentifier();
						stored_controller_value = cp.getPollData();
						controller=c;
						MODE=EditMode.COLORSET;
						buttoncol = PopupColorPanel();
						AddButton();
						MODE=EditMode.DEFAULT;
						break;
					}
				}
				if (stored_controller_button!=null) {
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
		DrawUtils.drawText(g, position.getX(), position.getY()+8, Color.BLACK, status);
		for (Button b : buttons) {
			b.draw(g);
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
		}
	}

	private void LoadButtonData() {
		String[] buttondata = FileUtils.readFromFile(CONTROLLERPATH+"button_data.txt");
		if (controllers.size()>0) {
			for (String s : buttondata) {
				if (s.length()>0) {
					buttons.add(Button.loadFromString(s, controllers.get(0), this));
				}
			}
		}
	}

	private void AddButton() {
		buttons.add(new Button(stored_rect,controller,stored_controller_button,stored_controller_value,buttoncol,this));
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