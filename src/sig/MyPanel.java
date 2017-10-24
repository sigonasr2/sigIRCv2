package sig;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JColorChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import sig.modules.ChatLogModule;
import sig.modules.ChatLog.ChatLogMessage;
import sig.utils.FileUtils;

public class MyPanel extends JPanel implements MouseListener, ActionListener, MouseWheelListener, KeyListener, ComponentListener, WindowListener{
	//List<String> messages = new ArrayList<String>();
	final public static Font programFont = new Font(sigIRC.messageFont,0,24);
	final public static Font userFont = new Font(sigIRC.usernameFont,0,16);
	final public static Font smallFont = new Font(sigIRC.touhoumotherConsoleFont,0,12);
	public int lastMouseX = 0;
	public int lastMouseY = 0;

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
        if (sigIRC.panel!=null) {
	        lastMouseX = (int)(MouseInfo.getPointerInfo().getLocation().getX()-sigIRC.panel.getLocationOnScreen().getX());
	        lastMouseY = (int)(MouseInfo.getPointerInfo().getLocation().getY()-sigIRC.panel.getLocationOnScreen().getY());
	        //System.out.println("("+lastMouseX+","+lastMouseY+")");
        }
        /*if (sigIRC.window!=null && sigIRC.window.getMousePosition(true)!=null && sigIRC.overlayMode) {
	        lastMouseX = (int)sigIRC.window.getMousePosition(true).getX();
	        lastMouseY = (int)sigIRC.window.getMousePosition(true).getY();
	        System.out.println("("+lastMouseX+","+lastMouseY+")");
        } else {
        	lastMouseX = -1;
        	lastMouseY = -1;
        }*/
		//
        for (Module m : sigIRC.modules) {
        	m.draw(g);
        }
        for (int i=0;i<sigIRC.chatlogtwitchemoticons.size();i++) {
        	if (sigIRC.chatlogtwitchemoticons.get(i).textRefIsVisible()) {
        		sigIRC.chatlogtwitchemoticons.get(i).draw(g);
        	}
        }
        for (int i=0;i<sigIRC.textobj.size();i++) {
        	if (sigIRC.textobj.get(i).isActive()) {
        		if (sigIRC.overlayMode) {
	        		if (!sigIRC.textobj.get(i).intersects(lastMouseX,lastMouseY)) {
	        			sigIRC.textobj.get(i).setVisible(true);
	        			sigIRC.textobj.get(i).draw(g);
	        		} else {
	        			//System.out.println("Setting to False.");
	        			sigIRC.textobj.get(i).setVisible(false);
	        		}
        		} else {
        			sigIRC.textobj.get(i).draw(g);
        		}
        	}
        }
        for (int i=0;i<sigIRC.twitchemoticons.size();i++) {
        	if (sigIRC.twitchemoticons.get(i).isActive() &&
        			sigIRC.twitchemoticons.get(i).textRefIsVisible()) {
        		sigIRC.twitchemoticons.get(i).draw(g);
        	} else {
        		break;
        	}
        }
        if (!sigIRC.overlayMode) {
        	sigIRC.button.draw(g);
        }
    }  
    
    public void addMessage(String message) {
    	addMessage(message,true);
    }
    
    public void addMessage(String message, boolean playSound) {
    	ScrollingText text = new ScrollingText(message,this.getWidth(),(int)(Math.random()*128),playSound);
    	TextRow row = TextRow.PickRandomTextRow(text.getUsername());
    	sigIRC.textobj.add(text);
    	row.updateRow(text);
    	//ChatLogMessage.importMessages(message);
    }

	@Override
	public void mouseClicked(MouseEvent ev) {
	}

	@Override
	public void mousePressed(MouseEvent ev) {
		for (Module m : sigIRC.modules) {
			m.mouseModuleMousePress(ev);
		}
        sigIRC.button.onClickEvent(ev);
	}

	@Override
	public void mouseReleased(MouseEvent ev) {
		for (Module m : sigIRC.modules) {
			m.mouseReleased(ev);
		}
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
		UpdateComponent(ev.getComponent());
	}

	@Override
	public void componentMoved(ComponentEvent ev) {
		UpdateComponent(ev.getComponent());
	}

	public static void UpdateComponent(Component com) {
		sigIRC.windowX = (int)sigIRC.window.getLocationOnScreen().getX(); 
		sigIRC.windowY = (int)sigIRC.window.getLocationOnScreen().getY();
		sigIRC.windowWidth = sigIRC.window.getWidth(); 
		sigIRC.windowHeight = sigIRC.window.getHeight();
		sigIRC.config.setInteger("windowX", sigIRC.windowX);
		sigIRC.config.setInteger("windowY", sigIRC.windowY);
		sigIRC.config.setInteger("windowWidth", sigIRC.windowWidth);
		sigIRC.config.setInteger("windowHeight", sigIRC.windowHeight);
		sigIRC.button.x = sigIRC.panel.getX()+sigIRC.panel.getWidth()-96;
		sigIRC.button.y = 64+sigIRC.rowobj.size()*sigIRC.rowSpacing;
		com.repaint();
		sigIRC.panel.repaint();
		sigIRC.config.saveProperties();
	}

	@Override
	public void componentShown(ComponentEvent ev) {
	}

	@Override
	public void componentHidden(ComponentEvent ev) {
	}

	@Override
	public void windowActivated(WindowEvent ev) {
	}

	@Override
	public void windowClosed(WindowEvent ev) {
	}

	@Override
	public void windowClosing(WindowEvent ev) {
		for (Module m : sigIRC.modules) {
			m.windowClosed(ev);
		}
		sigIRC.config.saveProperties();
		if (sigIRC.autoUpdateProgram) {
			try {
				FileUtils.copyFile(new File(sigIRC.PROGRAM_UPDATE_FILE), new File(sigIRC.BASEDIR+"sigIRCv2.jar"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void windowDeactivated(WindowEvent ev) {
	}

	@Override
	public void windowDeiconified(WindowEvent ev) {
	}

	@Override
	public void windowIconified(WindowEvent ev) {
	}

	@Override
	public void windowOpened(WindowEvent ev) {
	}
}
