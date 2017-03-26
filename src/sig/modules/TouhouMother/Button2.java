package sig.modules.TouhouMother;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import sig.DrawUtils;
import sig.FileUtils;
import sig.TextUtils;
import sig.sigIRC;
import sig.modules.TouhouMotherModule;

public class Button2 {
	BufferedImage buttonimg;
	int x=0;
	int y=0;
	TouhouMotherModule module;
	
	public Button2(TouhouMotherModule parentmodule, File filename, int x, int y) {
		this.x=x;
		this.y=y;
		this.module=parentmodule;
		try {
			buttonimg = ImageIO.read(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void draw(Graphics g) {
		g.drawImage(buttonimg, x, y, sigIRC.panel);
	}
	
	public void onClickEvent(MouseEvent ev) {
		if (ev.getX()>=x && ev.getX()<=x+buttonimg.getWidth() &&
				ev.getY()>=y && ev.getY()<=y+buttonimg.getHeight()) {
			module.endBattle();
		}
	}
}
