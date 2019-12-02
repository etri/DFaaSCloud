package org.faas.application;

public class DataLocationAndSize {
	private int type; // node group type
	private double size;
	private int nodeGroupId; // node group id
	
	public DataLocationAndSize(int type,double size) {
		this.type = type;
		this.size = size;
	}

	public int getType() {
		return type;
	}

	public double getSize() {
		return size;
	}

	public int getNodeGroupId() {
		return nodeGroupId;
	}

	public void setNodeGroupId(int nodeGroupId) {
		this.nodeGroupId = nodeGroupId;
	}
	
}
