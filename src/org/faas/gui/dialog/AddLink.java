package org.faas.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.faas.gui.DFaaSGui;
import org.faas.gui.core.Edge;
import org.faas.gui.core.Graph;
import org.faas.gui.core.Link;
import org.faas.gui.core.NetworkTopologyListener;
import org.faas.gui.core.Node;
import org.faas.gui.core.NodeCellRenderer;
import org.faas.topology.NetworkTopology;
import org.faas.topology.NetworkTopologyHelper;
import org.faas.topology.NodeIF;

/** A dialog to add a new edge */
public class AddLink extends JDialog {
	private static final long serialVersionUID = 4794808969864918000L;
	
	private final Graph graph;
	private JComboBox sourceNode;
	private JComboBox targetNode;
	private JTextField tfLatency;
	private JTextField tfBw;
	private JTextField tfNetworkingUnitCost;

	private NetworkTopologyListener networkTopologyListener;
	private NetworkTopology networkTopology;
	
	private int sourceNodeId;
	private int destNodeId;
	
	public AddLink(final Graph graph, final DFaaSGui gui, NetworkTopology networkTopology, int sourceNodeId, int destNodeId) {

		this.graph = graph;
		this.networkTopologyListener = gui;
		this.networkTopology = networkTopology;
		
		this.sourceNodeId = sourceNodeId;
		this.destNodeId = destNodeId;
		
		setLayout(new BorderLayout());

		add(createInputPanel(), BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.PAGE_END);
		// show dialog
		setTitle("Add Link");
		setModal(true);
		setPreferredSize(new Dimension(400, 260));
		setResizable(false);
		pack();
		setLocationRelativeTo(gui); // must be called between pack and setVisible to work properly
		setVisible(true);
		
	}

	private boolean initSet = true;
	
	@SuppressWarnings("unchecked")
	private JPanel createInputPanel() {

		Component rigid = Box.createRigidArea(new Dimension(10, 0));

		JPanel inputPanelWrapper = new JPanel();
		inputPanelWrapper.setLayout(new BoxLayout(inputPanelWrapper, BoxLayout.PAGE_AXIS));

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.LINE_AXIS));

		JPanel textAreaPanel = new JPanel();
		textAreaPanel.setLayout(new GridLayout(0,2));
		textAreaPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		//textAreaPanel.setLayout(new BoxLayout(textAreaPanel, BoxLayout.LINE_AXIS));

		ComboBoxModel sourceNodeModel = new DefaultComboBoxModel(graph.getAdjacencyList().keySet().toArray());
		sourceNodeModel.setSelectedItem(graph.getNode(sourceNodeId));

		ComboBoxModel targetNodeModel = new DefaultComboBoxModel(graph.getAdjacencyList().keySet().toArray());
		targetNodeModel.setSelectedItem(graph.getNode(destNodeId));
		
		sourceNode = new JComboBox(sourceNodeModel);
		targetNode = new JComboBox(targetNodeModel);
		sourceNode.setMaximumSize(sourceNode.getPreferredSize());
		sourceNode.setMinimumSize(new Dimension(150, sourceNode.getPreferredSize().height));
		sourceNode.setPreferredSize(new Dimension(150, sourceNode.getPreferredSize().height));
		targetNode.setMaximumSize(targetNode.getPreferredSize());
		targetNode.setMinimumSize(new Dimension(150, targetNode.getPreferredSize().height));
		targetNode.setPreferredSize(new Dimension(150, targetNode.getPreferredSize().height));

		NodeCellRenderer renderer = new NodeCellRenderer();

		sourceNode.setRenderer(renderer);
		targetNode.setRenderer(renderer);

		// TODO link가 없는 노드들만 표시 하도록 변경.
