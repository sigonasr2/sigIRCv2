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

public class UpdateButton extends TouhouMotherButton{
	String[] data;
	int currentselection=4;
	boolean buttonEnabled = false;
	
	public UpdateButton(TouhouMotherModule parentmodule, File filename, int x, int y) {
		super(parentmodule,filename,x,y);
		data = FileUtils.readFromFile(sigIRC.BASEDIR+"WSplits");
		if (data.length>4) {
			buttonEnabled=true;
		}
	}
	
	public void draw(Graphics g) {
		if (buttonEnabled) {
			DrawUtils.drawOutlineText(g, sigIRC.panel.smallFont, x-TextUtils.calculateStringBoundsFont(data[currentselection].split(",")[0], sigIRC.panel.smallFont).getWidth(), (int)module.getBounds().getY()+(int)module.getBounds().getHeight()-8, 1, Color.WHITE, new Color(30,0,86,255), 
					data[currentselection].split(",")[0]);
			super.draw(g);
		}
	}
	
	public void onClickEvent(MouseEvent ev) {
		if (buttonEnabled) {
			if (ev.getX()>=x && ev.getX()<=x+buttonimg.getWidth() &&
					ev.getY()>=y && ev.getY()<=y+buttonimg.getHeight()) {
				data = FileUtils.readFromFile(sigIRC.BASEDIR+"WSplits");
	
				int val = Integer.parseInt(data[1].replace("Attempts=", ""));
				data[1]=data[1].replace(Integer.toString(val), Integer.toString(++val));
				
				for (int i=4;i<=currentselection;i++) {
					int runCount = Integer.parseInt(data[i].substring(data[i].indexOf("(")+1, data[i].indexOf(")")));
					data[i]=data[i].replace("("+Integer.toString(runCount)+")", "("+Integer.toString(++runCount)+")");
				}
				for (int i=4;i<data.length-1;i++) {
					int runCount = Integer.parseInt(data[i].substring(data[i].indexOf("(")+1, data[i].indexOf(")")));
					final String pctRunString = " - "+Integer.toString((int)(((double)runCount/val)*100))+"%";
					if (data[i].contains("%")) {
						String pctRuns = data[i].substring(data[i].indexOf(" - "), data[i].indexOf("%")+1); 
						data[i]=data[i].replace(pctRuns, pctRunString);
					} else {
						data[i]=data[i].substring(0, data[i].indexOf(")")+1)+pctRunString+data[i].substring(data[i].indexOf(","), data[i].length());
					}
				}
				FileUtils.writetoFile(data, sigIRC.BASEDIR+"WSplits");
			}
		}
	}

	public void onMouseWheelEvent(MouseWheelEvent ev) {
		if (buttonEnabled) {
			int nextselection = currentselection+(int)Math.signum(ev.getWheelRotation());
			nextselection = LoopSelectionAround(nextselection);
			currentselection=nextselection;
		}
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
