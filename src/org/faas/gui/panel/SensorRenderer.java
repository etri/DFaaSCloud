package org.faas.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import org.faas.topology.Actuator;
import org.faas.topology.Sensor;

public class SensorRenderer extends JPanel implements ListCellRenderer<Sensor> {

	protected JLabel labId = new JLabel();
	//protected JComboBox jcbPairedId = new JComboBox();
	protected JLabel labPairedId = new JLabel();
	protected JLabel labProfileName = new JLabel();
	
	protected Border selectBorder;
	protected Border unselectBorder;
	
	private Color emptyColor = new Color(0,0,0,0);
	
	public SensorRenderer() {
		//super.setLayout(new BorderLayout());
		
		add(new JLabel("EventSource Id:"));
		add(labId);

		add(new JLabel("  "));
		
//		panel.add(new JLabel("Function Profile Name"));
//		panel.add(labProfileName);
		add(new JLabel("Paired EventSink Id:"));
		add(labPairedId);
		
		selectBorder = BorderFactory.createLineBorder(Color.blue, 2, true);
		unselectBorder = BorderFactory.createLineBorder(emptyColor, 2, true);

	}
	
	@Override
    public Component getListCellRendererComponent(JList<? extends Sensor> list, Sensor sensor, int index,
        boolean isSelected, boolean cellHasFocus) {
        
		labId.setText(sensor.getId()+"");
		//labProfileName.setText(actuator.);
		labPairedId.setText(sensor.getActuatorId()+"");
		if (isSelected) {
			this.setBorder(selectBorder);
		} else {
			this.setBorder(unselectBorder);
		}
		
        return this;
    }
}
