package sig.modules.Controller;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import sig.ColorPanel;
import sig.sigIRC;
import sig.modules.ControllerModule;

public class ControlConfigurationWindow extends JFrame implements WindowListener{
	DialogType dialog;
	List<JPanel> panels = new ArrayList<JPanel>();
	List<Component> analog_controller_components = new ArrayList<Component>();
	List<JCheckBox> analog_controller_component_labels = new ArrayList<JCheckBox>();
	ControllerModule module;
	DecimalFormat df = new DecimalFormat("0.000");
	PreviewPanel previewpanel;
	JRadioButton two_axis_button;
	Container twowayAxis_adjustContainer;
	Container twowayAxis_adjustOrientationContainer;
	LinkedTextField twowayAxis_range1,twowayAxis_range2;
	Color axis_background_col = Color.BLACK;
	Color axis_indicator_col = Color.WHITE;
	int axis_width=32,axis_height=32;
	JButton backgroundColor,indicatorColor;
	int orientation=0; //0=Left-to-Right, 1=Right-to-Left, 2=Bottom-to-Top, 3=Top-to-Bottom
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
					twowayAxis_adjustContainer.setVisible(false);
					twowayAxis_adjustOrientationContainer.setVisible(false);
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
					twowayAxis_adjustContainer.setVisible(true);
					twowayAxis_adjustOrientationContainer.setVisible(true);
				}break;
			}
		}
	};
	ActionListener backgroundColorListener = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			Color selectedcol = sigIRC.colorpanel.getBackgroundColor();
			if (selectedcol!=null) {
				axis_background_col = selectedcol;
				backgroundColor.setBackground(axis_background_col);
			}
		}
	};
	ActionListener indicatorColorListener = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			Color selectedcol = sigIRC.colorpanel.getBackgroundColor();
			if (selectedcol!=null) {
				axis_indicator_col = selectedcol;
				indicatorColor.setBackground(axis_indicator_col);
			}
		}
	};
	ActionListener twoWayAxis_OrientationListener = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			orientation=Integer.parseInt(e.getActionCommand());
		}
	};
	ActionListener createbuttonListener = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent ev) {
			if (DataIsValid()) {
				Axis a = ConstructTemporaryAxis();
				module.setTemporaryAxis(a);
				module.setMode(EditMode.DRAGAXISSELECTION);
				//module.setMouseWaitTimer(4);
				module.getConfigurationWindow().dispatchEvent(new WindowEvent(module.getConfigurationWindow(),WindowEvent.WINDOW_CLOSING));
				//module.getConfigurationWindow().setVisible(false);
				//module.getConfigurationWindow().dispose();
			}
		}

		private boolean DataIsValid() {
			return true;
		}
	};
	
	public ControlConfigurationWindow(DialogType type, ControllerModule parent_module) {
		this.setVisible(true);
		this.module = parent_module;
		this.module.setConfigureWindow(this);
		this.dialog = type;
		
		JTextField twowayAxis_range1 = new JTextField("-1.0",2);
		JTextField twowayAxis_range2 = new JTextField("1.0",2);
		this.twowayAxis_range1 = new LinkedTextField(twowayAxis_range1);
		this.twowayAxis_range2 = new LinkedTextField(twowayAxis_range2);
		this.setTitle("Axis Configuration Window");
		try {
			this.setIconImage(ImageIO.read(new File(sigIRC.BASEDIR+"/sigIRC/sigIRCicon.png")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
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
				two_axis_button = new JRadioButton("2-way Axis");
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
				twowayAxis_adjustContainer = new Container();
				JPanel twowayAxis_adjustPanel1 = new JPanel();
				JLabel twowayAxis_fromLabel = new JLabel("From");
				JLabel twowayAxis_toLabel = new JLabel("to");
				JPanel twowayAxis_adjustPanel2 = new JPanel();
				twowayAxis_adjustContainer.setLayout(new BoxLayout(twowayAxis_adjustContainer,BoxLayout.PAGE_AXIS));
				twowayAxis_adjustPanel2.setLayout(new BoxLayout(twowayAxis_adjustPanel2,BoxLayout.LINE_AXIS));
				JLabel twowayAxis_label = new JLabel("Axis Range:",JLabel.LEFT);
				
				twowayAxis_range1.getDocument().addDocumentListener(this.twowayAxis_range1);
				twowayAxis_range2.getDocument().addDocumentListener(this.twowayAxis_range2);
				
				twowayAxis_adjustPanel1.add(twowayAxis_label);
				twowayAxis_adjustPanel2.add(twowayAxis_fromLabel);
				twowayAxis_adjustPanel2.add(twowayAxis_range1);
				twowayAxis_adjustPanel2.add(twowayAxis_toLabel);
				twowayAxis_adjustPanel2.add(twowayAxis_range2);
				
				twowayAxis_adjustContainer.add(twowayAxis_adjustPanel1);
				twowayAxis_adjustContainer.add(twowayAxis_adjustPanel2);
				
				JLabel previewLabel = new JLabel("Axis Preview:  ");
				previewLabel.setVerticalAlignment(JLabel.TOP);
				previewLabel.setHorizontalAlignment(JLabel.RIGHT);
				previewLabelPanel.setPreferredSize(new Dimension(120,24));
				previewpanel = new PreviewPanel();
				previewpanel.setWindow(this);
				previewpanel.setPreferredSize(new Dimension(32,32));
				//Border previewBorder = BorderFactory.createEmptyBorder(axis_width, axis_height, axis_width, axis_height);
				//previewLabelPanel.setBorder(BorderFactory.createTitledBorder("Axis Preview"));
				//previewpanel.add(previewLabel);

				if (!two_axis_button.isSelected()) {
					twowayAxis_adjustContainer.setVisible(false);
				}
				
				Container sizePanel = new Container();
				sizePanel.setLayout(new BoxLayout(sizePanel,BoxLayout.PAGE_AXIS));
				JLabel widthLabel = new JLabel("Width: ");
				JTextField width_field = new JTextField("32",3);
				ResizeTextField width_field_listener = new ResizeTextField(width_field,this,SizeType.WIDTH);
				width_field.getDocument().addDocumentListener(width_field_listener);
				JLabel heightLabel = new JLabel("Height: ");
				JTextField height_field = new JTextField("32",3);
				ResizeTextField height_field_listener = new ResizeTextField(height_field,this,SizeType.HEIGHT);
				height_field.getDocument().addDocumentListener(height_field_listener);
				
				sizePanel.add(widthLabel);
				sizePanel.add(width_field);
				sizePanel.add(Box.createRigidArea(new Dimension(4,0)));
				sizePanel.add(heightLabel);
				sizePanel.add(height_field);
				sizePanel.setPreferredSize(new Dimension(56,96));
				
				ButtonGroup twoWayAxisOrientationGroup = new ButtonGroup();
				JRadioButton twoWayAxis_LeftToRight = new JRadioButton("Left-to-Right",true);
				twoWayAxis_LeftToRight.setActionCommand("0");
				twoWayAxis_LeftToRight.addActionListener(twoWayAxis_OrientationListener);
				JRadioButton twoWayAxis_RightToLeft = new JRadioButton("Right-to-Left");
				twoWayAxis_RightToLeft.setActionCommand("1");
				twoWayAxis_RightToLeft.addActionListener(twoWayAxis_OrientationListener);
				JRadioButton twoWayAxis_BottomToTop = new JRadioButton("Bottom-to-Top");
				twoWayAxis_BottomToTop.setActionCommand("2");
				twoWayAxis_BottomToTop.addActionListener(twoWayAxis_OrientationListener);
				JRadioButton twoWayAxis_TopToBottom = new JRadioButton("Top-to-Bottom");
				twoWayAxis_TopToBottom.setActionCommand("3");
				twoWayAxis_TopToBottom.addActionListener(twoWayAxis_OrientationListener);
				twoWayAxisOrientationGroup.add(twoWayAxis_LeftToRight);
				twoWayAxisOrientationGroup.add(twoWayAxis_RightToLeft);
				twoWayAxisOrientationGroup.add(twoWayAxis_BottomToTop);
				twoWayAxisOrientationGroup.add(twoWayAxis_TopToBottom);
				
				twowayAxis_adjustOrientationContainer = new Container();
				twowayAxis_adjustOrientationContainer.setLayout(new BoxLayout(twowayAxis_adjustOrientationContainer,BoxLayout.LINE_AXIS));
				
				twowayAxis_adjustOrientationContainer.add(twoWayAxis_LeftToRight);
				twowayAxis_adjustOrientationContainer.add(twoWayAxis_RightToLeft);
				twowayAxis_adjustOrientationContainer.add(twoWayAxis_BottomToTop);
				twowayAxis_adjustOrientationContainer.add(twoWayAxis_TopToBottom);

				if (!two_axis_button.isSelected()) {
					twowayAxis_adjustOrientationContainer.setVisible(false);
				}
				
				Container colorPickerContainer = new Container();
				colorPickerContainer.setLayout(new BoxLayout(colorPickerContainer,BoxLayout.LINE_AXIS));
				colorPickerContainer.setPreferredSize(new Dimension(640,64));
				backgroundColor = new JButton("");
				indicatorColor = new JButton("");
				backgroundColor.setBackground(Color.BLACK);
				backgroundColor.setPreferredSize(new Dimension(32,32));
				backgroundColor.addActionListener(backgroundColorListener);
				indicatorColor.setBackground(Color.WHITE);
				indicatorColor.setPreferredSize(new Dimension(32,32));
				indicatorColor.addActionListener(indicatorColorListener);
				
				JPanel backgroundColorPanel = new JPanel();
				backgroundColorPanel.setPreferredSize(new Dimension(32,32));
				backgroundColorPanel.setBorder(BorderFactory.createTitledBorder("Background Color"));
				JPanel indicatorColorPanel = new JPanel();
				indicatorColorPanel.setPreferredSize(new Dimension(32,32));
				indicatorColorPanel.setBorder(BorderFactory.createTitledBorder("Indicator Color"));
				backgroundColorPanel.add(backgroundColor);
				indicatorColorPanel.add(indicatorColor);
				
				colorPickerContainer.add(Box.createHorizontalGlue());
				colorPickerContainer.add(backgroundColorPanel);
				colorPickerContainer.add(indicatorColorPanel);
				colorPickerContainer.add(Box.createHorizontalGlue());
				
				JPanel submitPanel = new JPanel();
				submitPanel.setPreferredSize(new Dimension(640,32));
				JButton createButton = new JButton("Create Axis");
				submitPanel.add(createButton);
				createButton.addActionListener(createbuttonListener);
				createButton.setMaximumSize(new Dimension(64,24));
				
				previewLabelPanel.add(twowayAxis_adjustContainer);
				previewLabelPanel.add(Box.createRigidArea(new Dimension(10,1)));
				previewLabelPanel.add(previewLabel);
				previewLabelPanel.add(previewpanel);
				previewLabelPanel.add(Box.createHorizontalBox());
				previewLabelPanel.add(sizePanel);
				previewLabelPanel.add(twowayAxis_adjustOrientationContainer);
				previewLabelPanel.add(colorPickerContainer);
				previewLabelPanel.add(submitPanel);
				//previewLabelPanel.setBackground(Color.BLUE);
				
				
				container.add(previewLabelPanel);
				container.setLayout(new BoxLayout(container,BoxLayout.PAGE_AXIS));
				
				this.setMinimumSize(new Dimension(640,548));
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

	protected Axis ConstructTemporaryAxis() {
		Axis a;
		if (two_axis_button.isSelected()) {
			Identifier ident=null;
			for (int i=0;i<analog_controller_component_labels.size();i++) {
				if (analog_controller_component_labels.get(i).isSelected()) {
					ident=analog_controller_components.get(i).getIdentifier();
					break;
				}
			}
			a = new Axis(new Rectangle2D.Double(),
					module.getControllers().get(0),
					ident,
					Double.parseDouble(twowayAxis_range1.getTextField().getText()),
					Double.parseDouble(twowayAxis_range2.getTextField().getText()),
					orientation,
					axis_background_col,
					axis_indicator_col,
					module);
		} else {
			List<Identifier> ident=new ArrayList<Identifier>();
			ident.add(null);
			ident.add(null);
			int count=0;
			for (int i=0;i<analog_controller_component_labels.size();i++) {
				if (analog_controller_component_labels.get(i).isSelected()) {
					ident.set(count++,analog_controller_components.get(i).getIdentifier());
				}
			}
			a = new Axis(new Rectangle2D.Double(),
					module.getControllers().get(0),
					ident.get(0),
					ident.get(1),
					axis_background_col,
					axis_indicator_col,
					module);
		}
		return a;
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
        Axis.GetAxisDisplay(g,window.ConstructTemporaryAxis(),0,0,window.axis_width,window.axis_height);
    }
}