package org.faas.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.faas.DFaaSConstants;
import org.faas.gui.DFaaSGui;
import org.faas.gui.core.Graph;
import org.faas.gui.core.NetworkTopologyListener;
import org.faas.gui.core.SpringUtilities;
import org.faas.gui.swing.ComboElement;
import org.faas.gui.swing.DoubleTextField;
import org.faas.gui.swing.IntTextField;
import org.faas.topology.NetworkTopology;
import org.faas.topology.NodeGroup;

@SuppressWarnings({ "rawtypes" })
public class AddFogDevice extends JDialog {
	private static final long serialVersionUID = -5116677861770319577L;
	
	private NetworkTopology networkTopology;
	private final Graph graph;
	
	private JComboBox jcbNodeGroupType = new JComboBox();
	private JLabel deviceNameLabel;
	private JLabel upBwLabel;
	private JLabel downBwLabel;
	private JLabel nodeGroupTypeLabel;
	private JLabel mipsLabel;
	private JLabel ramLabel;
	private JLabel levelLabel;
	private JLabel rateLabel;
	
	private JTextField deviceName;
	private JTextField upBw;
	private JTextField downBw;
	private JTextField mips;
	private JTextField ram;
	private JTextField level;
	private JTextField rate;

	private IntTextField jtfNumberOfCpuCores = new IntTextField();
	private DoubleTextField jtfComputingUnitCost = new DoubleTextField();

	private NetworkTopologyListener networkTopologyListener;

	public AddFogDevice(final Graph graph, final DFaaSGui gui,NetworkTopology networkTopology) {
		this.graph = graph;
		this.networkTopologyListener = gui;
		this.networkTopology = networkTopology;
		
		setLayout(new BorderLayout());

		add(createInputPanelArea(), BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.PAGE_END);
		// show dialog
		setTitle("Add Node Group");
		setModal(true);
		setPreferredSize(new Dimension(350, 260));
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
				boolean catchedError = false;
				if (mips.getText() == null || mips.getText().length() < 1) {
					prompt("Please enter MIPS", "Error");				
				} else if (ram.getText() == null || ram.getText().length() < 1) {
					prompt("Please enter RAM", "Error");				
//				} else if (rate.getText() == null || rate.getText().length() < 1) {
//					prompt("Please enter Rate", "Error");
				}
//				long upBw_=-1, downBw_=-1; 
//				int level_=-1;
//				double rate_ = -1;
				long mips_=-1;
				int ram_=-1;
				int type = 0; // node type
				int numberOfCpuCores = 0;
				double computingUnitCost = 0.0;
				
				try {
					//upBw_ = Long.parseLong(upBw.getText());
					//downBw_ = Long.parseLong(downBw.getText());
					mips_ = Long.parseLong(mips.getText());
					ram_ = Integer.parseInt(ram.getText());
					//level_ = Integer.parseInt(level.getText());
					//rate_ = Double.parseDouble(rate.getText());
					
					numberOfCpuCores = jtfNumberOfCpuCores.getIntValue();
					computingUnitCost = jtfComputingUnitCost.getDoubleValue();
					
					catchedError = false;

					type = ((Integer)((ComboElement)jcbNodeGroupType.getSelectedItem()).getValue()).intValue();
					if (type == 0) {
						catchedError = true;
						prompt("Select Node Group type", "Error");
					}
				} catch (NumberFormatException e1) {
					catchedError = true;
					prompt("Input should be numerical character", "Error");
				}
				if(!catchedError){
					NodeGroup nodeGroup = new NodeGroup();
					nodeGroup.setId(NetworkTopology.getEntityId());
					nodeGroup.setType(type);
					nodeGroup.setMips((int)mips_);
					nodeGroup.setRam(ram_);
					nodeGroup.setComputingUnitCost(computingUnitCost);
					nodeGroup.setNumberOfCpuCores(numberOfCpuCores);
					//nodeGroup.setRatePerMips(rate_);
					networkTopology.addNodeGroup(nodeGroup);
					
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							networkTopologyListener.networkTopologyChanged();
						}
					});
					//NodeGroupGui fogDevice = new NodeGroupGui(deviceName.getText().toString(), mips_, ram_, upBw_, downBw_, level_, rate_);
					//graph.addNode(fogDevice);
					setVisible(false);								
				}
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
        //springPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));		

        nodeGroupTypeLabel = new JLabel("NodeGroup type: ");
		springPanel.add(nodeGroupTypeLabel);	

		List<ComboElement> gradeList = new ArrayList<ComboElement>();
		//gradeList.add(new ComboElement(0,"N/A"));
		gradeList.add(new ComboElement(DFaaSConstants.FOG_NODE_GROUP,DFaaSConstants.getEntityName(DFaaSConstants.FOG_NODE_GROUP)));
		gradeList.add(new ComboElement(DFaaSConstants.EDGE_NODE_GROUP,DFaaSConstants.getEntityName(DFaaSConstants.EDGE_NODE_GROUP)));
		gradeList.add(new ComboElement(DFaaSConstants.CORE_NODE_GROUP,DFaaSConstants.getEntityName(DFaaSConstants.CORE_NODE_GROUP)));
		jcbNodeGroupType.setModel(new DefaultComboBoxModel(gradeList.toArray()));
		springPanel.add(jcbNodeGroupType);		
		
		/** switch and host  */
		
		mipsLabel = new JLabel("Mips: ");
		springPanel.add(mipsLabel);	
		mips = new JTextField();
		mipsLabel.setLabelFor(mips);
		springPanel.add(mips);		
		
		ramLabel = new JLabel("Ram: ");
		springPanel.add(ramLabel);
		ram = new JTextField();
		ramLabel.setLabelFor(ram);
		springPanel.add(ram);
		
//		rateLabel = new JLabel("Rate/MIPS: ");
//		springPanel.add(rateLabel);
//		rate = new JTextField();
//		rateLabel.setLabelFor(rate);
//		springPanel.add(rate);

		
		JLabel labNumberOfCpuCores = new JLabel("Number Of CPU Cores: ");
		springPanel.add(labNumberOfCpuCores);
		jtfNumberOfCpuCores = new IntTextField();
		labNumberOfCpuCores.setLabelFor(jtfNumberOfCpuCores);
		springPanel.add(jtfNumberOfCpuCores);
		
		JLabel labComputingUnitCost = new JLabel("Computing Unit Cost: ");
		springPanel.add(labComputingUnitCost);
		jtfComputingUnitCost = new DoubleTextField();
		labComputingUnitCost.setLabelFor(jtfComputingUnitCost);
		springPanel.add(jtfComputingUnitCost);
		
        //Lay out the panel.
        SpringUtilities.makeCompactGrid(springPanel,
                                        5, 2,        //rows, cols
                                        6, 6,        //initX, initY
                                        6, 6);       //xPad, yPad
        //updatePanel("core");
		return springPanel;
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
		JOptionPane.showMessageDialog(AddFogDevice.this, msg, type, JOptionPane.ERROR_MESSAGE);
	}
}
