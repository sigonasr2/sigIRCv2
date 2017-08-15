package sig.modules.TouhouMother;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import sig.sigIRC;
import sig.modules.TouhouMotherModule;

public class TouhouMotherButton {
	protected BufferedImage buttonimg;
	protected int x=0;
	protected int y=0;
	protected TouhouMotherModule module;
	
	public TouhouMotherButton(TouhouMotherModule parentmodule, File filename, int x, int y) {
		this.x=x;
		this.y=y;
		this.module=parentmodule;
		try {
			buttonimg = ImageIO.read(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		
	}
	
	public void draw(Graphics g) {
		g.drawImage(buttonimg, x, y, sigIRC.panel);
	}
	
	public void onClickEvent(MouseEvent ev) {
		
	}

	public void keyPressEvent(KeyEvent ev) {
		
	}
	
	public void keyReleaseEvent(KeyEvent ev) {
		
	}
	
	public void onMouseWheelEvent(MouseWheelEvent ev) {
		
	}

	public void updatePosition(int oldX, int oldY, int newX, int newY) {
		int diffx = x - oldX;
		int diffy = y - oldY;
		x = newX + diffx;
		y = newY + diffy;
		/*System.out.println("Old: "+oldX+","+oldY);
		System.out.println("New: "+newX+","+newY);
		System.out.println("Diffs: "+diffx+","+diffy);*/
	}
}
