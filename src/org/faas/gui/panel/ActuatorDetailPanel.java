package org.faas.gui.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.faas.gui.DFaaSGui;
import org.faas.gui.core.SpringUtilities;
import org.faas.gui.dialog.AddSensor;
import org.faas.gui.swing.ComboElement;
import org.faas.topology.Actuator;
import org.faas.topology.FunctionProfileList;
import org.faas.topology.NetworkTopology;
import org.faas.topology.NetworkTopologyHelper;
import org.faas.topology.Sensor;

public class ActuatorDetailPanel extends JPanel {

	private JComboBox jcbPaired;
	private JComboBox jcbFunctionProfile;
	private JLabel    labFunctionProfile;
	private JLabel    labPairedSensor;
	private JLabel    labActuatorId;

	private JButton butSave;
	
	private Actuator actuator;
	
	private EndElementListener listener;
	
	public ActuatorDetailPanel(EndElementListener listener) {
		this.listener = listener;

		setLayout(new BorderLayout());

		add(createInputPanelArea(), BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);
		
		super.setPreferredSize(new Dimension(300,200));
	}
	
	private JPanel createInputPanelArea() {

		//Create and populate the panel.
        JPanel springPanel = new JPanel(new SpringLayout());
        springPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        springPanel.add(new JLabel("EventSink Id"));
        springPanel.add(labActuatorId = new JLabel());
        
        JLabel lFunctionProfile = new JLabel("Function Profile: ");
        springPanel.add(lFunctionProfile);
        jcbFunctionProfile = new JComboBox(FunctionProfileList.getInstance().getFunctionProfileList().toArray());
		jcbFunctionProfile.setEnabled(false);
        springPanel.add(jcbFunctionProfile);

//        JLabel lFunctionProfile = new JLabel("Function Profile: ");
//        springPanel.add(lFunctionProfile);
//        springPanel.add(labFunctionProfile);

        JLabel l = new JLabel("Paired EventSource: ");
        springPanel.add(l);
        labPairedSensor = new JLabel("N/A");
        springPanel.add(labPairedSensor);

        JLabel lPaired = new JLabel("Paired EventSource: ");
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
				if (actuator == null) return;
				if (jcbPaired.getSelectedIndex() <= 0) {
					JOptionPane.showMessageDialog(ActuatorDetailPanel.this, "Select paired EventSource", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				Sensor paired = (Sensor)((ComboElement)jcbPaired.getSelectedItem()).getValue();
				actuator.setSensorId(paired.getId());
				
				paired.setActuatorId(actuator.getId());
				
				DFaaSGui.getMe().showStatusMessage("EventSink #"+actuator.getId()+" info. updated.");
				listener.actuatorUpdated(actuator);
			}
		});
		
		return panel;
	}
	
	private NetworkTopologyHelper helper;
	public void show(NetworkTopology networkTopology,Actuator actuator) {
		this.actuator = actuator;
	
		if (actuator == null) {
			labActuatorId.setText("");
			labPairedSensor.setText("");
			butSave.setEnabled(false);
			//jcbPaired.setSelectedIndex(0);
			jcbPaired.setEnabled(false);

			return;
		}
		
		DefaultComboBoxModel model = new DefaultComboBoxModel(
				ComboElement.toArray(FunctionProfileList.getInstance().getFunctionProfileList(), "functionProfileId",true)
				);

		butSave.setEnabled(true);
		jcbPaired.setEnabled(true);
		jcbFunctionProfile.setModel(model);

		//labActuatorId.setText(actuator.getId()+"");
		
		helper = NetworkTopologyHelper.create(networkTopology);
		
		Sensor pairedSensor = (Sensor)helper.getNode(actuator.getSensorId());

		if (pairedSensor != null) {
			labPairedSensor.setText(pairedSensor.getId()+"");
			jcbFunctionProfile.setSelectedItem(new ComboElement(pairedSensor.getFunctionProfileId()));
		}

		jcbPaired.removeAllItems();
		List<Sensor> unpairedSensors = helper.getUnpairedSensorList();
		Sensor currentPaired = (Sensor)helper.getNode(actuator.getSensorId());
		jcbPaired.addItem(null);
		ComboElement currentElement = null;
		if (currentPaired!=null) jcbPaired.addItem(currentElement = new ComboElement(currentPaired,getPairedLabel(currentPaired)));
		for (int i=0;i<unpairedSensors.size();i++) {
			jcbPaired.addItem(new ComboElement(unpairedSensors.get(i),getPairedLabel(unpairedSensors.get(i))));
		}
		jcbPaired.setSelectedItem(currentElement);
		labActuatorId.setText(actuator.getId()+"");
		
	}
	
	private String getPairedLabel(Sensor pairedSensor) {
		if (this.actuator.getEndDeviceGroupId() == pairedSensor.getEndDeviceGroupId()) {
			return pairedSensor.getId()+"";
		}
		return pairedSensor.getId()+" ["+pairedSensor.getEndDeviceGroupId()+"]";
	}
	
	public void clear() {
		labActuatorId.setText("");
		labPairedSensor.setText("");
		jcbFunctionProfile.setSelectedIndex(0);
	}

}
