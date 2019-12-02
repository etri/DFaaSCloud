package org.faas.gui.panel;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.faas.DFaaSConstants;
import org.faas.gui.DFaaSGui;
import org.faas.gui.core.SpringUtilities;
import org.faas.gui.swing.ComboElement;
import org.faas.topology.Actuator;
import org.faas.topology.FunctionProfile;
import org.faas.topology.FunctionProfileList;
import org.faas.topology.NetworkTopology;
import org.faas.topology.NetworkTopologyHelper;
import org.faas.topology.Sensor;
import org.faas.utils.Util;

public class SensorDetailPanel extends JPanel {

	private JLabel labSensorId;
	private JComboBox jcbFunctionProfile;
	private JLabel    labPairedActuator;
	private JComboBox jcbPaired;

	private JButton butSave;
	
	private Sensor sensor;
	
	private EndElementListener listener;
	
	public SensorDetailPanel(EndElementListener listener) {
		this.listener = listener;
		
		setLayout(new BorderLayout());

		add(createInputPanelArea(), BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);

	}
	
	private JPanel createInputPanelArea() {
 
        //Create and populate the panel.
        JPanel springPanel = new JPanel(new SpringLayout());
        springPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        springPanel.add(new JLabel("EventSource Id"));
        springPanel.add(labSensorId = new JLabel());

        JLabel lFunctionProfile = new JLabel("Function Profile: ");
        springPanel.add(lFunctionProfile);
        jcbFunctionProfile = new JComboBox(FunctionProfileList.getInstance().getFunctionProfileList().toArray());
        springPanel.add(jcbFunctionProfile);
        jcbFunctionProfile.setEnabled(false);

        JLabel l = new JLabel("Paired EventSink: ");
        springPanel.add(l);
        labPairedActuator = new JLabel("N/A");
        springPanel.add(labPairedActuator);

        JLabel lPaired = new JLabel("Paired EventSink: ");
        springPanel.add(lPaired);
        jcbPaired = new JComboBox();
        springPanel.add(jcbPaired);
        jcbPaired.setEnabled(false);

       //Lay out the panel.
        SpringUtilities.makeCompactGrid(springPanel,
                                        4, 2,        //rows, columns
                                        6, 6,        //initX, initY
                                        6, 6);       //xPad, yPad
        
        JPanel p = new JPanel();
        p.add(springPanel);
		return p;
	}
	
	private JPanel createButtonPanel() {
		JPanel panel = new JPanel();
		
		butSave = new JButton("Apply");
		butSave.setEnabled(false);
		panel.add(butSave);
		
		butSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (sensor == null) return;
				if (jcbPaired.getSelectedIndex() <= 0) {
					JOptionPane.showMessageDialog(SensorDetailPanel.this, "Select paired EventSink", "Error", JOptionPane.ERROR_MESSAGE);

					return;
				}

				ComboElement element = (ComboElement)jcbFunctionProfile.getSelectedItem();
				sensor.setFunctionProfileId(element.getValue().toString());
				
				Actuator paired = (Actuator)((ComboElement)jcbPaired.getSelectedItem()).getValue();;
				sensor.setActuatorId(paired.getId());
				
				paired.setSensorId(sensor.getId());

				DFaaSGui.getMe().showStatusMessage("EventSource #"+sensor.getId()+" info. updated.");
				listener.sensorUpdated(sensor);
			}
		});
		
		return panel;
	}
	
	private NetworkTopology networkTopology;
	
	public void show(NetworkTopology networkTopology,Sensor sensor) {
		this.sensor = sensor;
		this.networkTopology=networkTopology;

		if (sensor == null) {
			labSensorId.setText("");
			labPairedActuator.setText("");
			jcbFunctionProfile.setEnabled(false);
			butSave.setEnabled(false);
			//jcbPaired.setSelectedIndex(0);
			jcbPaired.setEnabled(false);

			return;
		}
		NetworkTopologyHelper helper = NetworkTopologyHelper.create(networkTopology);

		
		DefaultComboBoxModel model = new DefaultComboBoxModel(
				ComboElement.toArray(FunctionProfileList.getInstance().getFunctionProfileList(), "functionProfileId",true)
				);
		
		butSave.setEnabled(true);
		jcbPaired.setEnabled(true);
		jcbFunctionProfile.setEnabled(true);

		jcbFunctionProfile.setModel(model);
		
		jcbFunctionProfile.setSelectedItem(new ComboElement(sensor.getFunctionProfileId()));
		
		jcbPaired.removeAllItems();
		List<Actuator> unpairedActuators = helper.getUnpairedActuatorList();
		Actuator currentPaired = (Actuator)helper.getNode(sensor.getActuatorId());
		jcbPaired.addItem(null);
		ComboElement currentElement = null;
		if (currentPaired!=null) jcbPaired.addItem(currentElement = new ComboElement(currentPaired,getPairedLabel(currentPaired)));
		for (int i=0;i<unpairedActuators.size();i++) {
			jcbPaired.addItem(new ComboElement(unpairedActuators.get(i),getPairedLabel(unpairedActuators.get(i))));
		}
		jcbPaired.setSelectedItem(currentElement);
		labSensorId.setText(sensor.getId()+"");

		Actuator pairedActuator = (Actuator)helper.getNode(sensor.getActuatorId());
		if (pairedActuator != null) {
			labPairedActuator.setText(pairedActuator.getId()+"");
		} else {
			labPairedActuator.setText("");
		}
		
	}
	
	private String getPairedLabel(Actuator pairedActuator) {
		if (this.sensor.getEndDeviceGroupId() == pairedActuator.getEndDeviceGroupId()) {
			return pairedActuator.getId()+"";
		}
		return pairedActuator.getId()+" ["+pairedActuator.getEndDeviceGroupId()+"]";
	}
	
	public void clear() {
		labPairedActuator.setText("");
		labSensorId.setText("");
		jcbFunctionProfile.setSelectedIndex(0);
	}
	
}
