package sig.windows;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import sig.BackgroundColorButton;
import sig.ColorPanel;
import sig.MyPanel;
import sig.sigIRC;

public class ProgramWindow extends JFrame{
	
	static Icon deselected_icon,selected_icon;
	
	List<ModuleButton> buttons = new ArrayList<ModuleButton>();
	
	public ProgramWindow() {
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
        if (sigIRC.overlayMode) { 
        	sigIRC.panel.setOpaque(false);
        }
        sigIRC.panel.setBackground(sigIRC.backgroundcol);
        
        if (!sigIRC.disableChatMessages) {
        	
        }
        
        //colorpanel = new ColorPanel();
        //this.add(colorpanel);
        this.add(sigIRC.panel);
        this.pack();
        this.setVisible(true);
        this.setLocation(sigIRC.windowX, sigIRC.windowY);
        this.setSize(sigIRC.windowWidth, sigIRC.windowHeight);
        
       this.setIconImage(sigIRC.programIcon);

       // button = new BackgroundColorButton(new File(sigIRC.BASEDIR+"backcolor.png"),panel.getX()+panel.getWidth()-96,64+rowobj.size()*rowSpacing);
        if (sigIRC.overlayMode) {
        	this.setBackground(new Color(0,0,0,0));
            this.setAlwaysOnTop(true);
        }
        //this.setOpacity(0.5f);
        this.addWindowListener(sigIRC.panel);
	}
}

class ModuleButton extends JToggleButton {
	String label = "";
	ModuleButton button;
	public ModuleButton(String label) {
		this.label=label;
		this.button=this;
		this.setBackground(Color.DARK_GRAY);
		button.setForeground(Color.GRAY);
		button.setIconTextGap(4);
		button.setIcon(IntroDialog.deselected_icon);
		button.setSelectedIcon(IntroDialog.selected_icon);
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