package org.faas.gui.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.cloudbus.cloudsim.sdn.graph.dialog.AddPhysicalEdge;
import org.faas.DFaaSConstants;
import org.faas.gui.swing.ComboElement;
import org.faas.gui.swing.DoubleTextField;
import org.faas.gui.swing.GridLayoutUtil;
import org.faas.gui.swing.IntTextField;
import org.faas.gui.swing.NullLayoutUtil;
import org.faas.topology.FunctionPlacementAgentInfo;
import org.faas.topology.NodeGroup;

public class NodeGroupDetailPanel extends JPanel {

	private NodeGroup nodeGroup;

	private JLabel labId = new JLabel("N/A");
	private IntTextField jtfNumberOfCpuCores = new IntTextField();
	private IntTextField jtfMips = new IntTextField();
	private IntTextField jtfRam = new IntTextField();
	private DoubleTextField jtfRatePerMips = new DoubleTextField();
	private DoubleTextField jtfComputingUnitCost = new DoubleTextField();
	private IntTextField jtfQueueSize = new IntTextField(5);
	private JCheckBox jcbQueueSize = new JCheckBox();

	private FunctionPlacementAgentInfoPanel functionPlacementControlPanel = new FunctionPlacementAgentInfoPanel();
	
	public NodeGroupDetailPanel() {
		
		super.setLayout(new BorderLayout());
		
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 20, 5));
		GridLayoutUtil layoutUtils = new GridLayoutUtil(panel);
		layoutUtils.setCol2StartX(100);
		
		int row = 0;
		
		labId.setBorder(BorderFactory.createEtchedBorder());
		layoutUtils.placeName(row, "ID");
		layoutUtils.placeComponent(row, labId);
		
		row++;
		layoutUtils.placeName(row, "Number of CPU Cores");
		layoutUtils.placeComponent(row, jtfNumberOfCpuCores, 100);

		row++;
		layoutUtils.placeName(row, "MIPs");
		layoutUtils.placeComponent(row, jtfMips, 100);

		row++;
		layoutUtils.placeName(row, "Memory (MB)");
		layoutUtils.placeComponent(row, jtfRam, 100);

//		row++;
//		layoutUtils.placeName(row, "Rate per MIPS");
//		layoutUtils.placeComponent(row, jtfRatePerMips, 100);

		row++;
		layoutUtils.placeName(row, "Computing Unit Cost");
		layoutUtils.placeComponent(row, jtfComputingUnitCost);

		
		row++;
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEADING));
		p.add(jtfQueueSize);
		p.add(jcbQueueSize);
		layoutUtils.placeName(row, "Queue Size");
		layoutUtils.placeComponent(row, p);
		
		super.add(panel,BorderLayout.NORTH);
		super.add(functionPlacementControlPanel, BorderLayout.CENTER);
		super.add(createButtonPanel(),BorderLayout.PAGE_END);
		
		functionPlacementControlPanel.setVisible(false);
		
		jcbQueueSize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (jcbQueueSize.isSelected()) {
					jtfQueueSize.setText("0");
				}
				jtfQueueSize.setEnabled(!jcbQueueSize.isSelected());

			}
		});
	}
	
	private JPanel createButtonPanel() {
		JPanel panel = new JPanel();
		
		JButton butSave = new JButton("Apply");
		panel.add(butSave);
		
		butSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				updateValues();
			}
		});
		
		return panel;
	}
	
	public void updateValues() {
		nodeGroup.setMips(jtfMips.getIntValue());
		nodeGroup.setRam(jtfRam.getIntValue());
		nodeGroup.setRatePerMips(jtfRatePerMips.getDoubleValue());
		nodeGroup.setComputingUnitCost(jtfComputingUnitCost.getDoubleValue());
		nodeGroup.setNumberOfCpuCores(jtfNumberOfCpuCores.getIntValue());
		nodeGroup.setQueueSize(jtfQueueSize.getIntValue());
		
		if (nodeGroup.getType() == DFaaSConstants.FOG_NODE_GROUP) {
			
			FunctionPlacementAgentInfo functionPlacementAgentInfo = this.functionPlacementControlPanel.getAgentInfo();
			if (functionPlacementAgentInfo == null) {
				JOptionPane.showMessageDialog(this, "Function Placement 정보를 설정 하세요.", "Error", JOptionPane.ERROR_MESSAGE);
			} else {
				nodeGroup.setFunctionPlacementAgentInfo(functionPlacementAgentInfo);
			}
			
		}
	}
	
	public void show(NodeGroup nodeGroup) {
		this.nodeGroup = nodeGroup;
		
		labId.setText(nodeGroup.getId()+"");
		jtfMips.setText(nodeGroup.getMips()+"");
		jtfRam.setText(nodeGroup.getRam()+"");
		jtfRatePerMips.setText(nodeGroup.getRatePerMips()+"");
		jtfComputingUnitCost.setText(nodeGroup.getComputingUnitCost()+"");
		jtfNumberOfCpuCores.setText(nodeGroup.getNumberOfCpuCores()+"");
		jtfQueueSize.setText(nodeGroup.getQueueSize()+"");
		jcbQueueSize.setSelected(nodeGroup.getQueueSize()==0?true:false);
		jtfQueueSize.setEnabled(nodeGroup.getQueueSize()==0?false:true);
		
		functionPlacementControlPanel.setVisible(nodeGroup.getType() == DFaaSConstants.FOG_NODE_GROUP ? true:false);

		if (nodeGroup.getType() == DFaaSConstants.FOG_NODE_GROUP) {
			functionPlacementControlPanel.show(nodeGroup.getFunctionPlacementAgentInfo());
		}
	}
}
