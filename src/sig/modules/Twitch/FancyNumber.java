package sig.modules.Twitch;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import sig.sigIRC;
import sig.modules.TwitchModule;
import sig.utils.DrawUtils;
import sig.utils.TextUtils;

public class FancyNumber {
	BufferedImage icon;
	int displayedValue=0;
	int lastValue=0;
	int lastValueChange=0;
	boolean upArrow=false;
	final static int DELAYEDFREQUENCY = 40; //How many ticks to wait before performing a calculation update.
	int delayCount=0;
	final static String ICONDIR = sigIRC.BASEDIR+"sigIRC/";
	
	public FancyNumber(String icon_name, int startingValue) {
		try {
			icon = ImageIO.read(new File(ICONDIR+icon_name));
		} catch (IOException e) {
			e.printStackTrace();
		}
		lastValue = startingValue;
	}
	
	public void updateValue(int newValue) {
		if (newValue>0) { //Don't accept 0 or negative numbers as acceptable values. 
			if (lastValue>newValue) {
				upArrow=false;
			} else 
			if (lastValue<newValue) {
				upArrow=true;
			}
			
			if (lastValue!=newValue) {
				lastValueChange = TwitchModule.ARROWTIMER;
			}
			lastValue = newValue;
		}
	}
	
	public Rectangle draw(Graphics g, int x, int y) {
		int xoffset = 0;
		int yoffset = 0;
		g.drawImage(icon, x, y, sigIRC.panel);
		xoffset+=icon.getWidth()+4;
		if (displayedValue!=lastValue) {
			if (delayCount==0) {
				int diff = Math.abs(displayedValue-lastValue);
				double chance = diff*0.1;
				int incr_rate = 0;
				if (chance+Math.random()>=1) {
					incr_rate = 1;
					int val = diff;
					while (val>10) {
						val/=10;
						incr_rate*=10;
					}
				}
				if (displayedValue<lastValue) {
					displayedValue+=incr_rate;
				} else {
					displayedValue-=incr_rate;
				}
				delayCount=DELAYEDFREQUENCY;
			} else {
				delayCount--;
			}
		}
		DrawUtils.drawTextFont(g, sigIRC.userFont, x+xoffset, y+yoffset+TextUtils.calculateStringBoundsFont(Integer.toString(displayedValue), sigIRC.userFont).getHeight()/2+3, new Color(184,181,192), Integer.toString(displayedValue));
		xoffset+=TextUtils.calculateStringBoundsFont(Integer.toString(displayedValue), sigIRC.userFont).getWidth()+4;
		if (lastValueChange>0) {
			lastValueChange--;
			g.drawImage((upArrow)?TwitchModule.UPARROWIMAGE:TwitchModule.DOWNARROWIMAGE, x+xoffset, y+yoffset, sigIRC.panel);
		}
		xoffset+=((upArrow)?TwitchModule.UPARROWIMAGE.getWidth():TwitchModule.DOWNARROWIMAGE.getWidth())+4;
		return new Rectangle(x,y,xoffset,yoffset+16);
	}
}
