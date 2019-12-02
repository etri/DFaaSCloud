package org.faas.topology;

public class Link implements Cloneable {
	private int sourceId;
	private int destId;
	
	private double networkingUnitCost;
	private double bw;
	private double delay; // propagation delay + hop delay

	// CWE-580, CWE-491 implement super.clone()
	@Override
	final public Link clone() throws CloneNotSupportedException {
		Link clone = (Link) super.clone();
		clone.sourceId = sourceId;
		clone.destId = destId;

		clone.networkingUnitCost = networkingUnitCost;
		clone.bw = bw;
		clone.delay = delay;

		return clone;
	}
	public int getSourceId() {
		return sourceId;
	}
	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}
	public int getDestId() {
		return destId;
	}
	public void setDestId(int destId) {
		this.destId = destId;
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
	
	public boolean equals(Link link) {
		if (this.sourceId == link.sourceId && this.destId == link.destId) {
			return true;
		}
		return false;
	}

	// CWE-581 add hashCode()
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
