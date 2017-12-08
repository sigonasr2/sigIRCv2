package sig.windows;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import sig.Module;

@SuppressWarnings("serial")
class ModuleButton extends JToggleButton{
	String label = "";
	ModuleButton button;
	Module myModule;
	public ModuleButton(String label, Module module) {
		this.label=label;
		this.button=this;
		this.myModule=module;
		this.setBackground(Color.DARK_GRAY);
		this.setText(label);
		this.setToolTipText("Click to enable and disable the \n"+label+" module.");
		this.setPreferredSize(new Dimension(160,56));
		this.setForeground(Color.GRAY);
		this.setIconTextGap(4);
		this.setSelectedIcon(ProgramWindow.selected_icon);
		this.setIcon(ProgramWindow.deselected_icon);
		this.setSelected(true);
		button.setForeground(Color.BLUE);
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
				myModule.setVisible(button.isSelected());
			}
			
		});
	}
}