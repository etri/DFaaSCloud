package org.faas.gui.swing;

import javax.swing.JTextField;

public class LongTextField extends JTextField {

	public LongTextField(int size) {
		super(size);
	}
	
	public long getLongValue() {
		try {
			return Long.parseLong(super.getText().trim());
		} catch (NumberFormatException e) { // CWE-396 Exception -> NumberFormatException
			return 0;
		}

	}
}
