package org.faas.gui.panel;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.faas.gui.swing.IntTextField;
import org.faas.gui.swing.NullLayoutUtil;
import org.faas.topology.ComputingResourceRequirements;

public class ComputingResourceRequirementsPanel extends JPanel {

	private ComputingResourceRequirements requirements;
	
	private IntTextField tfCpuCores = new IntTextField();
	private CloudSimDistributionPanel requiredMipsDistPanel = new CloudSimDistributionPanel();
	private IntTextField tfMemorySize = new IntTextField();
	
	public ComputingResourceRequirementsPanel() {
		NullLayoutUtil layoutUtil = new NullLayoutUtil(this);
		layoutUtil.setCol2StartX(160);
		
		int row = 0;
		
		layoutUtil.placeName(row, "Number Of Cpu Cores");
		layoutUtil.placeComponent(row, tfCpuCores, 100);
		
		row++;
		layoutUtil.placeName(row, "MIs Model");
		layoutUtil.placeComponent(row, requiredMipsDistPanel,500);
		
		row++;
		layoutUtil.placeName(row, "Memory size (MB)");
		layoutUtil.placeComponent(row, tfMemorySize, 100);
		
		super.setPreferredSize(new Dimension(700,3*40));
		
		super.setBorder(BorderFactory.createEtchedBorder());

	}
	
	public void show(ComputingResourceRequirements requirements) {
		this.requirements = requirements;
		
		tfCpuCores.setText(requirements.getNumberOfCpuCores()+"");
		requiredMipsDistPanel.show(requirements.getRequiredMipsModel());
		tfMemorySize.setText(requirements.getMemorySize()+"");
	}
	
	public void updateValues() {
		this.requirements.setNumberOfCpuCores(Integer.parseInt(tfCpuCores.getText()));
		this.requiredMipsDistPanel.updateValues();
		this.requirements.setMemorySize(Integer.parseInt(tfMemorySize.getText()));
	}

}
