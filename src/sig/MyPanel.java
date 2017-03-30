package sig;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class MyPanel extends JPanel implements MouseListener, ActionListener, MouseWheelListener, KeyListener{
	//List<String> messages = new ArrayList<String>();
	final public static Font programFont = new Font("Gill Sans Ultra Bold Condensed",0,24);
	final public static Font userFont = new Font("Gill Sans",0,16);
	final public static Font smallFont = new Font("Agency FB Bold",0,12);

    public MyPanel() {
        //setBorder(BorderFactory.createLineBorder(Color.black));
    	addMouseListener(this);
    	addMouseWheelListener(this);
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
        for (TwitchEmote e : sigIRC.twitchemoticons) {
        	e.draw(g);
        }
        for (ScrollingText st : sigIRC.textobj) {
        	st.draw(g);
        }
        for (Module m : sigIRC.modules) {
        	m.draw(g);
        }
    }  
    
    public void addMessage(String message) {
    	ScrollingText text = new ScrollingText(message,this.getWidth(),(int)(Math.random()*128),sigIRC.TEXTSCROLLSPD);
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
}
