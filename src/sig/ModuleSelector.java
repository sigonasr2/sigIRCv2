package sig;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import sig.modules.BandoriModule;
import sig.modules.ChatLogModule;
import sig.modules.ControllerModule;
import sig.modules.DDRStepModule;
import sig.modules.RabiRaceModule;
import sig.modules.RabiRibiModule;
import sig.modules.TouhouMotherModule;
import sig.modules.TwitchModule;

public class ModuleSelector extends JFrame implements WindowListener{
	public static JCheckBox bandoriBox = new JCheckBox("Bandori",sigIRC.bandorimodule_enabled);
	public static JCheckBox ddrstepBox = new JCheckBox("DDR Step",sigIRC.ddrstepmodule_enabled);
	public static JCheckBox rabiraceBox = new JCheckBox("Rabi Race",sigIRC.rabiracemodule_enabled);
	public static JCheckBox rabiribiBox = new JCheckBox("Rabi Ribi (Broken)",sigIRC.rabiribimodule_enabled);
	public static JCheckBox controllerBox = new JCheckBox("Controller",sigIRC.controllermodule_enabled);
	public static JCheckBox chatlogBox = new JCheckBox("Chat Log",sigIRC.chatlogmodule_enabled);
	public static JCheckBox twitchBox = new JCheckBox("Twitch",sigIRC.twitchmodule_enabled);
	public static JCheckBox touhoumotherBox = new JCheckBox("Touhou Mother",sigIRC.touhoumothermodule_enabled);
		public ModuleSelector() {
			JPanel panel = new JPanel();
			bandoriBox.setActionCommand("bandori");
			bandoriBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ev) {
					if (ev.getActionCommand().equalsIgnoreCase("bandori")) {
						if (ModuleSelector.bandoriBox.isSelected()) {
							BandoriModule.loadModule();
						} else {
							BandoriModule.unloadModule();
						}
					}
				}
			});
			bandoriBox.setVisible(true);
			ddrstepBox.setActionCommand("ddrstep");
			ddrstepBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ev) {
					if (ev.getActionCommand().equalsIgnoreCase("ddrstep")) {
						if (ModuleSelector.ddrstepBox.isSelected()) {
							DDRStepModule.loadModule();
						} else {
							DDRStepModule.unloadModule();
						}
					}
				}
			});
			ddrstepBox.setVisible(true);
			rabiraceBox.setActionCommand("rabirace");
			rabiraceBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ev) {
					if (ev.getActionCommand().equalsIgnoreCase("rabirace")) {
						if (ModuleSelector.rabiraceBox.isSelected()) {
							RabiRaceModule.loadModule();
						} else {
							RabiRaceModule.unloadModule();
						}
					}
				}
			});
			rabiraceBox.setVisible(true);
			rabiribiBox.setActionCommand("rabiribi");
			rabiribiBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ev) {
					if (ev.getActionCommand().equalsIgnoreCase("rabiribi")) {
						if (ModuleSelector.rabiribiBox.isSelected()) {
							RabiRibiModule.loadModule();
						} else {
							RabiRibiModule.unloadModule();
						}
					}
				}
			});
			rabiribiBox.setVisible(true);
			controllerBox.setActionCommand("controller");
			controllerBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ev) {
					if (ev.getActionCommand().equalsIgnoreCase("controller")) {
						if (ModuleSelector.controllerBox.isSelected()) {
							ControllerModule.loadModule();
						} else {
							ControllerModule.unloadModule();
						}
					}
				}
			});
			controllerBox.setVisible(true);
			chatlogBox.setActionCommand("chatlog");
			chatlogBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ev) {
					if (ev.getActionCommand().equalsIgnoreCase("chatlog")) {
						if (ModuleSelector.chatlogBox.isSelected()) {
							ChatLogModule.loadModule();
						} else {
							ChatLogModule.unloadModule();
						}
					}
				}
			});
			chatlogBox.setVisible(true);
			twitchBox.setActionCommand("twitch");
			twitchBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ev) {
					if (ev.getActionCommand().equalsIgnoreCase("twitch")) {
						if (ModuleSelector.twitchBox.isSelected()) {
							TwitchModule.loadModule();
						} else {
							TwitchModule.unloadModule();
						}
					}
				}
			});
			twitchBox.setVisible(true);
			touhoumotherBox.setActionCommand("touhoumother");
			touhoumotherBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ev) {
					if (ev.getActionCommand().equalsIgnoreCase("touhoumother")) {
						if (ModuleSelector.touhoumotherBox.isSelected()) {
							TouhouMotherModule.loadModule();
						} else {
							TouhouMotherModule.unloadModule();
						}
					}
				}
			});
			touhoumotherBox.setVisible(true);
			
			panel.setVisible(true);
			panel.add(twitchBox);
			panel.add(chatlogBox);
			panel.add(controllerBox);
			panel.add(rabiraceBox);
			panel.add(bandoriBox);
			panel.add(touhoumotherBox);
			panel.add(ddrstepBox);
			panel.add(rabiribiBox);

			panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
			
			this.add(panel);
			this.pack();
			this.setVisible(true);
			this.repaint();
		}

		@Override
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosing(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
}
