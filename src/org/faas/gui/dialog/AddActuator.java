package org.faas.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.faas.gui.DFaaSGui;
import org.faas.gui.core.ActuatorGui;
import org.faas.gui.core.Graph;
import org.faas.gui.core.NetworkTopologyListener;
import org.faas.gui.core.Node;
import org.faas.gui.core.SpringUtilities;
import org.faas.gui.panel.EndElementListener;
import org.faas.gui.swing.ComboElement;
import org.faas.topology.Actuator;
import org.faas.topology.EndDeviceGroup;
import org.faas.topology.FunctionProfileList;
import org.faas.topology.NetworkTopology;
import org.faas.topology.Sensor;

@SuppressWarnings({ "rawtypes" })
public class AddActuator extends JDialog {
	private static final long serialVersionUID = -511667786177319577L;
	
	private final Graph graph;
	
	private JComboBox jcbPairedSensor; // paired sensor id
	
	private NetworkTopology networkTopology;
	private NetworkTopologyListener networkTopologyListener;
	private EndDeviceGroup endDeviceGroup;

	private EndElementListener listener;

	/**
	 * Constructor.
	 * 
	 * @param frame the parent frame
	 */
	public AddActuator(final Graph graph, final DFaaSGui gui, EndElementListener listener,NetworkTopology networkTopology,EndDeviceGroup endDeviceGroup) {
		this.graph = graph;
		this.networkTopologyListener = gui;
		this.listener = listener;
		this.networkTopology = networkTopology;
		this.endDeviceGroup = endDeviceGroup;

		setLayout(new BorderLayout());

		add(createInputPanelArea(), BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.PAGE_END);
		
		// show dialog
		setTitle("Add EventSink");
		setModal(true);
		setPreferredSize(new Dimension(350, 150));
		setResizable(false);
		pack();
		setLocationRelativeTo(gui);
		setVisible(true);

	}

	private JPanel createButtonPanel() {

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		
		JButton okBtn = new JButton("Ok");
		JButton cancelBtn = new JButton("Cancel");
		
		cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	setVisible(false);
            }
        });

		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Sensor pairingSensor = (Sensor)((ComboElement)jcbPairedSensor.getSelectedItem()).getValue();
				
				if (pairingSensor == null) {
					prompt("Actuator를 선택 하세요.","Error");
					return;
				}
				
				Actuator actuator = new Actuator();
				actuator.setEndDeviceGroupId(endDeviceGroup.getId());
				actuator.setId(NetworkTopology.getEntityId());
				//actuator.setFunctionProfileId(pairingSensor.getFunctionProfileId());
				actuator.setSensorId(pairingSensor.getId());
				
				pairingSensor.setActuatorId(actuator.getId());
				
				endDeviceGroup.addActuator(actuator);
				
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						//networkTopologyListener.networkTopologyChanged();
						listener.actuatorAdded(actuator);
					}
				});
				
				setVisible(false);	

			}
		});

		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(okBtn);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(cancelBtn);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		return buttonPanel;
	}

	private JPanel createInputPanelArea() { 
        //Create and populate the panel.
        JPanel springPanel = new JPanel(new SpringLayout());
        springPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		
        JLabel lFunctionProfile = new JLabel("Paired EventSource: ");
        springPanel.add(lFunctionProfile);

        jcbPairedSensor = new JComboBox();//(graph.getUnpairedSensorList().toArray());
        List<Sensor> unpairedSensors = graph.getUnpairedSensorList();
        for (int i=0;i<unpairedSensors.size();i++) {
        	jcbPairedSensor.addItem(new ComboElement(unpairedSensors.get(i),getPairedLabel(unpairedSensors.get(i))));
        }
        springPanel.add(jcbPairedSensor);

							
       //Lay out the panel.
        SpringUtilities.makeCompactGrid(springPanel,
                                        1, 2,        //rows, columns
                                        6, 6,        //initX, initY
                                        6, 6);       //xPad, yPad
		return springPanel;
	}
	
	private String getPairedLabel(Sensor pairedSensor) {
		if (endDeviceGroup.getId() == pairedSensor.getEndDeviceGroupId()) {
			return pairedSensor.getId()+"";
		}
		return pairedSensor.getId()+" ["+pairedSensor.getEndDeviceGroupId()+"]";
	}
	
	public static void setUIFont (javax.swing.plaf.FontUIResource f){
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
          Object key = keys.nextElement();
          Object value = UIManager.get (key);
          if (value != null && value instanceof javax.swing.plaf.FontUIResource)
            UIManager.put (key, f);
          }
    }
    
	private void prompt(String msg, String type){
		JOptionPane.showMessageDialog(AddActuator.this, msg, type, JOptionPane.ERROR_MESSAGE);
	}
}
