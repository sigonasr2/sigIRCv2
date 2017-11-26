package sig.modules.RabiRace;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import sig.sigIRC;
import sig.modules.RabiRaceModule;
import sig.utils.FileUtils;
import sig.utils.TextUtils;

public class SessionCreateWindow extends JFrame{
	JPanel container = new JPanel();
	LengthValidationField session_name = new LengthValidationField(16);
	NumberValidationField maxplayers = new NumberValidationField();
	FloatValidationField difficulty = new FloatValidationField();
	JPasswordField pass = new JPasswordField();
	JComboBox gametype = new JComboBox();
	JButton create = new JButton("Create");
	
	public SessionCreateWindow() {
		this.setTitle("Create Rabi-Race Session");
		this.setIconImage(sigIRC.programIcon);
		this.setVisible(false);
		JPanel namepanel = new JPanel();
		JPanel playerpanel = new JPanel();
		JPanel passwordpanel = new JPanel();
		JPanel difficultypanel = new JPanel();
		
		JPanel[] panel_list = new JPanel[]{
			namepanel,playerpanel,passwordpanel,difficultypanel
		};
		
		for (JPanel panel : panel_list) {
			panel.setLayout(new BoxLayout(panel,BoxLayout.LINE_AXIS));
			panel.setSize(400, 24);
			panel.setMinimumSize(new Dimension(400, 24));
			panel.add(Box.createRigidArea(new Dimension(24,24)));
		}
		
		JLabel nameLabel = new JLabel("Session Name:  ");
		String label = RabiRaceModule.module.myProfile.displayName+"'s Race";
		if (label.length()>16) {
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

		difficultypanel.add(difficultyLabel);
		difficultypanel.add(difficulty);
		
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
				if (!TextUtils.isNumeric(difficulty.getText()) && difficulty.getText().length()>0) {
					JOptionPane.showMessageDialog(RabiRaceModule.createwindow, "Your difficulty value is invalid! (A number between 0.00 and 10.00)", "Error!", JOptionPane.WARNING_MESSAGE);
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
					org.apache.commons.io.FileUtils.copyURLToFile(new URL("http://45.33.13.215/rabirace/send.php?key=sessioncreate&name="+sessionText+"&players="+maxplayers.getText()+"&password="+((hashpass.length()>0)?hashpass:"none")+"&difficulty="+((difficulty.getText().length()>0)?difficulty.getText():"-1")),file);
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
		});
		
		//create.add(Box.createRigidArea(new Dimension(24,24)));
		
		container.setLayout(new BoxLayout(container,BoxLayout.PAGE_AXIS));

		container.add(Box.createRigidArea(new Dimension(24,24)));
		container.add(namepanel);
		container.add(playerpanel);
		container.add(passwordpanel);
		container.add(difficultypanel);
		container.add(create);
		container.add(Box.createRigidArea(new Dimension(24,24)));
		
		this.add(container);
		this.setSize(400, 192);
		this.setMinimumSize(new Dimension(400, 192));
		this.setMaximumSize(new Dimension(400, 192));
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
}
