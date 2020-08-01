package sig.modules.RabiRace;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import sig.sigIRC;
import sig.modules.RabiRaceModule;
import sig.utils.DebugUtils;
import sig.utils.FileUtils;

public class SessionListWindow extends JFrame{
	JPanel container = new JPanel();
	public JList<String> sessionlist = new JList<String>();
	public DefaultListModel<String> sessionlist_model = new DefaultListModel<String>();
	public int selected = -1;
	public DataPanel previewPanel = new DataPanel();
	public JButton joinButton = new JButton("Join");
	public JScrollPane scrolllist = new JScrollPane();
	public PasswordBox box = new PasswordBox();
	public String enteredPassword = "";
	DecimalFormat df = new DecimalFormat("0.00");
	
	
	public SessionListWindow(){
		this.setTitle("Rabi-Race Sessions List");
		this.setIconImage(sigIRC.programIcon);
		
		previewPanel.setWindow(this);
		
		scrolllist.setViewportView(sessionlist);
		scrolllist.setPreferredSize(new Dimension(320,150));
		scrolllist.setMinimumSize(new Dimension(320,150));
		scrolllist.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JPanel sessionPanel = new JPanel();
		
		sessionlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sessionlist.setLayoutOrientation(JList.VERTICAL);
		sessionlist.setVisibleRowCount(6);
		
		sessionPanel.setLayout(new BoxLayout(sessionPanel,BoxLayout.PAGE_AXIS));
		sessionPanel.setSize(160,200);
		sessionPanel.setMinimumSize(new Dimension(160,200));
		
		sessionPanel.add(scrolllist);
		sessionPanel.add(Box.createRigidArea(new Dimension(10,10)));
		sessionPanel.add(joinButton);
		
		UpdateSessionList();
		joinButton.setEnabled(false);
		joinButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {

				if (sessionlist.getSelectedIndex()!=-1) {
					selected = sessionlist.getSelectedIndex();
					Session session = RabiRaceModule.module.session_listing.data.get(getSelectedID());
					
					if (!session.password.equalsIgnoreCase("none")) {
						box.displayPasswordBox();
						ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
						scheduler.scheduleWithFixedDelay(()->{
							if (enteredPassword.length()!=0) {
								File file = new File(sigIRC.BASEDIR+"sigIRC/tmp.data");

								String hashedPass = GetHashedPassword(enteredPassword);
								
								ConnectToSession(session, hashedPass);
								
								enteredPassword="";
								
								scheduler.shutdownNow();	
							}
						}, 1000l, 1000l, TimeUnit.MILLISECONDS);
					} else {
						ConnectToSession(session, "");
					}
					if (RabiRaceModule.mySession!=null) {
						setVisible(false);
					}
					//Attempt to join the session.
					
					
				}
			}	
		});
		
		sessionlist.setModel(sessionlist_model);
		sessionlist.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (sessionlist.getSelectedIndex()!=-1) {
					selected = sessionlist.getSelectedIndex();
					Session session = RabiRaceModule.module.session_listing.data.get(getSelectedID());
					if (RabiRaceModule.module.mySession==null &&
							session.maxPlayers!=session.players.size()) {
						joinButton.setEnabled(true);
					} else {
						joinButton.setEnabled(false);
					}
				} else {
					joinButton.setEnabled(false);
				}
			}
			
		});

		container.add(Box.createRigidArea(new Dimension(10,1)));
		container.add(sessionPanel);
		container.add(Box.createRigidArea(new Dimension(10,1)));
		container.add(previewPanel);
		previewPanel.setPreferredSize(new Dimension(400,300));
		
		
		this.add(container);
		this.setMinimumSize(new Dimension(720,480));
	}

	public static void ConnectToSession(Session session, String hashedPass) {
		try {
			if (hashedPass.length()==0) {
				hashedPass="none";
			}
			File file = new File(sigIRC.BASEDIR+"sigIRC/tmp_session.data");
			org.apache.commons.io.FileUtils.copyURLToFile(new URL("http://45.33.13.215:8080/rabirace/send.php?key=joinsession&timekey="+RabiRaceModule.CLIENT_SERVER_READTIME+"&name="+RabiRaceModule.module.myProfile.username+"&session="+session.id+"&password="+hashedPass),file);
			String[] data = FileUtils.readFromFile(sigIRC.BASEDIR+"sigIRC/tmp_session.data");
			
			if (data.length==1) {
				int errorCode = Integer.parseInt(data[0]);
				switch (errorCode) {
					case 400:
					case 404:{
						JOptionPane.showMessageDialog(RabiRaceModule.module.window, "Invalid format sent. Please contact the dev! This should not be happening! \n"+DebugUtils.getStackTrace(), "Error "+errorCode, JOptionPane.WARNING_MESSAGE);
					}break;
					case 403:{
						JOptionPane.showMessageDialog(RabiRaceModule.module.window, "Session does not exist!", "Error "+errorCode, JOptionPane.WARNING_MESSAGE);
					}break;
					case 405:{
						JOptionPane.showMessageDialog(RabiRaceModule.module.window, "Session room is full!", "Error "+errorCode, JOptionPane.WARNING_MESSAGE);
					}break;
					case 406:{
						JOptionPane.showMessageDialog(RabiRaceModule.module.window, "Incorrect Password! "+hashedPass, "Error "+errorCode, JOptionPane.WARNING_MESSAGE);
					}break;
					case 0:{
						RabiRaceModule.mySession = session;
					}break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		this.repaint();
	}
	
	public static String GetHashedPassword(String input) {
	   try {
	        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
	        byte[] array = md.digest(input.getBytes());
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < array.length; ++i) {
	          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
	       }
			return sb.toString();
	    } catch (java.security.NoSuchAlgorithmException e) {
	 	   return null;
	    }
	}

	public void UpdateSessionList() {
		selected = sessionlist.getSelectedIndex();
		int selectedID = getSelectedID();
		sessionlist_model.clear();
		int count=0;
		for (Integer id : RabiRaceModule.module.session_listing.data.keySet()) {
			Session session = RabiRaceModule.module.session_listing.data.get(id);
			sessionlist_model.addElement((session.password.equalsIgnoreCase("none")?"":"🔑 ")+session.id+" - "+session.name+"  ("+session.players.size()+"/"+session.maxPlayers+")"+((session.difficulty!=-1)?" - Rating: "+df.format(session.difficulty):""));
			if (id == selectedID && sessionlist_model.getSize()>count) {
				sessionlist.setSelectedIndex(count);
			}
			count++;
		}

		FileUtils.logToFile("["+System.currentTimeMillis()+"]Updated sessions listing.", "debug.log");
		//System.out.println("Selected is "+selected);
		//Try to find ID in list.
	}

	public int getSelectedID() {
		if (selected!=-1) {
			return Integer.parseInt(sessionlist_model.getElementAt(selected).replaceAll("🔑", "").split(" - ")[0].trim());
		}
		return -1;
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
		        int selectedID = getSelectedID(); 
	        	//Get the players from that session.
	        	Session s = RabiRaceModule.module.session_listing.data.get(selectedID);
	        	
	        	Profile.DrawMultiPanel(g,0,0,400,s);
	        }
	    }
	}
	
	class PasswordBox extends JFrame{
		JPasswordField pass = new JPasswordField();
		JButton okay = new JButton("Submit");
		JPanel container = new JPanel();
		public PasswordBox(){
			this.setVisible(false);
			container.setLayout(new BoxLayout(container,BoxLayout.PAGE_AXIS));
			container.add(Box.createRigidArea(new Dimension(240,20)));
			JPanel label_panel = new JPanel();
			label_panel.setLayout(new BoxLayout(label_panel,BoxLayout.LINE_AXIS));
			label_panel.add(Box.createRigidArea(new Dimension(20,5)));
			JLabel label = new JLabel("Please enter the password required to join this session:");
			label_panel.add(label);
			label_panel.add(Box.createRigidArea(new Dimension(20,5)));
			label.setLayout(new BoxLayout(label,BoxLayout.LINE_AXIS));
			container.add(label_panel);
			container.add(Box.createRigidArea(new Dimension(240,5)));
			JPanel pass_row = new JPanel();
			pass_row.setLayout(new BoxLayout(pass_row,BoxLayout.LINE_AXIS));
			pass.setMinimumSize(new Dimension(120,20));
			pass.setPreferredSize(new Dimension(120,20));
			pass_row.setSize(240,20);
			pass_row.add(Box.createRigidArea(new Dimension(20,5)));
			pass_row.add(pass);
			pass_row.add(Box.createRigidArea(new Dimension(20,5)));
			
			okay.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent ev) {
					enteredPassword = String.copyValueOf(pass.getPassword());
					box.setVisible(false);
				}
			});
			
			container.add(pass_row);
			container.add(okay);
			container.add(Box.createRigidArea(new Dimension(240,20)));
			this.add(container);
			this.pack();
		}
		
		public void displayPasswordBox() {
			this.setVisible(true);
			pass.setText("");
		}
	}
}
