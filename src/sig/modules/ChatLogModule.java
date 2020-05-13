package sig.modules;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import sig.Module;
import sig.sigIRC;
import sig.modules.ChatLog.ChatLogMessage;
import sig.utils.DrawUtils;
import sig.utils.FileUtils;

public class ChatLogModule extends Module{
	public static int messageHistoryCount = 50;
	public List<ChatLogMessage> messageHistory = new ArrayList<ChatLogMessage>();
	int delay = 150;
	boolean initialized=false;
	public static ChatLogModule chatlogmodule;
	public int scrolllog_yoffset = 0;
	public Color backgroundColor;

	public ChatLogModule(Rectangle2D bounds, String moduleName) {
		super(bounds, moduleName);
		//Initialize();
		chatlogmodule = this;
		backgroundColor = DrawUtils.convertStringToColor(sigIRC.chatlogmodule_backgroundColor);
	}

	private void Initialize() {
		messageHistoryCount = sigIRC.chatlogMessageHistory;
		Calendar cal = Calendar.getInstance();
		String logFileLoc = sigIRC.BASEDIR+"sigIRC/logs/log_"+(cal.get(Calendar.MONTH)+1)+"_"+cal.get(Calendar.DAY_OF_MONTH)+"_"+cal.get(Calendar.YEAR)+".txt";
		File todaysLogFile = new File(logFileLoc);
		if (todaysLogFile.exists()) {
			String[] logContents = FileUtils.readFromFile(logFileLoc);
			if (logContents.length>messageHistoryCount) {
				logContents = Arrays.copyOfRange(logContents, logContents.length-messageHistoryCount-1, logContents.length);
			}
			ChatLogMessage.importMessages(logContents);
		} 
	}
	
	public static void loadModule() {
		sigIRC.modules.add(new ChatLogModule(
				new Rectangle(sigIRC.chatlogmodule_X,sigIRC.chatlogmodule_Y,sigIRC.chatlogmodule_width,sigIRC.chatlogmodule_height),
				"Chat Log"
				));
		sigIRC.chatlogmodule_enabled=true;
		sigIRC.config.setBoolean("Module_chatlog_Enabled", sigIRC.chatlogmodule_enabled);
		sigIRC.config.saveProperties();
	}
	public static void unloadModule() {
		for (int i=0;i<sigIRC.modules.size();i++) {
			if (sigIRC.modules.get(i) instanceof ChatLogModule) {
				sigIRC.modules.remove(sigIRC.modules.get(i));
			}
		}
		sigIRC.chatlogmodule_enabled=false;
		sigIRC.config.setBoolean("Module_chatlog_Enabled", sigIRC.chatlogmodule_enabled);
		sigIRC.config.saveProperties();
	}
	
	public void run() {
		super.run();
		if (delay>0 && sigIRC.subEmotesCompleted) {
			delay--;
		} else 
		if (!initialized)
		{
			Initialize();
			initialized=true;
		}
		for (int i=0; i<messageHistory.size();i++) {
			ChatLogMessage clm = messageHistory.get(i);
			if (clm!=null) {
				try {
					clm.run();
				} catch (ConcurrentModificationException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void draw(Graphics g) {
		super.draw(g);
		g.setColor(backgroundColor);
		g.fill3DRect((int)position.getX(), (int)position.getY(), (int)position.getWidth(), (int)position.getHeight(), true);
		g.setColor(Color.BLACK);
		for (int i=0; i<messageHistory.size();i++) {
			ChatLogMessage clm = messageHistory.get(i);
			if (clm!=null) {
				try {
					clm.draw(g);
				} catch (ConcurrentModificationException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void ApplyConfigWindowProperties() {
		sigIRC.chatlogmodule_X=(int)position.getX();
		sigIRC.chatlogmodule_Y=(int)position.getY();
		sigIRC.config.setInteger("CHATLOG_module_X", sigIRC.chatlogmodule_X);
		sigIRC.config.setInteger("CHATLOG_module_Y", sigIRC.chatlogmodule_Y);
	}
	
	public void mouseWheel(MouseWheelEvent ev) {
		if (mouseInBounds(ev.getX(),ev.getY())) {
			int scrollMult = 8;
			int scrollAmt = -ev.getWheelRotation()*scrollMult;
			if (scrollAmt>0) {
				if (HighestMessageIsVisible()) {
					return;
				}
			}
			if (scrolllog_yoffset+scrollAmt>0) {
				scrolllog_yoffset+=scrollAmt;	
				moveAllMessages(scrollAmt);
				//System.out.println("Moving all messages by "+(-(ev.getWheelRotation()*scrollMult)));
			} else 
			{	
				//System.out.println("Cannot move beyond lower bound. Moving all messages by -"+(scrolllog_yoffset)+".");
				moveAllMessages(-scrolllog_yoffset);
				scrolllog_yoffset=0;
			}
		}
	}
	
	private boolean HighestMessageIsVisible() {
		if (messageHistory.size()>0) {
			ChatLogMessage clm = messageHistory.get(0);
			return clm.isVisible();
		}
		return true;
	}

	public void moveAllMessages(int yAmt) {
		for (int i=0;i<messageHistory.size();i++) {
			ChatLogMessage clm = messageHistory.get(i);
			if (clm!=null) {
				try {
					clm.position.setLocation(clm.position.getX(), clm.position.getY()+yAmt);
				} catch (ConcurrentModificationException e) {
					e.printStackTrace();
				}
			}
		}			
	}

	private boolean mouseInBounds(int mouseX, int mouseY) {
		return mouseX>=position.getX() &&
				mouseX<=position.getX()+position.getWidth() &&
				mouseY>=position.getX() &&
				mouseY<=position.getX()+position.getHeight();
	}


	public void keypressed(KeyEvent ev) {
		int key = ev.getKeyCode();
		int scroll = 0;
		if (key==KeyEvent.VK_PAGE_UP) {
			scroll=8;
		} else
		if (key==KeyEvent.VK_PAGE_DOWN) {
			scroll=-8;
		}
		if (key==KeyEvent.VK_HOME) {
			scroll=Math.abs(GetHighestMessagePosition());
		}
		if (key==KeyEvent.VK_END) {
			moveAllMessages(-scrolllog_yoffset);
			scrolllog_yoffset=0;
			return;
		}
		if (scroll>0) {
			if (HighestMessageIsVisible()) {
				return;
			}
		}
		if (scrolllog_yoffset+scroll>0) {
			scrolllog_yoffset+=scroll;	
			moveAllMessages(scroll);
			//System.out.println("Moving all messages by "+(-(ev.getWheelRotation()*scrollMult)));
		} else 
		{	
			//System.out.println("Cannot move beyond lower bound. Moving all messages by -"+(scrolllog_yoffset)+".");
			moveAllMessages(-scrolllog_yoffset);
			scrolllog_yoffset=0;
		}
	}

	private int GetHighestMessagePosition() {
		if (messageHistory.size()>0) {
			ChatLogMessage clm = messageHistory.get(0);
			return (int)clm.position.getY();
		}
		return 0;
	}
}
