package org.faas.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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
import org.faas.gui.core.Graph;
import org.faas.gui.core.NetworkTopologyListener;
import org.faas.gui.core.SensorGui;
import org.faas.gui.core.SpringUtilities;
import org.faas.gui.panel.EndElementListener;
import org.faas.topology.EndDeviceGroup;
import org.faas.topology.FunctionProfile;
import org.faas.topology.FunctionProfileList;
import org.faas.topology.NetworkTopology;
import org.faas.topology.Sensor;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class AddSensor extends JDialog {
	private static final long serialVersionUID = -511667786177319577L;
	
	private final Graph graph;
	
	private JTextField sensorName;
	private JTextField sensorType;
	private JComboBox distribution;
	private JTextField uniformLowerBound;
	private JTextField uniformUpperBound;
	private JTextField deterministicValue;
	private JTextField normalMean;
	private JTextField normalStdDev;
	
	private JComboBox jcbFunctionProfile;

	private NetworkTopology networkTopology;
	private NetworkTopologyListener networkTopologyListener;
	private EndDeviceGroup endDeviceGroup;
	
	private EndElementListener listener;
	
	/**
	 * Constructor.
	 * 
	 * @param frame the parent frame
	 */
	public AddSensor(final Graph graph,DFaaSGui gui, EndElementListener listener,NetworkTopology networkTopology,EndDeviceGroup endDeviceGroup) {
		this.graph = graph;
		//this.networkTopologyListener = gui;
		this.listener = listener;
		this.networkTopology = networkTopology;
		this.endDeviceGroup = endDeviceGroup;
		
		setLayout(new BorderLayout());

		add(createInputPanelArea(), BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.PAGE_END);
		// show dialog
		setTitle("Add EventSource");
		setModal(true);
		setPreferredSize(new Dimension(520, 150));
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
				FunctionProfile fp = (FunctionProfile)jcbFunctionProfile.getSelectedItem();
				
				Sensor sensor = new Sensor();
				sensor.setEndDeviceGroupId(endDeviceGroup.getId());
				sensor.setId(NetworkTopology.getEntityId());
				sensor.setFunctionProfileId(fp.getFunctionProfileId());
				
				endDeviceGroup.addSensor(sensor);
				
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						//networkTopologyListener.networkTopologyChanged();
						listener.sensorAdded(sensor);
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
	    String[] distributionType = {"Normal", "Uniform", "Deterministic"};
 
        //Create and populate the panel.
        JPanel springPanel = new JPanel(new SpringLayout());
        springPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lFunctionProfile = new JLabel("Function Profile: ");
        springPanel.add(lFunctionProfile);
        
        jcbFunctionProfile = new JComboBox(FunctionProfileList.getInstance().getFunctionProfileList().toArray());
        springPanel.add(jcbFunctionProfile);
        
//		JLabel lName = new JLabel("Name: ");
//		springPanel.add(lName);
//		sensorName = new JTextField();
//		lName.setLabelFor(sensorName);
//		springPanel.add(sensorName);
//		
//		JLabel lType = new JLabel("Type: ");
//		springPanel.add(lType);
//		sensorType = new JTextField();
//		lType.setLabelFor(sensorType);
//		springPanel.add(sensorType);
//				
//		JLabel distLabel = new JLabel("Distribution Type: ", JLabel.TRAILING);
//		springPanel.add(distLabel);	
//		distribution = new JComboBox(distributionType);
//		distLabel.setLabelFor(distribution);
//		distribution.setSelectedIndex(-1);
//		distribution.addItemListener(new ItemListener() {
//			@Override
//			public void itemStateChanged(ItemEvent e) {
//				JComboBox ctype = (JComboBox)e.getSource();
//				String item = (String)ctype.getSelectedItem();
//				updatePanel(item);				
//			}
//		});
//		
//		
//		springPanel.add(distribution);		
//		
//		JLabel normalMeanLabel = new JLabel("Mean: ");
//		springPanel.add(normalMeanLabel);	
//		normalMean = new JTextField();
//		normalMeanLabel.setLabelFor(normalMean);
//		springPanel.add(normalMean);
//		
//		JLabel normalStdDevLabel = new JLabel("StdDev: ");
//		springPanel.add(normalStdDevLabel);	
//		normalStdDev = new JTextField();
//		normalStdDevLabel.setLabelFor(normalStdDev);
//		springPanel.add(normalStdDev);
//		
//		JLabel uniformLowLabel = new JLabel("Min: ");
//		springPanel.add(uniformLowLabel);	
//		uniformLowerBound = new JTextField();
//		uniformLowLabel.setLabelFor(uniformLowerBound);
//		springPanel.add(uniformLowerBound);
//		
//		JLabel uniformUpLabel = new JLabel("Max: ");
//		springPanel.add(uniformUpLabel);	
//		uniformUpperBound = new JTextField();
//		uniformUpLabel.setLabelFor(uniformUpperBound);
//		springPanel.add(uniformUpperBound);
//		
//		JLabel deterministicValueLabel = new JLabel("Value: ");
//		springPanel.add(deterministicValueLabel);	
//		deterministicValue = new JTextField();
//		uniformLowLabel.setLabelFor(deterministicValue);
//		springPanel.add(deterministicValue);		
						
       //Lay out the panel.
        SpringUtilities.makeCompactGrid(springPanel,
                                        1, 2,        //rows, columns
                                        6, 6,        //initX, initY
                                        6, 6);       //xPad, yPad
		return springPanel;
	}
	
    protected void updatePanel(String item) {
		switch(item){
		case "Normal":
			normalMean.setVisible(true);
			normalStdDev.setVisible(true);
			uniformLowerBound.setVisible(false);
			uniformUpperBound.setVisible(false);
			deterministicValue.setVisible(false);
			break;
		case "Uniform":
			normalMean.setVisible(false);
			normalStdDev.setVisible(false);
			uniformLowerBound.setVisible(true);
			uniformUpperBound.setVisible(true);
			deterministicValue.setVisible(false);
			break;
		case "Deterministic":
			normalMean.setVisible(false);
			normalStdDev.setVisible(false);
			uniformLowerBound.setVisible(false);
			uniformUpperBound.setVisible(false);
			deterministicValue.setVisible(true);
			break;
		default:
			break;
		}
		
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
		JOptionPane.showMessageDialog(AddSensor.this, msg, type, JOptionPane.ERROR_MESSAGE);
	}
}
