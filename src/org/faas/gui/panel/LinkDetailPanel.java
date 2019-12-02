package org.faas.gui.panel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.faas.gui.swing.DoubleTextField;
import org.faas.gui.swing.GridLayoutUtil;
import org.faas.gui.swing.NullLayoutUtil;
import org.faas.topology.Link;

public class LinkDetailPanel extends JPanel {

	private Link link;
	
	private DoubleTextField jtfDelay = new DoubleTextField();
	private DoubleTextField jtfBW = new DoubleTextField();
	private DoubleTextField jtfNetworkingUnitCost = new DoubleTextField();

	public LinkDetailPanel() {
		super.setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		
		GridLayoutUtil layoutUtils = new GridLayoutUtil(panel);
		layoutUtils.setCol2StartX(150);
		
		int row = 0;
		
		layoutUtils.placeName(row, "Delay (ms)");
		layoutUtils.placeComponent(row, jtfDelay, 100);
		
		row++;
		layoutUtils.placeName(row, "BW (Mbps)");
		layoutUtils.placeComponent(row, jtfBW, 100);

		row++;
		layoutUtils.placeName(row, "Networking Unit Cost");
		layoutUtils.placeComponent(row, jtfNetworkingUnitCost, 100);

		super.add(panel,BorderLayout.NORTH);
		super.add(createButtonPanel(),BorderLayout.PAGE_END);

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
		link.setDelay(jtfDelay.getDoubleValue());
		link.setBw(jtfBW.getDoubleValue());
		link.setNetworkingUnitCost(jtfNetworkingUnitCost.getDoubleValue());
	}
	
	public void show(Link link) {
		this.link = link;
		
		jtfNetworkingUnitCost.setText(link.getNetworkingUnitCost()+"");
		jtfBW.setText(link.getBw()+"");
		jtfDelay.setText(link.getDelay()+"");
	}
}
