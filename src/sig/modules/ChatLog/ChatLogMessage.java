package sig.modules.ChatLog;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.SwingUtilities;

import sig.Emoticon;
import sig.Module;
import sig.SubEmoticon;
import sig.sigIRC;
import sig.modules.ChatLogModule;
import sig.utils.DrawUtils;
import sig.utils.TextUtils;

public class ChatLogMessage {
	String rawMessage;
	Point messageDisplaySize; //The amount of screen space (w,h) this message takes up.
	public Point position;
	ChatLogModule refModule;
	String username;
	List<String> displayMessage = new ArrayList<String>();
	final static public int MESSAGE_SPACING = 24;
	final static public int BORDER_SPACING = 8;
	final static public Color SHADOW_COL = new Color(35,35,35,255); 
	int usernameWidth = 0;
	boolean active=true;
	
	public ChatLogMessage(String rawMessage) {
		this.refModule = ChatLogModule.chatlogmodule;
		this.rawMessage = rawMessage;
		this.position = new Point(0,(int)refModule.getPosition().getHeight()-MESSAGE_SPACING);
		WrapText();
		for (ChatLogMessage clm : this.refModule.messageHistory) {
			clm.position.setLocation(
					clm.position.getX(), 
					clm.position.getY()-messageDisplaySize.getY());
			//System.out.println(clm.rawMessage+": "+clm.position);
		}
		this.position.setLocation(this.position.getX(), this.position.getY()-messageDisplaySize.getY()+ChatLogModule.chatlogmodule.scrolllog_yoffset);
		//System.out.println(displayMessage);
		this.username = DetectUsername(displayMessage);
		if (this.username!=null) {
			displayMessage.set(0,GetMessage(displayMessage.get(0)+" "));
			usernameWidth = (int)TextUtils.calculateStringBoundsFont(this.username, sigIRC.panel.userFont).getWidth();
		}
		for (int i=0;i<displayMessage.size();i++) {
			displayMessage.set(i, ReplaceMessageWithEmoticons(displayMessage.get(i)+" ",(i==0)?usernameWidth:0,i*MESSAGE_SPACING));
		}
	}
	
	private String ReplaceMessageWithEmoticons(String basemsg, int xpos, int ypos) {
		int marker = basemsg.indexOf(" ");
		while (marker<basemsg.length()) {
			//Find a space.
			int space = basemsg.indexOf(" ", marker+1);
			if (space>0) {
				String word = basemsg.substring(marker+1, space);
				//System.out.println("Word is '"+word+"'");
				sigIRC.emoticons.addAll(sigIRC.emoticon_queue);
				sigIRC.emoticon_queue.clear();
				for (Emoticon e : sigIRC.emoticons) {
					//System.out.println("Checking for emoticon "+e.getEmoteName()+" vs \""+word+"\"");
					try {
						if (e.getEmoteName().equals(word)) {
							if (e instanceof SubEmoticon) {
								SubEmoticon se = (SubEmoticon)e;
								if (!se.canUserUseEmoticon(username)) {
									//System.out.println("User "+username+" is not subscribed to "+se.channelName+"'s channel!");
									break;
								}
							}
							//System.out.println("  Found one!");
							basemsg = TextUtils.replaceFirst(basemsg, e.getEmoteName(), e.getSmallSpaceFiller());
							GenerateEmoticon(marker+1, xpos, ypos, basemsg, e);
							space = basemsg.indexOf(" ", marker+1);
							break;
						}
					} catch (NullPointerException ex) {
						ex.printStackTrace();
					}
				}
				marker=space;
			} else {
				break;
			}
		}
		//textMaxWidth = (int)TextUtils.calculateStringBoundsFont(basemsg, sigIRC.panel.programFont).getWidth();
		//textMaxHeight = Math.max(textMaxHeight,(int)TextUtils.calculateStringBoundsFont(basemsg, sigIRC.panel.programFont).getHeight());
		return basemsg;
	}

