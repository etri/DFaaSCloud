package org.faas.gui.panel;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.faas.topology.Actuator;
import org.faas.topology.Sensor;

public class SensorTableCellRenderer extends DefaultTableCellRenderer {

	static class Data {
		Sensor sensor;
		Actuator paired;
		
		public Data(Sensor sensor,Actuator paired) {
			this.sensor = sensor;
			this.paired = paired;
		}
		
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		SensorTableCellRenderer renderer = (SensorTableCellRenderer)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		// Issue #32
		if (value == null) return renderer;
		
		Data data = (Data)value;
		if (column == 0) {
			Sensor sensor = data.sensor;
			renderer.setText(sensor.getId()+"");
		} else {
			if (data.paired == null) {
				renderer.setText("");
			} else {
				Actuator paired = data.paired;
				if (column == 1) {
					renderer.setText(paired.getEndDeviceGroupId()+"");
				} else {
					renderer.setText(paired.getId()+"");
				}
			}			
			
		}
		return renderer;
	}

}
