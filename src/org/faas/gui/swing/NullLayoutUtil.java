package org.faas.gui.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class NullLayoutUtil {

	private JPanel panel;
	private int rowHeight;
	
	public NullLayoutUtil(JPanel panel) {
		this(panel,40);
	}
	public NullLayoutUtil(JPanel panel,int rowHeight) {
		this.panel = panel;
		this.rowHeight = rowHeight;
		
		panel.setLayout(null);
	}
	
	private int getY(int i) {
		return i*rowHeight+10;
	}
	
	public void placeName(int row,String name) {
		int y = getY(row);
		JLabel lab =new JLabel(name);
		panel.add(lab);
		
		Insets insets = panel.getInsets();
		Dimension size = lab.getPreferredSize();
		lab.setBounds(10 + insets.left, y + insets.top, size.width, size.height);
	}

	public void placeComponent(int row, Component comp) {
		placeComponent(row,comp,-1);
	}
	
	private int col2StartX = 240;
	public void setCol2StartX(int col2StartX) {
		this.col2StartX = col2StartX;
	}
	public void placeComponent(int row, Component comp,int width) {
		int y = getY(row);
		
		panel.add(comp);
		
		Insets insets = panel.getInsets();
		Dimension size = comp.getPreferredSize();
		if (width != -1)
		{
			size.setSize(width, size.getHeight());
		}

		int diff = 0;
		if ((comp instanceof JLabel) == false) {
			diff = -3;
		}
		comp.setBounds(col2StartX + insets.left, y + insets.top + diff, size.width, size.height);
	}	
	
}
