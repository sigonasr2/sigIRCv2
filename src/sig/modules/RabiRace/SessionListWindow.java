package sig.modules.RabiRace;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import sig.modules.RabiRaceModule;

public class SessionListWindow extends JFrame{
	JPanel container = new JPanel();
	public JList<String> sessionlist = new JList<String>();
	public DefaultListModel<String> sessionlist_model = new DefaultListModel<String>();
	public int selected = -1;
	public DataPanel previewPanel = new DataPanel();
	
	public SessionListWindow(){
		
		previewPanel.setWindow(this);
		
		sessionlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sessionlist.setLayoutOrientation(JList.VERTICAL);
		sessionlist.setPreferredSize(new Dimension(120,150));
		
		UpdateSessionList();
		
		sessionlist.setModel(sessionlist_model);
		
		container.add(sessionlist);
		container.add(previewPanel);
		previewPanel.setPreferredSize(new Dimension(400,300));
		
		
		this.add(container);
		this.setMinimumSize(new Dimension(640,480));
	}
	
	public void run() {
		this.repaint();
	}

	public void UpdateSessionList() {
		selected = sessionlist.getSelectedIndex();
		sessionlist_model.clear();
		int count=0;
		for (Session session : RabiRaceModule.module.session_listing.data) {
			sessionlist_model.addElement(session.id+" - "+session.name+"  ("+session.players.size()+"/"+session.maxPlayers+")");
			count++;
		}
		if (count>=selected) {
			sessionlist.setSelectedIndex(selected);
		}
	}

	class DataPanel extends JPanel{
		SessionListWindow window;
		public void setWindow(SessionListWindow window) {
			this.window=window;
		}
	    public void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        //Axis.GetAxisDisplay(g,window.ConstructTemporaryAxis(),0,0,window.axis_width,window.axis_height);
	        //Axis.GetAxisIndicatorDisplay(g,window.ConstructTemporaryAxis(),0,0,window.axis_width,window.axis_height);
	        if (selected!=-1 &&
	        		RabiRaceModule.module.session_listing.data.size()>selected) {
	        	//Get the players from that session.
	        	Session s = RabiRaceModule.module.session_listing.data.get(selected);
	        	
	        	Profile.DrawMultiPanel(g,0,0,400,s.players);
	        }
	    }
	}
}
