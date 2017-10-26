package sig.modules.Controller;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

public class ResizeTextField extends LinkedTextField{
	ControlConfigurationWindow window;
	SizeType type;

	public ResizeTextField(JTextField field,ControlConfigurationWindow window,SizeType type) {
		super(field);
		this.window=window;
		this.type=type;
	}


	@Override
	public void insertUpdate(DocumentEvent e) {
		ValidateFormAndResizeComponent();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		ValidateFormAndResizeComponent();
	}


	private void ValidateFormAndResizeComponent() {
		ValidateForm();
		ResizeComponent();
	}


	private void ResizeComponent() {
		if (!fieldIsInvalid()) {
			switch (type) {
			case HEIGHT:
				window.axis_height=Integer.parseInt(field.getText());
				break;
			case WIDTH:
				window.axis_width=Integer.parseInt(field.getText());
				break;
			}
			window.previewpanel.setPreferredSize(new Dimension(window.axis_width,window.axis_height));
		}
	}
	
}
