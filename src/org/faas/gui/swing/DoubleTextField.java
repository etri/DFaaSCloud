package org.faas.gui.swing;

import javax.swing.JTextField;

public class DoubleTextField extends JTextField {

	public DoubleTextField() {
		super();
	}
	
	public DoubleTextField(int size) {
		super(size);
	}
	
	public double getDoubleValue() {
		
		try {
			return Double.parseDouble(super.getText().trim());
		} catch (NumberFormatException e) { // CWE-396 Exception -> NumberFormatException
			return 0;
		}
	}
}
