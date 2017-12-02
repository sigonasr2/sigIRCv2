package sig.windows;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;

import sig.sigIRC;
import sig.modules.RabiRaceModule;
import sig.utils.FileUtils;

public class IntroDialog extends JFrame{
	Font systemFont;
	final static Color panelBackgroundColor = new Color(192,192,192);
	JPanel displayPanel;
	JPanel borderPanel;
	JPanel buttonPanel;
	JButton nextbutton;
	JButton exitbutton;
	JButton backbutton;
	JEditorPane introText;
	TitledBorder border;
	JTextField nameBox,oauthTokenBox;
	JPanel namePanel;
	JLabel nameLabel;
	JLabel oauthLabel;
	JScrollPane scrollDisplayPanel;
	JButton openWebpage;
	ModuleButton[] moduleButtons; 
	JComboBox<String> updateType;
	
	static Icon deselected_icon,selected_icon;
	
	public IntroDialog() {
		this.setLocationByPlatform(true);
		this.setVisible(true);
		this.setTitle("sigIRCv2 v"+sigIRC.VERSION);
		
		InputStream stream = sigIRC.class.getResourceAsStream("/resource/CP_Font.ttf");
		//File font = new File(sigIRC.BASEDIR+"sigIRC/CP_Font.ttf");
		
		try {
			systemFont = Font.createFont(Font.TRUETYPE_FONT,stream);
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(systemFont);
			systemFont = new Font("CP Font",0,16);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		
		borderPanel = new JPanel();
		displayPanel = new JPanel();
		buttonPanel = new JPanel();
		namePanel = new JPanel();
		namePanel.setBackground(panelBackgroundColor);
		namePanel.setPreferredSize(new Dimension(560,320));
		nameBox = new JTextField(30);
		oauthTokenBox = new JTextField(30);
		//dialogPanel.setSize(this.getSize()); 
		border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true),"Welcome to sigIRC!",TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION,systemFont);
		borderPanel.setBorder(border);
		
		this.add(borderPanel);
		
		try {
			this.setIconImage(ImageIO.read(sigIRC.class.getResource("/resource/sigIRCicon.png")));
			deselected_icon = new ImageIcon(ImageIO.read(sigIRC.class.getResource("/resource/deselected_button.png")));
			selected_icon = new ImageIcon(ImageIO.read(sigIRC.class.getResource("/resource/selected_button.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		moduleButtons = new ModuleButton[]{
				new ModuleButton("Scrolling Chat"),
				new ModuleButton("Chat Log"),
				new ModuleButton("Controller"),
				new ModuleButton("Twitch"),
				new ModuleButton("Rabi-Race"),
				new ModuleButton("Touhou Mother"),
		};
		
		moduleButtons[0].setSelected(true);
		moduleButtons[1].setSelected(true);
		moduleButtons[3].setSelected(true);
		
		introText = new JEditorPane();
		try {
			introText.setPage(sigIRC.class.getResource("/resource/text/introText.html"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		introText.setPreferredSize(new Dimension(560, 320));
		introText.setEditable(false);
		introText.setBackground(panelBackgroundColor);
		
		
		backbutton = new JButton("< Back");
		backbutton.setPreferredSize(new Dimension(128,24));
		backbutton.setActionCommand("2");
		backbutton.setEnabled(false);
		backbutton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent ev) {
				setupBackPage(ev.getActionCommand());
			}
			
		});
		
		nextbutton = new JButton("Next >");
		nextbutton.setPreferredSize(new Dimension(128,24));
		nextbutton.setActionCommand("2");
		nextbutton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent ev) {
				setupPage(ev.getActionCommand());
			}
			
		});
		exitbutton = new JButton("Exit");
		exitbutton.setPreferredSize(new Dimension(128,24));

		displayPanel.setPreferredSize(new Dimension(680,360));
		
		scrollDisplayPanel = new JScrollPane();
		scrollDisplayPanel.setViewportView(introText);
		scrollDisplayPanel.setPreferredSize(introText.getPreferredSize());
		scrollDisplayPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollDisplayPanel.setBorder(null);
		displayPanel.add(scrollDisplayPanel);
		displayPanel.add(namePanel);
		displayPanel.setBackground(panelBackgroundColor);
		
		buttonPanel.setPreferredSize(new Dimension(680,48));
		
