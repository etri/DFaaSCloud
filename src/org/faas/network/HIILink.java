package org.faas.network;

public class HIILink {
	
	private HIINode sourceHIINode;
	private HIINode destHIINode;
	
	private double networkingUnitCost;
	private double bw;
	private double delay; // hop delay + propagation delay
	
	public HIINode getSourceHIINode() {
		return sourceHIINode;
	}
	public void setSourceHIINode(HIINode sourceHIINode) {
		this.sourceHIINode = sourceHIINode;
	}
	public HIINode getDestHIINode() {
		return destHIINode;
	}
	public void setDestHIINode(HIINode destHIINode) {
		this.destHIINode = destHIINode;
	}
	public double getNetworkingUnitCost() {
		return networkingUnitCost;
	}
	public void setNetworkingUnitCost(double networkingUnitCost) {
		this.networkingUnitCost = networkingUnitCost;
	}
	public double getBw() {
		return bw;
	}
	public void setBw(double bw) {
		this.bw = bw;
	}
	public double getDelay() {
		return delay;
	}
	public void setDelay(double delay) {
		this.delay = delay;
	}
	
	public String toString() {
		return "HIILink:"+sourceHIINode.getId()+"->"+destHIINode.getId();
	}
	
}
