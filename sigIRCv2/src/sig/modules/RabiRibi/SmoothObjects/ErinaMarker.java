package sig.modules.RabiRibi.SmoothObjects;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;

import sig.modules.RabiRibiModule;
import sig.modules.RabiRibi.SmoothObject;

public class ErinaMarker extends SmoothObject{
	

	public ErinaMarker(int x, int y, int targetx, int targety, RabiRibiModule parent) {
		super(x, y, targetx, targety, parent);
	}

	public void draw(Graphics g) {
		super.draw(g);
		Point2D.Double erina_pos = parent.overlay.getScreenPosition(parent.overlay.xpos, parent.overlay.ypos);
		setTarget(new Point((int)erina_pos.getX(),(int)erina_pos.getY()-72));
		g.fillOval(x, y, 16, 16);
	}
}
