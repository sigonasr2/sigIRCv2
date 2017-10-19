package sig.modules;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import sig.Module;

public class ControllerModule extends Module{

	public ControllerModule(Rectangle2D bounds, String moduleName) {
		super(bounds, moduleName);
		Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();
		for (Controller c : ca) {
			System.out.println(c.getName());
		}
	}

	public void run() {
		super.run();
	}
	
	public void draw(Graphics g) {
		super.draw(g);
	}
}
