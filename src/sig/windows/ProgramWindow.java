package sig.windows;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;
import sig.BackgroundColorButton;
import sig.ColorPanel;
import sig.Module;
import sig.MyPanel;
import sig.sigIRC;
import sig.modules.ChatLogModule;
import sig.modules.RabiRaceModule;
import sig.modules.ScrollingChatModule;

@SuppressWarnings("serial")
public class ProgramWindow extends JFrame{

	public static Icon deselected_icon,selected_icon; 
	
	List<ModuleSelectButton> buttons = new ArrayList<ModuleSelectButton>();

	public int lastMouseX = 0;

	public int lastMouseY = 0;
	
	public static ProgramWindow frame;
	
	public ProgramWindow() {
		ProgramWindow.frame=this;
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleWithFixedDelay(()->{
			if (sigIRC.configNeedsUpdating>0 &&
					System.currentTimeMillis()-sigIRC.configNeedsUpdating>1000) {
				sigIRC.config.saveProperties();
				sigIRC.configNeedsUpdating=0;
			}
		},1000,1000,TimeUnit.MILLISECONDS);
		scheduler.scheduleWithFixedDelay(()->{
	        lastMouseX = (int)(MouseInfo.getPointerInfo().getLocation().getX());
	        lastMouseY = (int)(MouseInfo.getPointerInfo().getLocation().getY());
		},(long)((1d/(sigIRC.framerate+1))*1000),(long)((1d/(sigIRC.framerate+1))*1000),TimeUnit.MILLISECONDS);
		
		try {
			sigIRC.programIcon = ImageIO.read(sigIRC.class.getResource("/resource/sigIRCicon.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			deselected_icon = new ImageIcon(ImageIO.read(sigIRC.class.getResource("/resource/deselected_button.png")));
			selected_icon = new ImageIcon(ImageIO.read(sigIRC.class.getResource("/resource/selected_button.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (sigIRC.overlayMode && sigIRC.showWindowControls) {
			JFrame.setDefaultLookAndFeelDecorated(true);
		}
		System.setProperty("sun.java2d.opengl", Boolean.toString(sigIRC.hardwareAcceleration));
		System.setProperty("sun.java2d.d3d",Boolean.toString(true));
        JFrame f = new JFrame("sigIRCv2");
        this.setAutoRequestFocus(true);
        this.toFront();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		if (sigIRC.overlayMode && !sigIRC.showWindowControls) {
			this.setUndecorated(true);
		}
		
		
        sigIRC.panel = new MyPanel();
        
        JLabel myLabel = new JLabel("Module Control:");
        if (sigIRC.overlayMode) { 
        	sigIRC.panel.setOpaque(false);
        }
        sigIRC.panel.setBackground(Color.BLACK);
		myLabel.setBackground(sigIRC.panel.getBackground());
		myLabel.setForeground(Color.WHITE);
		myLabel.setIcon(new ImageIcon(sigIRC.programIcon.getScaledInstance(48, 48, Image.SCALE_AREA_AVERAGING)));
		
		sigIRC.panel.add(myLabel);
        
        if (!sigIRC.disableChatMessages) {
        	ScrollingChatModule mod = new ScrollingChatModule(new Rectangle((int)sigIRC.scrollingchatmodule_X,(int)sigIRC.scrollingchatmodule_Y,(int)sigIRC.scrollingchatmodule_width,(int)sigIRC.scrollingchatmodule_height),"Scrolling Chat");
        	ModuleSelectButton button = new ModuleSelectButton("Scrolling Chat",mod);
        	sigIRC.panel.add(button);
        }
        if (sigIRC.chatlogmodule_enabled) {
            ChatLogModule mod = new ChatLogModule(new Rectangle(sigIRC.chatlogmodule_X,sigIRC.chatlogmodule_Y,sigIRC.chatlogmodule_width,sigIRC.chatlogmodule_height),"Chat Log");
        	ModuleSelectButton button = new ModuleSelectButton("Chat Log",mod);
        	sigIRC.panel.add(button);
        }
        if (sigIRC.controllermodule_enabled) {
        	ModuleSelectButton button = new ModuleSelectButton("Controller",new Module(new Rectangle(0,0,0,0),"Test"));
        	sigIRC.panel.add(button);
        }
        if (sigIRC.twitchmodule_enabled) {
        	ModuleSelectButton button = new ModuleSelectButton("Twitch",new Module(new Rectangle(0,0,0,0),"Test"));
        	sigIRC.panel.add(button);
        }
        if (sigIRC.rabiracemodule_enabled) {
        	RabiRaceModule mod = new RabiRaceModule(new Rectangle((int)sigIRC.rabiracemodule_X,(int)sigIRC.rabiracemodule_Y,(int)sigIRC.rabiracemodule_width,(int)sigIRC.rabiracemodule_height),"Rabi Race");
        	ModuleSelectButton button = new ModuleSelectButton("Rabi-Race",mod);
        	sigIRC.panel.add(button);
        }
        if (sigIRC.touhoumothermodule_enabled) {
        	ModuleSelectButton button = new ModuleSelectButton("Touhou Mother",new Module(new Rectangle(0,0,0,0),"Test"));
        	sigIRC.panel.add(button);
        }
		GridLayout myLayout = new GridLayout(0,1);
		sigIRC.panel.setLayout(myLayout);
		
		///Play MUSIC
		/*BasicPlayer player = new BasicPlayer();
		try {
			player.open(new File("D:\\Videos\\4K Video Downloader\\3R2 - The Truth Never Spoken (Energetic Trance Mix).mp3"));
			player.play();
		} catch (BasicPlayerException e) {
			e.printStackTrace();
		}*/
        
        //colorpanel = new ColorPanel();
        //this.add(colorpanel);
        this.setLocationByPlatform(true);
        this.add(sigIRC.panel);
        //this.pack();
        this.setSize(240, 640);
        this.setVisible(true);
        //this.setLocation(sigIRC.windowX, sigIRC.windowY);
        //this.setSize(sigIRC.windowWidth, sigIRC.windowHeight);
        
       this.setIconImage(sigIRC.programIcon);

       // button = new BackgroundColorButton(new File(sigIRC.BASEDIR+"backcolor.png"),panel.getX()+panel.getWidth()-96,64+rowobj.size()*rowSpacing);
        if (sigIRC.overlayMode) {
        	this.setBackground(new Color(0,0,0,0));
            this.setAlwaysOnTop(true);
        }
        //this.setOpacity(0.5f);
        //this.addWindowListener(sigIRC.panel);
        
        //Module testMod = new Module(new Rectangle(0,0,640,480),"Test");
	}
}