		buttonPanel.add(Box.createHorizontalStrut(128));
		buttonPanel.add(backbutton);
		buttonPanel.add(Box.createHorizontalStrut(12));
		buttonPanel.add(nextbutton);
		buttonPanel.add(Box.createHorizontalStrut(80));
		buttonPanel.add(exitbutton);
		buttonPanel.setBackground(panelBackgroundColor);

		borderPanel.add(displayPanel);
		borderPanel.add(buttonPanel);
		
		borderPanel.setBackground(panelBackgroundColor);
		
		this.setSize(720, 480);
		this.setBackground(panelBackgroundColor);
		this.setResizable(false);
	}

	protected void setupBackPage(String page) {
		switch (page) {
			case "2":{
				setupPage("1");
			}break;
			case "3":{
				setupPage("2");
			}break;
			case "4":{
				setupPage("3");
			}break;
		}
	}

	protected void setupPage(String page) {
		switch (page) {
			case "1":{
				scrollDisplayPanel.setPreferredSize(new Dimension(560, 320));
				try {
					introText.setPage(sigIRC.class.getResource("/resource/text/introText.html"));
				} catch (IOException e) {
					e.printStackTrace();
				}	
				displayPanel.validate();
				backbutton.setEnabled(false);
				nextbutton.setActionCommand("2");
				namePanel.removeAll();
				namePanel.validate();
				border.setTitle("Welcome to sigIRC!");
				borderPanel.repaint();
			}break;
			case "2":{
				border.setTitle("Setup Twitch Account");
				borderPanel.repaint();
				try {
					introText.setPage(sigIRC.class.getResource("/resource/text/setupTwitchName.html"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				scrollDisplayPanel.setPreferredSize(new Dimension(560,180));
				nameLabel = new JLabel("Twitch Username: ");
				namePanel.removeAll();
				namePanel.add(nameLabel);
				namePanel.add(nameBox);
				namePanel.setPreferredSize(new Dimension(560,320));
				namePanel.setBackground(panelBackgroundColor);
				nextbutton.setActionCommand("3");
				backbutton.setActionCommand("2");
				namePanel.validate();
				displayPanel.validate();
				displayPanel.repaint();
				backbutton.setEnabled(true);
			}break;
			case "3":{
				border.setTitle("Setup Twitch Account");
				borderPanel.repaint();
				if (nameBox.getText().length()==0) {
					JOptionPane.showMessageDialog(this, "You must enter a Twitch Username!", "Error!", JOptionPane.WARNING_MESSAGE);
					break;
				}
				try {
					introText.setPage(sigIRC.class.getResource("/resource/text/setupoauthToken.html"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				openWebpage = new JButton("Get oauthToken");
				openWebpage.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							Desktop.getDesktop().browse(new URI("https://twitchapps.com/tmi/"));
						} catch (IOException | URISyntaxException e1) {
							e1.printStackTrace();
						}
					}

				});
				scrollDisplayPanel.setPreferredSize(new Dimension(560,180));
				nextbutton.setActionCommand("4");
				backbutton.setActionCommand("3");
				try {
					introText.setPage(sigIRC.class.getResource("/resource/text/setupoauthToken.html"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				oauthLabel = new JLabel("Twitch oauth Token:");
				namePanel.removeAll();
				namePanel.add(openWebpage);
				namePanel.add(Box.createRigidArea(new Dimension(640,32)));
				namePanel.add(oauthLabel);
				namePanel.add(oauthTokenBox);
				namePanel.validate();
			}break;
			case "4":{
				oauthTokenBox.setText(oauthTokenBox.getText().trim());
				if (oauthTokenBox.getText().length()!=36) {
					JOptionPane.showMessageDialog(this, "An oauth Token is 36 characters long. Please verify you have copied and pasted it correctly! (Include the oauth: part)", "Error!", JOptionPane.WARNING_MESSAGE);
					break;
				}
				border.setTitle("Connecting to Twitch...");
				borderPanel.repaint();
				this.repaint();
				if (!AttemptConnection()) {
					JOptionPane.showMessageDialog(this, "Failed to authenticate with Twitch! Please verify your username, oauth Token, and Internet Connection and then try again.", "Error!", JOptionPane.WARNING_MESSAGE);
					border.setTitle("Setup Twitch Account");
					borderPanel.repaint();
					break;
				}
				border.setTitle("Successfully Authenticated!");
				borderPanel.repaint();
				try {
					introText.setPage(sigIRC.class.getResource("/resource/text/setupProgramSettings.html"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				scrollDisplayPanel.setPreferredSize(new Dimension(560,200));
				namePanel.removeAll();
				JLabel label = new JLabel("Program Updating Behavior: ");
				JLabel modulelabel = new JLabel("Select all Modules you want enabled:");
				updateType = new JComboBox<String>();
				updateType.addItem("Automatically Update");
				updateType.addItem("Notify when Update Available");
				updateType.addItem("Never Update");
				namePanel.add(label);
				namePanel.add(updateType);
				namePanel.add(Box.createRigidArea(new Dimension(520,4)));
				namePanel.add(modulelabel);
				namePanel.add(Box.createRigidArea(new Dimension(520,4)));
				for (ModuleButton button : moduleButtons) {
					namePanel.add(button);
				}
				nextbutton.setActionCommand("5");
				backbutton.setActionCommand("4");
				//namePanel.setMinimumSize(new Dimension(560,64));
				namePanel.validate();
				displayPanel.validate();
				displayPanel.repaint();
				sigIRC.config.setProperty("nickname", nameBox.getText());
				sigIRC.config.setProperty("channel", "#"+nameBox.getText());
				sigIRC.config.saveProperties();
				FileUtils.writetoFile(new String[]{oauthTokenBox.getText()}, sigIRC.BASEDIR+"sigIRC/oauthToken.txt");
			}break;
			case "5":{
				sigIRC.disableChatMessages = !moduleButtons[0].isSelected();
				sigIRC.chatlogmodule_enabled = moduleButtons[1].isSelected();
				sigIRC.controllermodule_enabled = moduleButtons[2].isSelected();
				sigIRC.twitchmodule_enabled = moduleButtons[3].isSelected();
				sigIRC.rabiracemodule_enabled = moduleButtons[4].isSelected();
				sigIRC.touhoumothermodule_enabled = moduleButtons[5].isSelected();
				sigIRC.config.setBoolean("Disable_Chat_Messages", sigIRC.disableChatMessages);
				sigIRC.config.setBoolean("Module_chatlog_Enabled", sigIRC.chatlogmodule_enabled);
				sigIRC.config.setBoolean("Module_controller_Enabled", sigIRC.controllermodule_enabled);
				sigIRC.config.setBoolean("Module_twitch_Enabled", sigIRC.twitchmodule_enabled);
				sigIRC.config.setBoolean("Module_rabirace_Enabled", sigIRC.rabiracemodule_enabled);
				sigIRC.config.setBoolean("Module_touhoumother_Enabled", sigIRC.touhoumothermodule_enabled);
				switch ((String)updateType.getSelectedItem()) {
					case "Automatically Update":{
						sigIRC.autoUpdateProgram = 0;
					}break;
					case "Notify when Update Available":{
						sigIRC.autoUpdateProgram = 1;
					}break;
					case "Never Update":{
						sigIRC.autoUpdateProgram = 2;
					}break;
				}
				sigIRC.config.setInteger("Auto_Update_Program", sigIRC.autoUpdateProgram);
				sigIRC.config.saveProperties();
				this.invalidate();
			}break;
		}
	}
	
	protected boolean AttemptConnection() {
		Socket socket;
		try {
			socket = new Socket(sigIRC.server, 6667);
	        BufferedWriter writer = new BufferedWriter(
	                new OutputStreamWriter(socket.getOutputStream( )));
	        BufferedReader reader = new BufferedReader(
	                new InputStreamReader(socket.getInputStream( )));
	        
	        // Log on to the server.
	        writer.write("PASS " + oauthTokenBox.getText() + "\r\n");
	        writer.write("NICK " + nameBox.getText() + "\r\n");
	        writer.flush( );
	        return sigIRC.VerifyLogin(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}

class ModuleButton extends JToggleButton{
	String label = "";
	ModuleButton button;
	public ModuleButton(String label) {
		this.label = label;
		this.button = this;
		this.setBackground(Color.DARK_GRAY);
		button.setForeground(Color.GRAY);
		button.setIconTextGap(4);
		button.setIcon(IntroDialog.deselected_icon);
		button.setSelectedIcon(IntroDialog.selected_icon);
		this.setText(label);
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
