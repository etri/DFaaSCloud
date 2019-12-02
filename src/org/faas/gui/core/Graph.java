package org.faas.gui.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.faas.topology.Actuator;
import org.faas.topology.EndDeviceGroup;
import org.faas.topology.NetworkTopology;
import org.faas.topology.Sensor;

/**
 * A graph model. Normally a model should not have any logic, but in this case we implement logic to manipulate the
 * adjacencyList like reorganizing, adding nodes, removing nodes, e.g
 *
 */
public class Graph implements Serializable {
	private static final long serialVersionUID = 745864022429447529L;
	// CWE-495 : private -> public
	public Map<Node, List<Edge>> adjacencyList;

	protected List<Node> nodeList;
	protected List<Edge> edgeList;

	public Graph() {
		// when creating a new graph ensure that a new adjacencyList is created
		adjacencyList = new HashMap<Node, List<Edge>>();
		
		//edgeList = new ArrayList<Edge>();
		//nodeList = new ArrayList<Node>();
	}
	
	public Graph(Map<Node, List<Edge>> adjacencyList) {
		this.adjacencyList = adjacencyList;
		
		//reset();
	}
	
	public Node getNode(int id) {
		Iterator<Node> ite = adjacencyList.keySet().iterator();
		while (ite.hasNext()) {
			Node node = ite.next();
			if (node.getId() == id) {
				return node;
			}
		}
		
		return null;
	}
	
	public Node getPairedNode(Node node) {
		int pairedId = -1;
		if (node.getData() instanceof Sensor) {
			pairedId = ((Sensor)node.getData()).getActuatorId();
		} else if (node.getData() instanceof Actuator) {
			pairedId = ((Actuator)node.getData()).getSensorId();
		}

		if (pairedId != -1) {
			Iterator<Node> ite = adjacencyList.keySet().iterator();
			while (ite.hasNext()) {
				Node n = ite.next();
				//System.out.println(this.getClass()+".getPairedNode: "+n.getId() + " > " + n);
				if (pairedId == n.getId()) {
					return n;
				}
			}

		}
		
		return null;
	}
	
	public Object findElement(double x,double y) {

		//Iterator<Node> nodeIte = adjacencyList.keySet().iterator();
		List<Node> sortedList = new ArrayList<Node>(adjacencyList.keySet());
		Collections.sort(sortedList, new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				return ((Node)o1).getWidth() - ((Node)o2).getWidth();
			}
		});

		Iterator<Node> nodeIte = sortedList.iterator();
		while (nodeIte.hasNext()) {
			Node node = nodeIte.next();
			//nodeList.add(node);
			
			Iterator<Edge> edgeIte = adjacencyList.get(node).iterator();
			while (edgeIte.hasNext()) {
				Edge edge = edgeIte.next();
				//edgeList.add(edge);

				if (edge.contains(x, y)) {
					return edge;
				}
			}

			if (node.contains(x, y)) {
				return node;
			}
		}
		return null;
	}
	
	public List<Node> getNodeList() {
		return nodeList;
	}
	public List<Edge> getEdgeList() {
		return edgeList;
	}
	
	public List<Sensor> getUnpairedSensorList() {
		List<Sensor> sensors = new ArrayList<Sensor>();
		Iterator<Node> nodeIte = adjacencyList.keySet().iterator();
		while (nodeIte.hasNext()) {
			Node node = nodeIte.next();

			if (node.getData() instanceof EndDeviceGroup) {
				EndDeviceGroup endGroup = (EndDeviceGroup)node.getData();
				List<Sensor> sensorList = endGroup.getSensorList();
				for (int i=0;i<sensorList.size();i++) {
					int pairedActuatorId = sensorList.get(i).getActuatorId();
					if (pairedActuatorId == 0) {
						sensors.add(sensorList.get(i));
					}
				}
			}
			
//			if (node.getData() instanceof EventSource) {
//				int pairedActustorId = ((EventSource)node.getData()).getActuatorId();
//				if (pairedActustorId == 0) {
//					sensors.add(node);
//				}
//			}
		}

		return sensors;
	}
	
	public List<Actuator> getUnpairedActuatorList() {
		List<Actuator> actuators = new ArrayList<Actuator>();
		Iterator<Node> nodeIte = adjacencyList.keySet().iterator();
		while (nodeIte.hasNext()) {
			Node node = nodeIte.next();

			if (node.getData() instanceof EndDeviceGroup) {
				EndDeviceGroup endGroup = (EndDeviceGroup)node.getData();
				List<Actuator> actuatorsList = endGroup.getActuatorList();
				for (int i=0;i<actuatorsList.size();i++) {
					int pairedSensorId = actuatorsList.get(i).getSensorId();
					if (pairedSensorId == 0) {
						actuators.add(actuatorsList.get(i));
					}
				}
			}

		}

		return actuators;
	}
	
	private void reset() {
		Iterator<Node> nodeIte = adjacencyList.keySet().iterator();
		while (nodeIte.hasNext()) {
			Node node = nodeIte.next();
			nodeList.add(node);
			
			Iterator<Edge> edgeIte = adjacencyList.get(node).iterator();
			while (edgeIte.hasNext()) {
				Edge edge = edgeIte.next();
				edgeList.add(edge);
			}
		}
	}

//	public void setAdjacencyList(Map<Node, List<Edge>> adjacencyList) {
//		this.adjacencyList = adjacencyList;
//		reset();
//	}

	public Map<Node, List<Edge>> getAdjacencyList() {
		return adjacencyList;
	}
	
	/** Adds a given edge to the adjacency list. If the base node is not yet part of the adjacency list a new entry is added */
	public void addEdge(Node key, Edge value) {

		if (adjacencyList.containsKey(key)) {
			if (adjacencyList.get(key) == null) {
				adjacencyList.put(key, new ArrayList<Edge>());
			}
			// TODO: perhaps check if a value may not be added twice.
			// add edge if not null
			if (value != null) {
				adjacencyList.get(key).add(value);
			}
		} else {
			List<Edge> edges = new ArrayList<Edge>();
			// add edge if not null
			if (value != null) {
				edges.add(value);
			}

			adjacencyList.put(key, edges);
		}

		// do bidirectional adding. Ugly duplicated code.
		// only execute when there is an edge defined.
		/*if (value != null) {
			Edge reverseEdge = new Edge(key, value.getInfo());

			if (adjacencyList.containsKey(value.getNode())) {
				if (adjacencyList.get(value.getNode()) == null) {
					adjacencyList.put(value.getNode(), new ArrayList<Edge>());
				}
				// TODO: perhaps check if a value may not be added twice.
				// add edge if not null
				if (reverseEdge != null) {
					adjacencyList.get(value.getNode()).add(reverseEdge);
				}
			} else {
				List<Edge> edges = new ArrayList<Edge>();
				// add edge if not null
				if (reverseEdge != null) {
					edges.add(reverseEdge);
				}

				adjacencyList.put(value.getNode(), edges);
			}
		}*/
	}

	/** Simply adds a new node, without setting any edges */
	public void addNode(Node node) {
		addEdge(node, null);
	}

	// CWE-389 remove removeEdge() code
    // CWE-389 remove removeNode() code

	public void clearGraph(){
		adjacencyList.clear();
	}
	
	public String toJsonString(){
		String jsonText = Bridge.graphToJson(this);
		return jsonText;
	}
	

	@Override
	public String toString() {
		return "Graph [adjacencyList=" + adjacencyList + "]";
	}

}
