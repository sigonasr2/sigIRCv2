package sig.modules.Controller;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import sig.modules.ControllerModule;

public class ControlConfigurationWindow extends JFrame implements WindowListener{
	DialogType dialog;
	List<JPanel> panels = new ArrayList<JPanel>();
	List<Component> analog_controller_components = new ArrayList<Component>();
	List<JCheckBox> analog_controller_component_labels = new ArrayList<JCheckBox>();
	ControllerModule module;
	DecimalFormat df = new DecimalFormat("0.000");
	PreviewPanel previewpanel;
	ActionListener checkboxListener = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent ev) {
			int checkedamt=0;
			for (JCheckBox cb : analog_controller_component_labels) {
				checkedamt+=(cb.isSelected())?1:0;
			}
			if (previewpanel.twoAxis && checkedamt>1) {
				UncheckPreviouslyCheckedbox(ev);
			} else
			if (!previewpanel.twoAxis && checkedamt>2) {
				UncheckPreviouslyCheckedbox(ev);
			}
		}

		private void UncheckPreviouslyCheckedbox(ActionEvent ev) {
			for (int i=0;i<analog_controller_components.size();i++) {
				if (analog_controller_components.get(i).getName().equals(ev.getActionCommand())) {
					analog_controller_component_labels.get(i).setSelected(false);
				}
			}
		}
	};
	ActionListener axisListener = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent ev) {
			switch (ev.getActionCommand()) {
				case "four":{
					previewpanel.setAxis(false);
				}break;
				case "two":{
					previewpanel.setAxis(true);
					boolean firstBox=false;
					for (int i=0;i<analog_controller_components.size();i++) {
						if (analog_controller_component_labels.get(i).isSelected()) {
							if (!firstBox) {
								firstBox=true;
							} else {
								analog_controller_component_labels.get(i).setSelected(false);
							}
						}
					}
				}break;
			}
		}
	};
	
	public ControlConfigurationWindow(DialogType type, ControllerModule parent_module) {
		this.setVisible(true);
		this.module = parent_module;
		this.module.setConfigureWindow(this);
		this.dialog = type;
		switch (dialog) {
			case AXIS_OPTIONS:
				
				break;
			case BUTTON_AXIS_SELECTION:
				JPanel container = new JPanel();
				JPanel axisPanel = new JPanel();	
				ButtonGroup axisSelection = new ButtonGroup();
				axisPanel.setLayout(new BoxLayout(axisPanel,BoxLayout.LINE_AXIS));
				JPanel selectionFrame = new JPanel();
				selectionFrame.setLayout(new BoxLayout(selectionFrame,BoxLayout.LINE_AXIS));
				JPanel selectionPanel1 = new JPanel(){
				    public void paintComponent(Graphics g) {
				        super.paintComponent(g);
						try {
							g.drawImage(ImageIO.read(new File(ControllerModule.CONTROLLERPATH+"4-way_axis.png")), 0, 0, this);
						} catch (IOException e) {
							e.printStackTrace();
						}
				    }
				};
				JPanel selectionPanel2 = new JPanel(){
				    public void paintComponent(Graphics g) {
				        super.paintComponent(g);
						try {
							g.drawImage(ImageIO.read(new File(ControllerModule.CONTROLLERPATH+"2-way_axis.png")), 0, 0, this);
						} catch (IOException e) {
							e.printStackTrace();
						}
				    }
				};
				//selectionPanel.setLayout(new BoxLayout(selectionPanel,BoxLayout.LINE_AXIS));
				selectionFrame.add(selectionPanel1);
				JRadioButton four_axis_button = new JRadioButton("4-way Axis",true);
				four_axis_button.setActionCommand("four");
				four_axis_button.addActionListener(axisListener);
				selectionFrame.add(selectionPanel2);
				JRadioButton two_axis_button = new JRadioButton("2-way Axis");
				two_axis_button.setActionCommand("two");
				two_axis_button.addActionListener(axisListener);
				axisSelection.add(four_axis_button);
				axisSelection.add(two_axis_button);
				selectionPanel1.add(four_axis_button);
				selectionPanel2.add(two_axis_button);
				selectionPanel1.setBackground(new Color(0,0,255,96));
				selectionPanel2.setBackground(new Color(0,0,255,96));
				int counter=0;
				for (Controller c : module.getControllers()) {
					for (Component cp : c.getComponents()) {
						if (cp.isAnalog()) {
							analog_controller_components.add(cp);
							JCheckBox component_checkbox = new JCheckBox(GetComponentValue(cp),false);
							component_checkbox.setActionCommand(cp.getName());
							component_checkbox.addActionListener(checkboxListener);
							analog_controller_component_labels.add(component_checkbox);
							axisPanel.add(component_checkbox);
							counter=Math.floorMod(counter+1, 5);
							if (counter==0) {
								panels.add(axisPanel);
								axisPanel = new JPanel();
								axisPanel.setLayout(new BoxLayout(axisPanel,BoxLayout.LINE_AXIS));
							}
						}
					}
				}
				for (JPanel panel : panels) {
					container.add(panel);
				}
				container.add(axisPanel);
				container.add(selectionFrame);
				
				JComponent previewLabelPanel = new JPanel();
				JLabel previewLabel = new JLabel("Axis Preview:  ");
				previewLabel.setVerticalAlignment(JLabel.TOP);
				previewLabel.setHorizontalAlignment(JLabel.RIGHT);
				previewLabelPanel.setPreferredSize(new Dimension(120,24));
				previewpanel = new PreviewPanel();
				previewpanel.setWindow(this);
				previewpanel.setPreferredSize(new Dimension(240,32));
				//previewpanel.add(previewLabel);

				
				previewLabelPanel.add(previewLabel,BorderLayout.NORTH);
				previewLabelPanel.add(previewpanel,BorderLayout.CENTER);
				//previewLabelPanel.setBackground(Color.BLUE);
				
				
				container.add(previewLabelPanel);
				container.setLayout(new BoxLayout(container,BoxLayout.PAGE_AXIS));
				
				this.setMinimumSize(new Dimension(640,480));
				this.add(container);
				//this.pack();
				this.repaint();
				break;
			case BUTTON_OPTIONS:
				
				break;
		}
	}

	private String GetComponentValue(Component component) {
		return component.getName()+": "+df.format(component.getPollData())+"     ";
	}
	
	public void run() {
		switch (dialog) {
			case AXIS_OPTIONS:
				break;
			case BUTTON_AXIS_SELECTION:
				for (int i=0;i<analog_controller_components.size();i++) {
					JCheckBox mycheckbox = analog_controller_component_labels.get(i);
					mycheckbox.setText(GetComponentValue(analog_controller_components.get(i)));
				}
				break;
			case BUTTON_OPTIONS:
				break;
		}
		if (previewpanel!=null) {
			previewpanel.repaint();
		}
	}
	
	@Override
	public void windowClosing(WindowEvent ev) {
		this.module.setConfigureWindow(null);
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}
}

