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

public class Button {
	BufferedImage buttonimg;
	int x=0;
	int y=0;
	String[] data;
	int currentselection=4;
	TouhouMotherModule module;
	
	public Button(TouhouMotherModule parentmodule, File filename, int x, int y) {
		this.x=x;
		this.y=y;
		data = FileUtils.readFromFile(sigIRC.BASEDIR+"..\\WSplits");
		this.module=parentmodule;
		try {
			buttonimg = ImageIO.read(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void draw(Graphics g) {
		DrawUtils.drawOutlineText(g, sigIRC.panel.smallFont, x-TextUtils.calculateStringBoundsFont(data[currentselection].split(",")[0], sigIRC.panel.smallFont).getWidth(), (int)module.getBounds().getY()+(int)module.getBounds().getHeight()-8, 1, Color.BLACK, Color.WHITE, 
				data[currentselection].split(",")[0]);
		g.drawImage(buttonimg, x, y, sigIRC.panel);
	}
	
	public void onClickEvent(MouseEvent ev) {
		if (ev.getX()>=x && ev.getX()<=x+buttonimg.getWidth() &&
				ev.getY()>=y && ev.getY()<=y+buttonimg.getHeight()) {
			for (int i=4;i<=currentselection;i++) {
				int runCount = Integer.parseInt(data[i].substring(data[i].indexOf("(")+1, data[i].indexOf(")")));
				data[i]=data[i].replace("("+Integer.toString(runCount)+")", "("+Integer.toString(++runCount)+")");
			}
			FileUtils.writetoFile(data, sigIRC.BASEDIR+"..\\WSplits");
		}
	}

	public void onMouseWheelEvent(MouseWheelEvent ev) {
		int nextselection = currentselection+(int)Math.signum(ev.getWheelRotation());
		nextselection = LoopSelectionAround(nextselection);
		currentselection=nextselection;
	}

	public int LoopSelectionAround(int nextselection) {
		if (nextselection<4) {
			nextselection=findLastNonBlankLine();
		} else
		if (nextselection>=findLastNonBlankLine()) {
			nextselection=4;
		}
		return nextselection;
	}

	private int findLastNonBlankLine() {
		for (int i=data.length-2;i>=0;i++) {
			if (data[i].length()>0) {
				return i;
			}
		}
		System.out.println("WARNING! Could not find last non blank line!");
		return -1;
	}
}
