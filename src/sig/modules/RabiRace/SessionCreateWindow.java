package sig.modules.RabiRace;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import sig.sigIRC;
import sig.modules.RabiRaceModule;
import sig.utils.FileUtils;
import sig.utils.TextUtils;

public class SessionCreateWindow extends JFrame{
	JPanel container = new JPanel();
	LengthValidationField session_name = new LengthValidationField(24);
	NumberValidationField maxplayers = new NumberValidationField();
	FloatValidationField difficulty = new FloatValidationField();
	JPasswordField pass = new JPasswordField();
	JComboBox<String> gametype = new JComboBox<String>();
	NumberValidationField eggcount = new NumberValidationField();
	public List<ItemDisplayBox<MemoryData>> itemHunt = new ArrayList<ItemDisplayBox<MemoryData>>();
	JButton create = new JButton("Create");
	public JPanel itempropertiespanel = new JPanel();
	public int currentHuntBox = 0;
	
	public SessionCreateWindow() {
		this.setTitle("Create Rabi-Race Session");
		this.setIconImage(sigIRC.programIcon);
		this.setVisible(false);
		JPanel namepanel = new JPanel();
		JPanel playerpanel = new JPanel();
		JPanel passwordpanel = new JPanel();
		JPanel difficultypanel = new JPanel();
		JPanel modepanel = new JPanel();
		JPanel eggpropertiespanel = new JPanel();
		
		JPanel[] panel_list = new JPanel[]{
			namepanel,playerpanel,passwordpanel,difficultypanel,modepanel,eggpropertiespanel,itempropertiespanel
		};
		
		for (JPanel panel : panel_list) {
			panel.setLayout(new BoxLayout(panel,BoxLayout.LINE_AXIS));
			panel.setSize(400, 24);
			panel.setMinimumSize(new Dimension(400, 24));
			panel.add(Box.createRigidArea(new Dimension(24,24)));
		}
		
		JLabel nameLabel = new JLabel("Session Name:  ");
		String label = RabiRaceModule.module.myProfile.displayName+"'s Race";
		if (label.length()>session_name.length) {
			label = "My Rabi-Race!";
		}
		session_name.setText(label);
		
		namepanel.add(nameLabel);
		namepanel.add(session_name);

		JLabel playerLabel = new JLabel("Max Players:  ");
		maxplayers.setText("4");
		
		playerpanel.add(playerLabel);
		playerpanel.add(maxplayers);

		JLabel passwordLabel = new JLabel("🔑  Password (Optional):  ");
		
		playerpanel.add(passwordLabel);
		playerpanel.add(pass);
		
		JLabel difficultyLabel = new JLabel("Race Difficulty (0.00~10.00):  ");
		difficulty.setPreferredSize(new Dimension(60,24));
		difficulty.setText("5.00");

		difficultypanel.add(Box.createHorizontalStrut(60));
		difficultypanel.add(difficultyLabel);
		difficultypanel.add(difficulty);
		difficultypanel.add(Box.createHorizontalStrut(60));
		
		JLabel modeLabel = new JLabel("Game Mode:  ");
		gametype.setPreferredSize(new Dimension(120,24));
		gametype.addItem("Egg Mode");
		gametype.addItem("Item Mode");
		
		eggpropertiespanel.setLayout(new BoxLayout(eggpropertiespanel,BoxLayout.LINE_AXIS));
		itempropertiespanel.setVisible(false);
		JLabel eggLabel = new JLabel("Egg Count: ");
		eggcount.setText("5");
		eggcount.setPreferredSize(new Dimension(60,24));
		eggpropertiespanel.add(Box.createHorizontalStrut(64));
		eggpropertiespanel.add(eggLabel);
		eggpropertiespanel.add(eggcount);
		eggpropertiespanel.add(Box.createHorizontalStrut(192));
		eggpropertiespanel.setSize(400, 24);
		eggpropertiespanel.setMinimumSize(new Dimension(400, 24));
		
		gametype.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				switch (((String)gametype.getSelectedItem())) {
					case "Egg Mode":{
						eggpropertiespanel.setVisible(true);
						itempropertiespanel.setVisible(false);
						for (ItemDisplayBox box : itemHunt) {
							box.myPanel.setVisible(false);
							box.set=false;
						}
						RabiRaceModule.module.createwindow.setSize(400, 240);
					}break;
					case "Item Mode":{
						eggpropertiespanel.setVisible(false);
						itempropertiespanel.setVisible(true);
						currentHuntBox = 0;
						for (ItemDisplayBox box : itemHunt) {
							box.setSelectedIndex(0);
						}
						itemHunt.get(0).myPanel.setVisible(true);
					}break;
				}
			}
		});
		
		/*itempropertiespanel.setLayout(new BoxLayout(itempropertiespanel,BoxLayout.LINE_AXIS));
		ItemDisplayBox<MemoryData> itemBox = new ItemDisplayBox<MemoryData>(this);
		itemHunt.add(itemBox);
		JLabel itemLabel = new JLabel("Item 1:  ");

		itempropertiespanel.add(itemLabel);
		itempropertiespanel.add(itemBox);*/
		
		modepanel.add(modeLabel);
		modepanel.add(gametype);
		
		for (JPanel panel : panel_list) {
			panel.add(Box.createRigidArea(new Dimension(24,24)));
		}
		
		create.setSize(164,24);
		create.setMinimumSize(new Dimension(164,24));
		create.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent ev) {
				if (session_name.getText().length()>session_name.length) {
					JOptionPane.showMessageDialog(RabiRaceModule.createwindow, "Your session name is too long!", "Error!", JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (session_name.getText().length()<=2) {
					JOptionPane.showMessageDialog(RabiRaceModule.createwindow, "Your session name is too short!", "Error!", JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (!TextUtils.isAlphanumeric(session_name.getText())) {
					JOptionPane.showMessageDialog(RabiRaceModule.createwindow, "Your session name has invalid characters! Only A-Z,0-9,!,-,.,? and spaces allowed!", "Error!", JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (!TextUtils.isInteger(maxplayers.getText(), 10)) {
					JOptionPane.showMessageDialog(RabiRaceModule.createwindow, "Your max player count is invalid!", "Error!", JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (Integer.parseInt(maxplayers.getText())>48 || Integer.parseInt(maxplayers.getText())<2) {
					JOptionPane.showMessageDialog(RabiRaceModule.createwindow, "Your max player count needs to be between 2-48!", "Error!", JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (((String)gametype.getSelectedItem()).equalsIgnoreCase("Egg Mode") && !TextUtils.isInteger(eggcount.getText(), 10)) {
					JOptionPane.showMessageDialog(RabiRaceModule.createwindow, "Your egg count is invalid!", "Error!", JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (((String)gametype.getSelectedItem()).equalsIgnoreCase("Egg Mode") && (Integer.parseInt(eggcount.getText())>48 || Integer.parseInt(eggcount.getText())<2)) {
					JOptionPane.showMessageDialog(RabiRaceModule.createwindow, "Your egg count needs to be between 2-48!", "Error!", JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (!TextUtils.isNumeric(difficulty.getText()) && difficulty.getText().length()>0) {
					JOptionPane.showMessageDialog(RabiRaceModule.createwindow, "Your difficulty value is invalid! (A number between 0.00 and 10.00)", "Error!", JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (GetModeData()==null) {
					JOptionPane.showMessageDialog(RabiRaceModule.createwindow, "Mode Input is completely wrong! THIS SHOULD NOT BE HAPPENING! Please let the developer know about this.", "Error!", JOptionPane.WARNING_MESSAGE);
					return;
				}
				String hashpass = "";
				if (String.copyValueOf(pass.getPassword()).length()>0) {
					hashpass = SessionListWindow.GetHashedPassword(String.copyValueOf(pass.getPassword()));
				}
				String sessionText = session_name.getText();
				sessionText = sessionText.replaceAll(" ", "%20");
				File file = new File(sigIRC.BASEDIR+"sigIRC/tmp.data");
				try {
					//System.out.println("Write to "+"http://45.33.13.215/rabirace/send.php?key=sessioncreate&name="+sessionText+"&players="+maxplayers.getText()+"&password="+((hashpass.length()>0)?hashpass:"none")+"&difficulty="+((difficulty.getText().length()>0)?difficulty.getText():"-1")+"&mode="+GetModeIndex()+"&extradata="+GetModeData());
					org.apache.commons.io.FileUtils.copyURLToFile(new URL("http://45.33.13.215/rabirace/send.php?key=sessioncreate&name="+sessionText+"&players="+maxplayers.getText()+"&password="+((hashpass.length()>0)?hashpass:"none")+"&difficulty="+((difficulty.getText().length()>0)?difficulty.getText():"-1")+"&mode="+GetModeIndex()+"&extradata="+GetModeData()),file);
					//org.apache.commons.io.FileUtils.copyURLToFile(new URL("http://45.33.13.215/rabirace/send.php?key=sessioncreate&name="+session_name.getText()+"&players="+maxplayers.getText()+"&password="+((hashpass.length()>0)?hashpass:"none")),file);
					String[] contents = FileUtils.readFromFile(sigIRC.BASEDIR+"sigIRC/tmp.data");
					int sessionID=-1;
					if (contents.length>=2) {
						sessionID=Integer.parseInt(contents[0]);
					}
					if (sessionID!=-1) {
						RabiRaceModule.module.getSessionList();
						//RabiRaceModule.module.session_listing.data.put(sessionID, new Session());
						Session session = RabiRaceModule.module.session_listing.data.get(sessionID);
						SessionListWindow.ConnectToSession(session, hashpass);
						setVisible(false);
					}
					//SessionListWindow.ConnectToSession(session, hashedPass);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			private String GetModeIndex() {
				switch (((String)gametype.getSelectedItem())) {
					case "Egg Mode":{
						return "0";
					}
					case "Item Mode":{
						return "1";
					}
					default:{
						return null;
					}
				}
			}

			private String GetModeData() {
				switch (((String)gametype.getSelectedItem())) {
				case "Egg Mode":{
					return eggcount.getText();
				}
				case "Item Mode":{
					StringBuilder sb = new StringBuilder("");
					for (ItemDisplayBox box : itemHunt) {
						if (box.getSelectedIndex()!=0 && box.getSelectedItem() instanceof MemoryData) {
							if (sb.length()!=0) {
								sb.append(";");
							}
							sb.append(((MemoryData)box.getSelectedItem()).name());
						}
					}
					return sb.toString();
				}
				default:{
					JOptionPane.showMessageDialog(RabiRaceModule.createwindow, "Mode Index is completely wrong! THIS SHOULD NOT BE HAPPENING! Please let the developer know about this.", "Error!", JOptionPane.WARNING_MESSAGE);
					return null;
				}
			}
			}
		});
		
		//create.add(Box.createRigidArea(new Dimension(24,24)));

		playerpanel.setSize(200,24);
		passwordpanel.setSize(200,24);
		
		container.setLayout(new BoxLayout(container,BoxLayout.PAGE_AXIS));

		container.add(Box.createRigidArea(new Dimension(24,24)));
		container.add(namepanel);
		container.add(playerpanel);
		container.add(passwordpanel);
		container.add(difficultypanel);
		container.add(modepanel);
		container.add(eggpropertiespanel);
		container.add(itempropertiespanel);
		for (int i=0;i<10;i++) {
			ItemDisplayBox<MemoryData> box = new ItemDisplayBox<MemoryData>(this);
			box.myPanel.setVisible(false);
			container.add(box.myPanel);
			itemHunt.add(box);
		}
		container.add(create);
		container.add(Box.createRigidArea(new Dimension(24,24)));
		
		this.add(container);
		this.setSize(400, 240);
		this.setMinimumSize(new Dimension(400, 240));
		this.setMaximumSize(new Dimension(400, 240));
		this.setResizable(false);
	}
	
	class LengthValidationField extends JTextField implements DocumentListener{
		int length = 20;
		
		public LengthValidationField(int maxLength) {
			this.length = maxLength;
			getDocument().addDocumentListener(this);
		}

		@Override
		public void changedUpdate(DocumentEvent arg0) {
		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			ValidateForm();
		}

		protected void ValidateForm() {
			if (fieldIsInvalid()) {
				setBackground(Color.RED);
			} else {
				setBackground(Color.WHITE);
			}
		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			ValidateForm();
		}

		protected boolean fieldIsInvalid() {
			return getText().length()>length || getText().length()<=2 || !TextUtils.isAlphanumeric(getText());
		}
	}
	
	class NumberValidationField extends JTextField implements DocumentListener{
		
		public NumberValidationField() {
			getDocument().addDocumentListener(this);
		}

		@Override
		public void changedUpdate(DocumentEvent arg0) {
		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			ValidateForm();
		}

		protected void ValidateForm() {
			if (fieldIsInvalid()) {
				setBackground(Color.RED);
			} else {
				setBackground(Color.WHITE);
			}
		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			ValidateForm();
		}

		protected boolean fieldIsInvalid() {
			if (!TextUtils.isInteger(getText(), 10)) {
				return true;
			}
			int val = Integer.parseInt(getText());
			if (val>48 || val<2) {
				return true;
			}
			return false;
		}
	}
	
	class FloatValidationField extends JTextField implements DocumentListener{
		
		public FloatValidationField() {
			getDocument().addDocumentListener(this);
		}

		@Override
		public void changedUpdate(DocumentEvent arg0) {
		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			ValidateForm();
		}

		protected void ValidateForm() {
			if (fieldIsInvalid()) {
				setBackground(Color.RED);
			} else {
				setBackground(Color.WHITE);
			}
		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			ValidateForm();
		}

		protected boolean fieldIsInvalid() {
			if (!TextUtils.isNumeric(getText()) && getText().length()>0) {
				return true;
			}
			if (TextUtils.isNumeric(getText()) && getText().length()>0) {
				float val = Float.parseFloat(getText());
				if (val>10f || val<0f) {
					return true;
				}
			}
			return false;
		}
	}
	
	class ItemDisplayBox<E> extends JComboBox<E>{
		ItemDisplayBox box;
		SessionCreateWindow parent;
		JLabel mylabel = new JLabel("Item "+(itemHunt.size()+1)+":  ");
		JPanel myPanel = new JPanel();
		boolean set=false;
		public ItemDisplayBox(SessionCreateWindow frame) {
			box = this;
			myPanel.setLayout(new BoxLayout(myPanel,BoxLayout.LINE_AXIS));
			myPanel.setSize(400, 24);
			myPanel.setMinimumSize(new Dimension(400, 24));
			myPanel.add(Box.createRigidArea(new Dimension(24,24)));
			myPanel.add(mylabel);
			myPanel.add(this);
			this.parent=frame;
			this.addItem((E)"");
			for (MemoryData md : MemoryData.values()) {
				this.addItem((E)md);
			}
			this.setRenderer(new ItemLabelRenderer());
			this.setMaximumRowCount(6);
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent ev) {
					if (box.getSelectedItem() instanceof MemoryData && !box.set) {
						box.set=true;
						if (currentHuntBox<9) {
							parent.itemHunt.get(++parent.currentHuntBox).myPanel.setVisible(true);
							parent.setSize(parent.getWidth(), parent.getHeight()+24);
						}
					}
				}
			});
		}
	}
	
	class ItemLabelRenderer extends JLabel implements ListCellRenderer{
		
		public ItemLabelRenderer() {
			setOpaque(true);
		}
		
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			if (value instanceof MemoryData) {
				this.setIcon(new ImageIcon(((MemoryData)value).getImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT)));
				setText(((MemoryData)value).getDisplayName());
			} else {
				this.setIcon(new ImageIcon(RabiRaceModule.UNKNOWN_ITEM));
				setText("<None>");
			}
			if (isSelected) {
	            setBackground(list.getSelectionBackground());
	            setForeground(list.getSelectionForeground());
	        } else {
	            setBackground(list.getBackground());
	            setForeground(list.getForeground());
	        }
			setFont(sigIRC.rabiRibiMoneyDisplayFont);
			setIconTextGap(4);
			return this;
		}
	}
}
