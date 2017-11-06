package sig.modules.RabiRibi;

import java.awt.Graphics;
import java.awt.Point;

import sig.modules.RabiRibiModule;

public class SmoothObject {
	protected int x,y;
	int targetx,targety;
	protected RabiRibiModule parent;
	
	public SmoothObject(int x, int y, int targetx, int targety, RabiRibiModule parent) {
		this.x=x;
		this.y=y;
		this.targetx=targetx;
		this.targety=targety;
		this.parent=parent;
	}
	
	public void setTarget(Point target) {
		targetx = (int)target.getX();
		targety = (int)target.getY();
	}
	
	public void setTarget(Point.Double target) {
		targetx = (int)target.getX();
		targety = (int)target.getY();
	}
	
	public Point getTarget() {
		return new Point(targetx,targety);
	}
	
	public void setPosition(Point.Double position) {
		x = (int)position.getX();
		y = (int)position.getY();
	}
	
	public void run() {
		int xdiff = targetx-x;
		int ydiff = targety-y;
		x+=xdiff/1.3;
		y+=ydiff/1.3;
		//System.out.println("X:"+x+", Y:"+y+" TargetX:"+targetx+" TargetY:"+targety);
	}
	
	public void draw(Graphics g) {
		
	}
}
