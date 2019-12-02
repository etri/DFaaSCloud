package org.faas.gui.core;

import java.util.ArrayList;
import java.util.List;

public class ElementSelectionHistory {
	// CWE-495 private -> public
	public List<Node> nodeHistory = new ArrayList<Node>();
	private static int QUEUE_SIZE = 5;
	
	public void nodeSelected(Node node) {
		nodeHistory.add(0, node);
		
		if (nodeHistory.size()>QUEUE_SIZE) {
			nodeHistory.remove(nodeHistory.size()-1);
		}
	}
	
	public List<Node> getNodes() {
		return nodeHistory;
	}
	
	public void linkSelected() {
		
	}
	
	public void clear() {
		nodeHistory.clear();
	}
	
}