class PreviewPanel extends JPanel{
	public boolean twoAxis=false;
	ControlConfigurationWindow window;
	public void setWindow(ControlConfigurationWindow window) {
		this.window=window;
	}
	public void setAxis(boolean twoAxis) {
		this.twoAxis=twoAxis;
	}
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (twoAxis) {
        	g.fillRect(0, 0, 32, 32);
        } else {
        	double xval=0;
        	double yval=0;
        	for (int i=0;i<window.analog_controller_component_labels.size();i++) {
        		if (window.analog_controller_component_labels.get(i).isSelected() &&
        				window.analog_controller_component_labels.get(i).getText().contains("X")) {
        			xval=window.analog_controller_components.get(i).getPollData();
        		} else
        		if (window.analog_controller_component_labels.get(i).isSelected() &&
        				window.analog_controller_component_labels.get(i).getText().contains("Y")) {
        			yval=window.analog_controller_components.get(i).getPollData();
        		}
        	}
        	Color color_identity = g.getColor();
        	g.setColor(Color.BLACK);
    		g.fillOval(0, 0, 32, 32);
        	g.setColor(Color.WHITE);
        	g.drawOval((int)((xval+1)*12), (int)((yval+1)*12), 8, 8);
        	g.setColor(color_identity);
        }
    }
}