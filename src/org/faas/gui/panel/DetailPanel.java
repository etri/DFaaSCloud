package org.faas.gui.panel;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.faas.DFaaSConstants;
import org.faas.gui.DFaaSGui;
import org.faas.gui.core.Graph;
import org.faas.topology.Actuator;
import org.faas.topology.EndDeviceGroup;
import org.faas.topology.Link;
import org.faas.topology.NetworkTopology;
import org.faas.topology.NodeGroup;
import org.faas.topology.Sensor;

public class DetailPanel extends JPanel {

	private CardLayout layout = new CardLayout();
	private JPanel cardPanel = new JPanel();

	private JLabel labTitle = new JLabel("");
	
	private NodeGroupDetailPanel nodeGroupDetailPanel = new NodeGroupDetailPanel();
	private LinkDetailPanel linkDetailPanel = new LinkDetailPanel();
//	private SensorDetailPanel sensorDetailPanel = new SensorDetailPanel();
//	private ActuatorDetailPanel actuatorDetailPanel = new ActuatorDetailPanel();
	private EndDeviceGroupDetailPanel endDeviceGroupDetailPanel = new EndDeviceGroupDetailPanel();
	
	public DetailPanel() {
		super.setBorder(BorderFactory.createLoweredSoftBevelBorder());
		super.setLayout(new BorderLayout());
		
		labTitle.setHorizontalAlignment(JLabel.CENTER);
		labTitle.setBorder(BorderFactory.createEtchedBorder());
		
		super.add(labTitle, BorderLayout.NORTH);

		cardPanel.setLayout(layout);
		super.add(cardPanel,BorderLayout.CENTER);
		
		cardPanel.add("nodeGroup", nodeGroupDetailPanel);
		cardPanel.add("link", linkDetailPanel);
		//cardPanel.add("sensor", sensorDetailPanel);
		//cardPanel.add("actuator", actuatorDetailPanel);
		cardPanel.add("endDeviceGroup", endDeviceGroupDetailPanel);
		
		
	}
	
	Graph graph;
	DFaaSGui gui;
	
	public void show(Graph graph,DFaaSGui gui,NetworkTopology networkTopology,EndDeviceGroup node) {
		this.graph = graph;
		this.gui = gui;
		labTitle.setText("End Device Group"+" "+node.getId());

		layout.show(cardPanel, "endDeviceGroup");
		endDeviceGroupDetailPanel.show(graph,gui,networkTopology,node);
	}

	public void show(NodeGroup node) {
		labTitle.setText("Node Group - "+DFaaSConstants.getEntityName(node.getType())+" "+node.getId());
		
		layout.show(cardPanel, "nodeGroup");
		nodeGroupDetailPanel.show(node);
	}

	public void show(Link link) {
		labTitle.setText("Link");
		
		layout.show(cardPanel, "link");
		linkDetailPanel.show(link);
	}

	@Deprecated
	public void show(NetworkTopology networkTopology,Sensor sensor) {
//		labTitle.setText("EventSource "+sensor.getId());
//		
//		layout.show(cardPanel, "sensor");
//		sensorDetailPanel.show(networkTopology,sensor);
	}
	
	@Deprecated
	public void show(NetworkTopology networkTopology,Actuator actuator) {
//		labTitle.setText("EventSink "+actuator.getId());
//		
//		layout.show(cardPanel, "actuator");
//		actuatorDetailPanel.show(networkTopology, actuator);
	}

}
