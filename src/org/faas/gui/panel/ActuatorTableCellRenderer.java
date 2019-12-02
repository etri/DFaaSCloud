package org.faas.gui.panel;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.faas.topology.Actuator;
import org.faas.topology.Sensor;

public class ActuatorTableCellRenderer extends DefaultTableCellRenderer {

	static class Data {
		Actuator actuator;
		Sensor paired;
		
		public Data(Actuator actuator,Sensor paired) {
			this.actuator = actuator;
			this.paired = paired;
		}
		
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		ActuatorTableCellRenderer renderer = (ActuatorTableCellRenderer)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		// Issue #32
		if (value == null) return renderer;

		Data data = (Data)value;

		if (column == 0) {
			Actuator actuator = data.actuator;
			renderer.setText(actuator.getId()+"");
		} else {
			if (data.paired == null) {
				renderer.setText("");
			} else {
				Sensor paired = data.paired;
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
