package sig;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

public class ListenerPanel extends JPanel implements MouseListener, MouseWheelListener{
	
	Module mod;
	
	public ListenerPanel(Module mod) {
		this.addMouseListener(this);
		this.addMouseWheelListener(this);
		this.mod=mod;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		this.mod.mouseWheelMoved(e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		this.mod.mouseClicked(e);		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.mod.mouseEntered(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		this.mod.mouseExited(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.mod.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		this.mod.mouseReleased(e);
	}
	
	
}