package sig.modules.Controller;

import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import sig.utils.TextUtils;

public class LinkedTextField implements DocumentListener{
	JTextField field;
	public LinkedTextField(JTextField field) {
		this.field=field;
	}
	
	public JTextField getTextField() {
		return field;
	}
	
	public DocumentListener getListener() {
		return this;
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
	}

	protected boolean fieldIsInvalid() {
		return !TextUtils.isNumeric(field.getText());
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		ValidateForm();
	}

	protected void ValidateForm() {
		if (fieldIsInvalid()) {
			field.setBackground(Color.RED);
		} else {
			field.setBackground(Color.WHITE);
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		ValidateForm();
	}
}
