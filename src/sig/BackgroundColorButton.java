package sig;

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

public class BackgroundColorButton {
	BufferedImage buttonimg;
	int x=0;
	int y=0;
	boolean buttonEnabled = true;
	
	public BackgroundColorButton(File filename, int x, int y) {
		this.x=x;
		this.y=y;
		try {
			buttonimg = ImageIO.read(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void draw(Graphics g) {
		if (buttonEnabled) {
			g.drawImage(buttonimg, x, y, sigIRC.panel);
		}
	}
	
	public void onClickEvent(MouseEvent ev) {
		if (buttonEnabled) {
			if (ev.getX()>=x && ev.getX()<=x+buttonimg.getWidth() &&
					ev.getY()>=y && ev.getY()<=y+buttonimg.getHeight()) {
				sigIRC.backgroundcol=sigIRC.colorpanel.getBackgroundColor();
				sigIRC.config.setProperty("backgroundColor", Integer.toString(sigIRC.backgroundcol.getRGB()));
				sigIRC.config.saveProperties();
				sigIRC.panel.setBackground(sigIRC.backgroundcol);
			}
		}
	}
}
