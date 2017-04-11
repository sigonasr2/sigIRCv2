package sig;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JColorChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class MyPanel extends JPanel implements MouseListener, ActionListener, MouseWheelListener, KeyListener, ComponentListener{
	//List<String> messages = new ArrayList<String>();
	final public static Font programFont = new Font(sigIRC.messageFont,0,24);
	final public static Font userFont = new Font(sigIRC.usernameFont,0,16);
	final public static Font smallFont = new Font(sigIRC.touhoumotherConsoleFont,0,12);

    public MyPanel() {
        //setBorder(BorderFactory.createLineBorder(Color.black));
    	addMouseListener(this);
    	addMouseWheelListener(this);
        addComponentListener(this);
    	addKeyListener(this);
    	setFocusable(true);
    }

    public Dimension getPreferredSize() {
        return new Dimension(1280,480);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);      
        // Draw Text
        //int counter=18;
        for (int i=0;i<sigIRC.twitchemoticons.size();i++) {
        	if (sigIRC.twitchemoticons.get(i).isActive()) {
        		sigIRC.twitchemoticons.get(i).draw(g);
        	} else {
        		break;
        	}
        }
        for (int i=0;i<sigIRC.textobj.size();i++) {
        	if (sigIRC.textobj.get(i).isActive()) {
        		sigIRC.textobj.get(i).draw(g);
        	} else {
        		break;
        	}
        }
        for (Module m : sigIRC.modules) {
        	m.draw(g);
        }
        if (!sigIRC.overlayMode) {
        	sigIRC.button.draw(g);
        }
    }  
    
    public void addMessage(String message) {
    	ScrollingText text = new ScrollingText(message,this.getWidth(),(int)(Math.random()*128));
    	TextRow row = TextRow.PickRandomTextRow(text.getUsername());
    	sigIRC.textobj.add(text);
    	row.updateRow(text);
    }

	@Override
	public void mouseClicked(MouseEvent ev) {
	}

	@Override
	public void mousePressed(MouseEvent ev) {
		for (Module m : sigIRC.modules) {
			m.mousePressed(ev);
		}
        sigIRC.button.onClickEvent(ev);
	}

	@Override
	public void mouseReleased(MouseEvent ev) {
	}

	@Override
	public void mouseEntered(MouseEvent ev) {
	}

	@Override
	public void mouseExited(MouseEvent ev) {
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent ev) {
		for (Module m : sigIRC.modules) {
			m.mouseWheel(ev);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent ev) {
		for (Module m : sigIRC.modules) {
			m.keypressed(ev);
		}
	}

	@Override
	public void keyReleased(KeyEvent ev) {
		for (Module m : sigIRC.modules) {
			m.keyreleased(ev);
		}
	}

	@Override
	public void componentResized(ComponentEvent ev) {
		sigIRC.windowX = sigIRC.window.getX(); 
		sigIRC.windowY = sigIRC.window.getY();
		sigIRC.windowWidth = sigIRC.window.getWidth(); 
		sigIRC.windowHeight = sigIRC.window.getHeight();
		sigIRC.config.setInteger("windowX", sigIRC.windowX);
		sigIRC.config.setInteger("windowY", sigIRC.windowY);
		sigIRC.config.setInteger("windowWidth", sigIRC.windowWidth);
		sigIRC.config.setInteger("windowHeight", sigIRC.windowHeight);
		sigIRC.button.x = sigIRC.panel.getX()+sigIRC.panel.getWidth()-96;
		sigIRC.button.y = 64+sigIRC.rowobj.size()*sigIRC.rowSpacing;
		sigIRC.config.saveProperties();
	}

	@Override
	public void componentMoved(ComponentEvent ev) {
		sigIRC.windowX = sigIRC.window.getX(); 
		sigIRC.windowY = sigIRC.window.getY();
		sigIRC.windowWidth = sigIRC.window.getWidth(); 
		sigIRC.windowHeight = sigIRC.window.getHeight();
		sigIRC.config.setInteger("windowX", sigIRC.windowX);
		sigIRC.config.setInteger("windowY", sigIRC.windowY);
		sigIRC.config.setInteger("windowWidth", sigIRC.windowWidth);
		sigIRC.config.setInteger("windowHeight", sigIRC.windowHeight);
		sigIRC.button.x = sigIRC.panel.getX()+sigIRC.panel.getWidth()-96;
		sigIRC.button.y = 64+sigIRC.rowobj.size()*sigIRC.rowSpacing;
		sigIRC.config.saveProperties();
	}

	@Override
	public void componentShown(ComponentEvent ev) {
	}

	@Override
	public void componentHidden(ComponentEvent ev) {
	}
}