//		sourceNode.addItemListener(new ItemListener() {
//
//			@Override
//			public void itemStateChanged(ItemEvent e) {
//				if (initSet) {
//					initSet = false;
//					return;
//				}
//				
//				// only display nodes which do not have already an edge
//
//				targetNode.removeAllItems();
//				Node selectedNode = (Node) sourceNode.getSelectedItem();
//
//				if (selectedNode != null) {
//
//					List<Node> nodesToDisplay = new ArrayList<Node>();
//					Set<Node> allNodes = graph.getAdjacencyList().keySet();
//
//					// get edged for selected node and throw out all target nodes where already an edge exists
//					List<Edge> edgesForSelectedNode = graph.getAdjacencyList().get(selectedNode);
//					Set<Node> nodesInEdges = new HashSet<Node>();
//					for (Edge edge : edgesForSelectedNode) {
//						nodesInEdges.add(edge.getNode());
//					}
//					if(!(selectedNode.getType().equals("SENSOR")||selectedNode.getType().equals("ACTUATOR")) || edgesForSelectedNode.size()==0){
//						for (Node node : allNodes) {
//							if((selectedNode.getType().equals("SENSOR")||selectedNode.getType().equals("ACTUATOR")) && !node.getType().equals("FOG_DEVICE"))
//								continue;
//							if (!node.equals(selectedNode) && !nodesInEdges.contains(node)) {
//								nodesToDisplay.add(node);
//							}
//						}						
//					}
//					
//
//					ComboBoxModel targetNodeModel = new DefaultComboBoxModel(nodesToDisplay.toArray());
//					targetNode.setModel(targetNodeModel);
//				}
//			}
//		});

		inputPanel.add(sourceNode);
		inputPanel.add(new Label("  <---->"));
		inputPanel.add(targetNode);
		inputPanel.add(Box.createHorizontalGlue());
		inputPanelWrapper.add(inputPanel);

		//textAreaPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		textAreaPanel.add(new JLabel("Delay (ms): "));
		tfLatency = new JTextField();
		tfLatency.setMaximumSize(tfLatency.getPreferredSize());
		tfLatency.setMinimumSize(new Dimension(150, tfLatency.getPreferredSize().height));
		tfLatency.setPreferredSize(new Dimension(150, tfLatency.getPreferredSize().height));
		textAreaPanel.add(tfLatency);

		textAreaPanel.add(new JLabel("BW (Mbps): "));
		tfBw = new JTextField();
		tfBw.setMaximumSize(tfLatency.getPreferredSize());
		tfBw.setMinimumSize(new Dimension(150, tfLatency.getPreferredSize().height));
		tfBw.setPreferredSize(new Dimension(150, tfLatency.getPreferredSize().height));
		textAreaPanel.add(tfBw);

		textAreaPanel.add(new JLabel("Networking Unit Cost: "));
		tfNetworkingUnitCost = new JTextField();
		tfNetworkingUnitCost.setMaximumSize(tfLatency.getPreferredSize());
		tfNetworkingUnitCost.setMinimumSize(new Dimension(150, tfLatency.getPreferredSize().height));
		tfNetworkingUnitCost.setPreferredSize(new Dimension(150, tfLatency.getPreferredSize().height));
		textAreaPanel.add(tfNetworkingUnitCost);
		
		//textAreaPanel.add(Box.createHorizontalGlue());

		inputPanelWrapper.add(textAreaPanel);
		inputPanelWrapper.add(Box.createVerticalGlue());

		inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		return inputPanelWrapper;
	}

	private JPanel createButtonPanel() {

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));

		JButton okBtn = new JButton("Ok");
		JButton cancelBtn = new JButton("Cancel");

		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				double latency = 0;
				double bw = 0;
				double networkingUnitCost = 0;
				boolean catchedError = false;

				if (tfLatency.getText() == null || tfLatency.getText().isEmpty()) {
					catchedError = true;
					prompt("Please type latency", "Error");
				} else if (tfBw.getText() == null || tfBw.getText().isEmpty()) {
					catchedError = true;
					prompt("Please type BW", "Error");
				} else  if (tfNetworkingUnitCost.getText() == null || tfNetworkingUnitCost.getText().isEmpty()) {
					catchedError = true;
					prompt("Please type Networking Unit Cost", "Error");
				} else {
					try {
						latency = Double.valueOf(tfLatency.getText());											
						bw = Double.valueOf(tfBw.getText());											
						networkingUnitCost = Double.valueOf(tfNetworkingUnitCost.getText());											
					} catch (NumberFormatException e1) {
						catchedError = true;
						prompt("Latency should be double type", "Error");
					}
				}

				if (!catchedError) {
//					if (sourceNode.getSelectedItem() == null || targetNode.getSelectedItem() == null) {
//						prompt("Please select node", "Error");
//					} else {
//
//						Node source = (Node) sourceNode.getSelectedItem();
//						Node target = (Node) targetNode.getSelectedItem();
//
//						Link edge = new Link(target, latency);
//						graph.addEdge(source, edge);
//
//						setVisible(false);
//					}
					
					int sourceId = ((Node) sourceNode.getSelectedItem()).getId();
					int targetId = ((Node) targetNode.getSelectedItem()).getId();

					org.faas.topology.Link link1 = new org.faas.topology.Link();
					link1.setSourceId(sourceId);
					link1.setDestId(targetId);
					link1.setBw(bw);
					link1.setDelay(latency);
					link1.setNetworkingUnitCost(networkingUnitCost);

					if (networkTopology.addLink(link1)) {
						org.faas.topology.Link link2 = new org.faas.topology.Link();
						link2.setSourceId(targetId);
						link2.setDestId(sourceId);
						link2.setBw(bw);
						link2.setDelay(latency);
						link2.setNetworkingUnitCost(networkingUnitCost);
					
						if (networkTopology.addLink(link2)) {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									networkTopologyListener.networkTopologyChanged();
								}
							});
							setVisible(false);
							
						} else {
							prompt("Link conflicts", "Error");
						}
						

					} else {
						prompt("Link conflicts", "Error");
					}
					

				}

			}
		});

		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(okBtn);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(cancelBtn);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		return buttonPanel;
	}
	
	private void prompt(String msg, String type){
		JOptionPane.showMessageDialog(AddLink.this, msg, type, JOptionPane.ERROR_MESSAGE);
	}

}
