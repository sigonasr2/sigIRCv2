package sig.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import sig.BackgroundColorButton;
import sig.ColorPanel;
import sig.Module;
import sig.MyPanel;
import sig.sigIRC;

public class ProgramWindow extends JFrame{
	
	static Icon deselected_icon,selected_icon;
	
	List<ModuleButton> buttons = new ArrayList<ModuleButton>();
	
	public ProgramWindow() {
		
		try {
			sigIRC.programIcon = ImageIO.read(new File(sigIRC.BASEDIR+"/sigIRC/sigIRCicon.png"));
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
        	ModuleButton button = new ModuleButton("Scrolling Chat");
        	sigIRC.panel.add(button);
        }
        if (sigIRC.chatlogmodule_enabled) {
        	ModuleButton button = new ModuleButton("Chat Log");
        	sigIRC.panel.add(button);
        }
        if (sigIRC.controllermodule_enabled) {
        	ModuleButton button = new ModuleButton("Controller");
        	sigIRC.panel.add(button);
        }
        if (sigIRC.twitchmodule_enabled) {
        	ModuleButton button = new ModuleButton("Twitch");
        	sigIRC.panel.add(button);
        }
        if (sigIRC.rabiracemodule_enabled) {
        	ModuleButton button = new ModuleButton("Rabi-Race");
        	sigIRC.panel.add(button);
        	sigIRC.panel.add(new ModuleButton("Rabi-Race"));
        }
        if (sigIRC.touhoumothermodule_enabled) {
        	ModuleButton button = new ModuleButton("Touhou Mother");
        	sigIRC.panel.add(button);
        }
		GridLayout myLayout = new GridLayout(0,1);
		sigIRC.panel.setLayout(myLayout);
        
        //colorpanel = new ColorPanel();
        //this.add(colorpanel);
        this.setLocationByPlatform(true);
        this.add(sigIRC.panel);
        this.pack();
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
        this.addWindowListener(sigIRC.panel);
        
        Module testMod = new Module(new Rectangle(0,0,640,480),"Test");
	}
}

class ModuleButton extends JToggleButton {
	String label = "";
	ModuleButton button;
	public ModuleButton(String label) {
		this.label=label;
		this.button=this;
		this.setBackground(Color.DARK_GRAY);
		this.setText(label);
		this.setToolTipText("Click to enable and disable the \n"+label+" module.");
		this.setPreferredSize(new Dimension(160,56));
		this.setForeground(Color.GRAY);
		this.setIconTextGap(4);
		this.setIcon(ProgramWindow.deselected_icon);
		this.setSelectedIcon(ProgramWindow.selected_icon);
		this.setSelected(true);
		button.setForeground(Color.BLUE);
		this.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (button.isSelected()) {
					button.setForeground(Color.BLUE);
				}
				else {
					button.setBackground(Color.DARK_GRAY);
					button.setForeground(Color.GRAY);
				}
			}
			
		});
	}
	
}