package sig.modules;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
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
import sig.utils.DrawUtils;
import sig.utils.FileUtils;

public class ControllerModule extends Module{
	public final static String CONTROLLERPATH = sigIRC.BASEDIR+"sigIRC/controller/";
	List<Controller> controllers = new ArrayList<Controller>();
	Image controller_img;
	double imgratio = 1;
	List<Button> buttons = new ArrayList<Button>();
	List<ClickableButton> click_buttons = new ArrayList<ClickableButton>();

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
		} catch (IOException e) {
			e.printStackTrace();
		}
		buttons.add(new Button(0.1,0.05,0.1,0.05,controllers.get(0),Identifier.Button._3,Color.RED,this));
		click_buttons.add(new ClickableButton(new Rectangle(
				0,0,96,20),"Test",this));
	}
	
	public void mousePressed(MouseEvent ev) {
		super.mousePressed(ev);
		for (ClickableButton cb : click_buttons) {
			cb.onClickEvent(ev);
		}
	}
	
	public Rectangle2D getPosition() {
		return position;
	}

	public void run() {
		super.run();
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
	}
	
	public void draw(Graphics g) {
		super.draw(g);
		for (int i=0;i<controllers.get(0).getComponents().length;i++) {
			Component cp = controllers.get(0).getComponents()[i];
			if (!cp.isAnalog()) {
				if (cp.getPollData()!=0) {
					//System.out.println("Button "+cp.getIdentifier()+" held down!");
					//DrawUtils.drawText(g,position.getX(),position.getY(),Color.BLACK,"Button "+cp.getIdentifier()+" held down!");
				}
			}
		}
		g.drawImage(controller_img, (int)(position.getX()+1), (int)(position.getY()+8), sigIRC.panel);
		for (Button b : buttons) {
			b.draw(g);
		}
		for (ClickableButton cb : click_buttons) {
			cb.draw(g);
		}
	}
}
