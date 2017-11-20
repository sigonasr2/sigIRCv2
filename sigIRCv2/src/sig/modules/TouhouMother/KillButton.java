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

import sig.sigIRC;
import sig.modules.TouhouMotherModule;
import sig.utils.DrawUtils;
import sig.utils.FileUtils;
import sig.utils.TextUtils;

public class KillButton extends TouhouMotherButton{
	
	public KillButton(TouhouMotherModule parentmodule, File filename, int x, int y) {
		super(parentmodule,filename,x,y);
	}
	
	public void onClickEvent(MouseEvent ev) {
		if (ev.getX()>=x && ev.getX()<=x+buttonimg.getWidth() &&
				ev.getY()>=y && ev.getY()<=y+buttonimg.getHeight()) {
			module.endBattle();
		}
	}
}
