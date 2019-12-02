package org.faas.network;

import org.cloudbus.cloudsim.core.SimEntity;
import org.faas.DFaaSConstants;
import org.faas.entities.EventSink;
import org.faas.entities.EventSource;
import org.faas.entities.NodeGroup;

public class HIINode {
	
	private HIINode northNode;
	private SimEntity node;
	
	public HIINode(SimEntity node) {
		this.node = node;
	}
	
	public HIINode getNorthNode() {
		return northNode;
	}
	public void setNorthNode(HIINode northNode) {
		this.northNode = northNode;
	}
	public SimEntity getNode() {
		return node;
	}
	public void setNode(SimEntity node) {
		this.node = node;
	}
	public void setParentId(int parentId) {
		if (node instanceof EventSource) {
			((EventSource)node).setParentId(parentId);
		} else if (node instanceof EventSink) {
			((EventSink)node).setParentId(parentId);
		}
	}
	public int getId() {
		return node.getId();
	}
	public int getTpId() {
		int tpId = -1;
		if (node instanceof EventSource) {
			tpId = ((EventSource)node).getTpId();
		} else if (node instanceof EventSink) {
			tpId = ((EventSink)node).getTpId();
		} else if (node instanceof NodeGroup) {
			tpId = ((NodeGroup)node).getTpId();
		}
		return tpId;
	}
	
	public int getType() {
		if (node instanceof NodeGroup) {
			return ((NodeGroup)node).getType();
		} else if (node instanceof EventSource) {
			return DFaaSConstants.SENSOR;
		}
		return DFaaSConstants.ACTUATOR;
	}
	
}
