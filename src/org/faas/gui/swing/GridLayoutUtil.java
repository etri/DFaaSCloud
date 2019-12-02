package org.faas.gui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class GridLayoutUtil {

	private JPanel panel = new JPanel();
	
	public GridLayoutUtil(JPanel panel) {
		panel.setLayout(new BorderLayout());
		panel.add(this.panel, BorderLayout.NORTH);
		
		this.panel.setLayout(new GridLayout(0,2));
	}

	public void placeName(int row,String name) {
		JLabel lab =new JLabel(name);
		panel.add(lab);
	}

	public void placeComponent(int row, Component comp) {
		panel.add(comp);
	}

	public void placeComponent(int row, Component comp, int dummy) {
		panel.add(comp);
	}
	
	public void setCol2StartX(int col2StartX) {
	}


}
