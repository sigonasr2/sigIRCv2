package sig;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class ScrollingText {
	private String username;
	private String message;
	private double x;
	private double y;
	private double scrollspd;
	private int stringWidth;
	private int stringHeight;
	private boolean isAlive=true;
	private Color userColor;
	
	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public String getUsername() {
		return username;
	}

	public String getMessage() {
		return message;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public int getStringWidth() {
		return stringWidth;
	}

	public int getStringHeight() {
		return stringHeight;
	}

	private int userstringWidth;
	private int shadowSize;
	
	public ScrollingText(String msg, double x, double y, double scrollspd) {
		LogMessageToFile(msg);
		this.username = GetUsername(msg);
		this.userColor = GetUserNameColor(this.username);
		this.message = GetMessage(msg);
		this.x=x;
		this.y=y;
		this.scrollspd=scrollspd;
		
		this.shadowSize=2;
		
		this.stringWidth = (int)TextUtils.calculateStringBoundsFont(this.message,MyPanel.programFont).getWidth();
		this.stringHeight = (int)TextUtils.calculateStringBoundsFont(this.message,MyPanel.programFont).getHeight();
		this.userstringWidth = (int)TextUtils.calculateStringBoundsFont(this.username,MyPanel.userFont).getWidth();
		
		playMessageSound(username);
	}
	
	private void playMessageSound(String user) {
		CustomSound cs = CustomSound.getCustomSound(user);
		if (cs!=null && cs.isSoundAvailable()) {
			cs.playCustomSound();
		} else {
			String soundName = sigIRC.BASEDIR+"sounds\\ping.wav";    
			SoundUtils.playSound(soundName);
		}
	}

	private void LogMessageToFile(String message) {
		Calendar cal = Calendar.getInstance();
		FileUtils.logToFile(message, sigIRC.BASEDIR+"logs\\log_"+(cal.get(Calendar.MONTH)+1)+"_"+cal.get(Calendar.DAY_OF_MONTH)+"_"+cal.get(Calendar.YEAR)+".txt");
	}

	private Color GetUserNameColor(String username) {
		Random r = new Random();
		r.setSeed(username.hashCode());
		int randomnumb = r.nextInt(3);
		Color col;
		switch (randomnumb) {
			case 0:{
				col=new Color(255,r.nextInt(128)+64,r.nextInt(128)+64,255);
			}break;
			case 1:{
				col=new Color(r.nextInt(128)+64,255,r.nextInt(128)+64,255);
			}break;
			case 2:{
				col=new Color(r.nextInt(128)+64,r.nextInt(128)+64,255,255);
			}break;
			default:{
				col=Color.GREEN;
			}
		}
		return col;
	}

	public boolean run() {
		x-=scrollspd;
		//System.out.println("X: "+x);
		sigIRC.panel.repaint(
				FindLeftMostCornerInDisplay(),
				FindTopMostCornerInDisplay()-32,
				(int)Math.max(FindRightMostCornerInDisplay(),(int)TextUtils.calculateStringBoundsFont(username, MyPanel.userFont).getWidth())+4,
				FindBottomMostCornerInDisplay()+(stringHeight*2)+4);
		//sigIRC.panel.repaint();
		if (x+stringWidth<0) {
			isAlive=false;
			return false;
		}
		return true;
	}
	
	public void draw(Graphics g) {
		if (isAlive) {
			//DrawUtils.drawTextFont(g, MyPanel.userFont, x+8, y+stringHeight-20, Color.GREEN, username);
			DrawUtils.drawOutlineText(g, MyPanel.userFont, x+8, y+stringHeight-20, 2, userColor, Color.BLACK, username);
			DrawUtils.drawOutlineText(g, MyPanel.programFont, x, y+stringHeight, 2, Color.WHITE, Color.BLACK, message);
		}
	}

	public int FindLeftMostCornerInDisplay() {
		if (x-shadowSize>0) {
			return Math.min((int)x-shadowSize, sigIRC.panel.getWidth());
		} else {
			return 0;
		}
	}
	public int FindTopMostCornerInDisplay() {
		if (y-shadowSize>0) {
			return Math.min((int)y-shadowSize, sigIRC.panel.getHeight());
		} else {
			return 0;
		}
	}
	public int FindRightMostCornerInDisplay() {
		if (x+stringWidth+(int)scrollspd+1+shadowSize+1>0) {
			return Math.min(Math.max(stringWidth,userstringWidth+8)+(int)scrollspd+1+shadowSize+1, sigIRC.panel.getWidth()-(int)x);
		} else {
			return 0;
		}
	}
	public int FindBottomMostCornerInDisplay() {
		if (y+stringHeight+shadowSize>0) {
			return Math.min(stringHeight+shadowSize+4, sigIRC.panel.getHeight()-(int)y);
		} else {
			return 0;
		}
	}

	private String GetMessage(String msg) {
		String basemsg = " "+msg.substring(msg.indexOf(":")+2, msg.length())+" ";
		//basemsg = ConvertMessageSymbols(basemsg);
		basemsg = ReplaceMessageWithEmoticons(basemsg);
		return basemsg.replaceFirst(" ", "").substring(0,basemsg.length()-1);
	}

	private String ConvertMessageSymbols(String basemsg) {
		basemsg = basemsg.replace("/", "SLASH").replace(":", "COLON").replace("\\", "BACKSLASH").replace("|", "BAR").replace(">", "GREATERTHAN").replace("<", "LESSTHAN");
		return basemsg;
	}

	private String ReplaceMessageWithEmoticons(String basemsg) {
		int marker = basemsg.indexOf(" ");
		while (marker<basemsg.length()) {
			//Find a space.
			int space = basemsg.indexOf(" ", marker+1);
			if (space>0) {
				String word = basemsg.substring(marker+1, space);
				//System.out.println("Word is '"+word+"'");
				
				for (Emoticon e : sigIRC.emoticons) {
					if (e.getEmoteName().equals(word)) {
						basemsg = TextUtils.replaceFirst(basemsg, e.getEmoteName(), e.getSpaceFiller());
						GenerateEmoticon(marker+1, basemsg, e);
						space = basemsg.indexOf(" ", marker+1);
						break;
					}
				}
				
				marker=space;
			} else {
				break;
			}
		}
		return basemsg;
	}

	private boolean EmoteExists(Emoticon e, String basemsg) {
		//Emote exists if it is standalone (no other words in message),
		//Contains spaces on both sides,
		//OR contains spaces in front/behind and ends the message with the emote.
		String tempstr = " "+basemsg+" ";
		return tempstr.contains(" "+e.getEmoteName()+" ");
	}

	private void GenerateEmoticon(int pos, String basemsg, Emoticon e) {
		String cutstring = basemsg.substring(0, pos);
		double width = TextUtils.calculateStringBoundsFont(cutstring, sigIRC.panel.programFont).getWidth();
		//System.out.println("Width of '"+cutstring+"' is "+width);
		sigIRC.createEmoticon(e, this, (int)(width), 0);
	}

	private String GetUsername(String msg) {
		return msg.substring(0,msg.indexOf(":"));
	}
}