	private void GenerateEmoticon(int textpos, int xpos, int ypos, String basemsg, Emoticon e) {
		String cutstring = basemsg.substring(0, textpos);
		double width = TextUtils.calculateStringBoundsFont(cutstring, sigIRC.panel.userFont).getWidth();
		//System.out.println("Width of '"+cutstring+"' is "+width);
		//System.out.println("Offsetting emote by "+xpos+"+"+width);
		sigIRC.createEmoticon(e, this, (int)(xpos+width), ypos+16);
		//textMaxHeight = Math.max(textMaxHeight, e.getImage().getHeight());
		//textMaxWidth = (int)(width + e.getImage().getWidth()+1);
	}

	private String GetMessage(String msg) {
		String basemsg = " "+msg.substring(msg.indexOf(":")+2, msg.length())+" ";
		//basemsg = ConvertMessageSymbols(basemsg);
		//basemsg = ReplaceMessageWithEmoticons(basemsg);
		return basemsg.substring(0,basemsg.length()-1);
	}
	
	private String DetectUsername(List<String> messages) {
		if (messages.size()>0) {
			String username = GetUsername(messages.get(0));
			if (username!=null) {
				return username;
			}
		}
		return null;
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

	private String GetUsername(String msg) {
		if (msg.contains(":")) {
			return msg.substring(0,msg.indexOf(":"));
		} else {
			return null;
		}
	}

	private void WrapText() {
		String rawmessage = rawMessage;
		int textWidth = (int)TextUtils.calculateStringBoundsFont(rawmessage, sigIRC.panel.userFont).getWidth();
		int maxWidth = (int)refModule.getPosition().getWidth()-BORDER_SPACING;
		do {
			rawmessage = BreakTextAtNextSection(rawmessage,maxWidth);
			textWidth = (int)TextUtils.calculateStringBoundsFont(rawmessage, sigIRC.panel.userFont).getWidth();
		} while (textWidth>maxWidth);
		if (rawmessage.length()>0) {
			displayMessage.add(rawmessage);
		}
		messageDisplaySize = new Point((int)(refModule.getPosition().getWidth()-BORDER_SPACING),(int)(displayMessage.size()*MESSAGE_SPACING));
		//System.out.println(displayMessage+": "+messageDisplaySize);
	}

	private String BreakTextAtNextSection(String msg, int maxWidth) {
		int marker = 1;
		int textWidth = (int)TextUtils.calculateStringBoundsFont(msg.substring(0, marker), sigIRC.panel.userFont).getWidth();
		while (textWidth<maxWidth) {
			if (marker<msg.length()) {
				int tempmarker = msg.indexOf(' ', marker);
				if (tempmarker!=-1) {
					textWidth = (int)TextUtils.calculateStringBoundsFont(msg.substring(0, tempmarker), sigIRC.panel.userFont).getWidth();
					if (textWidth<maxWidth) {
						marker = tempmarker+1;
					}
					//System.out.println(msg.substring(0, marker)+" | "+textWidth);
				} else {
					marker=msg.length();
					break;
				}
			} else {
				break;
			}
		}
		String currentText = msg.substring(0, marker);
		displayMessage.add(currentText);
		return msg.substring(marker, msg.length());
	}

	public boolean run() {
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	sigIRC.panel.repaint(
					(int)Math.max(refModule.getPosition().getX()+position.getX(),0), 
					(int)Math.max(refModule.getPosition().getY()+position.getY(),0), 
					(int)Math.min(sigIRC.panel.getWidth()-(refModule.getPosition().getX()+position.getX()+messageDisplaySize.getX()),messageDisplaySize.getX()), 
					(int)Math.min(sigIRC.panel.getHeight()-(refModule.getPosition().getY()+position.getY()+messageDisplaySize.getY()),messageDisplaySize.getY()));
		    }  
		});
		//System.out.println(refModule.getPosition()+","+position);
		return true;
	}
	
	public void draw(Graphics g) {
		if (isVisible()) {
			for (int i=0;i<displayMessage.size();i++) {
				//System.out.println(displayMessage.get(i));
				if (username!=null && i==0) {
					DrawUtils.drawOutlineText(g, sigIRC.panel.userFont, refModule.getPosition().getX()+position.getX(), refModule.getPosition().getY()+position.getY()+(i*MESSAGE_SPACING)+32, 2, GetUserNameColor(this.username), SHADOW_COL, this.username);
					DrawUtils.drawTextFont(g, sigIRC.panel.userFont, refModule.getPosition().getX()+position.getX()+usernameWidth+2, refModule.getPosition().getY()+position.getY()+(i*MESSAGE_SPACING)+32, Color.BLACK, displayMessage.get(i));	
				} else {
					DrawUtils.drawTextFont(g, sigIRC.panel.userFont, refModule.getPosition().getX()+position.getX(), refModule.getPosition().getY()+position.getY()+(i*MESSAGE_SPACING)+32, Color.BLACK, displayMessage.get(i));
				}
			}
			g.drawImage(Module.MSG_SEPARATOR, (int)(refModule.getPosition().getX()+position.getX()+8), (int)(refModule.getPosition().getY()+position.getY()+messageDisplaySize.getY()+12), (int)(messageDisplaySize.getX()-8), 1, sigIRC.panel);
			//g.drawLine((int)(refModule.getPosition().getX()+position.getX()+8), (int)(refModule.getPosition().getY()+position.getY()+messageDisplaySize.getY()+32), (int)(refModule.getPosition().getX()+position.getX()+messageDisplaySize.getX()-8), (int)(refModule.getPosition().getY()+position.getY()+messageDisplaySize.getY()+32));
		}
	}

	public static void importMessages(String...logContents) {
		for (String s : logContents) {
			if (s!=null) {
				if (ChatLogModule.chatlogmodule.messageHistory.size()>=ChatLogModule.messageHistoryCount) {
					ChatLogModule.chatlogmodule.messageHistory.remove(0).cleanup();
				}
				ChatLogModule.chatlogmodule.messageHistory.add(new ChatLogMessage(s));
			}
		}
	}
	
	public void cleanup() {
		active=false;
	}
	
	public boolean isVisible() {
		return (refModule.getPosition().getY()+position.getY()+MESSAGE_SPACING)>refModule.getPosition().getY() &&
				(refModule.getPosition().getY()+position.getY()+messageDisplaySize.getY())<refModule.getPosition().getY()+refModule.getPosition().getHeight()-16;
	}
	
	/*
	private String ReplaceMessageWithEmoticons(String basemsg) {
		int marker = basemsg.indexOf(" ");
		while (marker<basemsg.length()) {
			//Find a space.
			int space = basemsg.indexOf(" ", marker+1);
			if (space>0) {
				String word = basemsg.substring(marker+1, space);
				//System.out.println("Word is '"+word+"'");
				sigIRC.emoticons.addAll(sigIRC.emoticon_queue);
				sigIRC.emoticon_queue.clear();
				for (Emoticon e : sigIRC.emoticons) {
					//System.out.println("Checking for emoticon "+e.getEmoteName());
					try {
						if (e.getEmoteName().equals(word)) {
							if (e instanceof SubEmoticon) {
								SubEmoticon se = (SubEmoticon)e;
								if (!se.canUserUseEmoticon(username)) {
									//System.out.println("User "+username+" is not subscribed to "+se.channelName+"'s channel!");
									break;
								}
							}
							//System.out.println("  Found one!");
							basemsg = TextUtils.replaceFirst(basemsg, e.getEmoteName(), e.getSpaceFiller());
							GenerateEmoticon(marker+1, basemsg, e);
							space = basemsg.indexOf(" ", marker+1);
							break;
						}
					} catch (NullPointerException ex) {
						ex.printStackTrace();
					}
				}
				marker=space;
			} else {
				break;
			}
		}
		textMaxWidth = (int)TextUtils.calculateStringBoundsFont(basemsg, sigIRC.panel.programFont).getWidth();
		textMaxHeight = Math.max(textMaxHeight,(int)TextUtils.calculateStringBoundsFont(basemsg, sigIRC.panel.programFont).getHeight());
		return basemsg;
	}
	
	private void GenerateEmoticon(int pos, String basemsg, Emoticon e) {
		String cutstring = basemsg.substring(0, pos);
		double width = TextUtils.calculateStringBoundsFont(cutstring, sigIRC.panel.programFont).getWidth();
		//System.out.println("Width of '"+cutstring+"' is "+width);
		sigIRC.createEmoticon(e, this, (int)(width), 0);
		textMaxHeight = Math.max(textMaxHeight, e.getImage().getHeight());
		//textMaxWidth = (int)(width + e.getImage().getWidth()+1);
	}*/
}
