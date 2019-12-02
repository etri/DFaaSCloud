package org.faas.gui.core;

public class FaaSGraph extends Graph {

	
	public void addEdge(Edge edge) {
		super.edgeList.add(edge);
	}
	
	public void addNode(Node node) {
		super.nodeList.add(node);
	}
	
	public Object findElement(double x,double y) {
		for (int i=0;i<edgeList.size();i++) {
			Edge edge = edgeList.get(i);
			if (edge.contains(x, y)) {
				return edge;
			}
		}
		
		for (int i=0;i<nodeList.size();i++) {
			Node node = nodeList.get(i);
			if (node.contains(x, y)) {
				return node;
			}
		}
		return null;
	}
}
