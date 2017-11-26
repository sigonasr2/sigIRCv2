package sig.modules.RabiRace;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import sig.sigIRC;
import sig.modules.RabiRaceModule;
import sig.utils.ReflectUtils;

public class AvatarSelectionWindow extends JFrame{
	static ImagePanel[] avatars = new ImagePanel[Avatar.values().length];
	static AvatarSelectionWindow avatarwindow;
	final static int WINDOW_WIDTH = 350; 
	final static int COLUMNS = WINDOW_WIDTH/50;
	final static int WINDOW_HEIGHT = ((Avatar.values().length/COLUMNS)+1)*50;
	static boolean mousePressed = false;
	
	public AvatarSelectionWindow() {
		this.setVisible(false);
		this.setTitle("Avatar Selection");
		this.setIconImage(sigIRC.programIcon);
		avatarwindow = this;
		JPanel panels = new JPanel();
		JPanel container = new JPanel();
		panels.setLayout(new BoxLayout(panels,BoxLayout.PAGE_AXIS));
		container.setLayout(new BoxLayout(container,BoxLayout.LINE_AXIS));
		container.setSize(WINDOW_WIDTH, 50);
		int i=0;
		panels.add(container);
		for (int k=0;k<avatars.length;k++) {
			ImagePanel j = new ImagePanel(RabiRaceModule.image_map.get(Avatar.values()[i].fileName),i%COLUMNS,i/COLUMNS,Avatar.values()[i].value);
			avatars[k] = j;
			j.setSize(50,50);
			j.setPreferredSize(new Dimension(50,50));
			container.add(j);
			i++;
			if (i%COLUMNS==0) {
				container = new JPanel();
				container.setLayout(new BoxLayout(container,BoxLayout.LINE_AXIS));
				container.setSize(WINDOW_WIDTH, 50);
				panels.add(container);
			}
		}
		while (i%COLUMNS!=0) {
			container.add(Box.createRigidArea(new Dimension(50,50)));
			i++;
		}
		this.add(panels);
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT+48);
		this.setResizable(false);
	}
}

class ImagePanel extends JPanel implements MouseListener{
	Image img;
	int x,y;
	public boolean selected=false;
	int myID = 0;
	
	public ImagePanel(Image img, int x, int y, int myID) {
		this.img = img;
		this.setSize(50,50);
		this.x=x;
		this.y=y;
		this.myID = myID;
		addMouseListener(this);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (selected) {
			g.setColor(new Color(0,0,64));
			g.fillRect(0, 0, 50, 50);
		}
		g.drawImage(img, 0, 0, this);
		if (selected) {
			g.setColor(Color.YELLOW);
			for (int i=0;i<2;i++) {
				g.drawRect(i, i, 50-i, 50-i);
			}
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getName()+"(");
		boolean first=false;
		for (Field f : this.getClass().getDeclaredFields()) {
			if (!first) {
				try {
					sb.append(f.getName()+"="+f.get(this));
					first=true;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			} else {
				try {
					sb.append(","+f.getName()+"="+f.get(this));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		sb.append(")");
		return sb.toString();
	}
	
	@Override
	public void mousePressed(MouseEvent arg0) {		
		for (ImagePanel i : AvatarSelectionWindow.avatars) {
			if (i!=null) {
				i.selected = false;
				i.repaint();
			}
		}
		selected = true;
		AvatarSelectionWindow.mousePressed=true;
		RabiRaceModule.module.myProfile.avatar = Avatar.getAvatarFromID(myID);
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {	
		if (AvatarSelectionWindow.mousePressed) {
			mousePressed(e);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {		
	}

	@Override
	public void mouseReleased(MouseEvent e) {	
		AvatarSelectionWindow.mousePressed=false;	
	}
}