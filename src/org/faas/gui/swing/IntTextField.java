package org.faas.gui.swing;

import javax.swing.JTextField;

public class IntTextField extends JTextField {

	public IntTextField() {
		super();
	}
	
	public IntTextField(int size) {
		super(size);
	}
	
	public int getIntValue() {
		try {
			return Integer.parseInt(super.getText().trim());
		} catch (NumberFormatException e) { // CWE-396 Exception -> NumberFormatException
			return 0;
		}

	}
}
