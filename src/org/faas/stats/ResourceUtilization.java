package org.faas.stats;

import java.io.Serializable;

import org.faas.entities.NodeGroup;

public class ResourceUtilization implements Serializable {

	private int nodeId;
	private int nodeType;
	
	private double totalCores = 0;
	private double usedCores = 0;
	private double cpuUtilization = 0;

	private double totalRam = 0;
	private double usedRam = 0;
	private double memoryUtilization = 0;

	public ResourceUtilization(NodeGroup nodeGroup) {
		this(nodeGroup.getTpId(),nodeGroup.getType(),nodeGroup.getResourceUtilization());
	}
	
	private ResourceUtilization(int nodeId,int nodeType,NodeGroup.ResourceUtilization util) {
		this(nodeId,nodeType,util.getTotalCores(),util.getUsedCores(),util.getTotalRam(),util.getUsedRam());
	}	
	
	private ResourceUtilization(int nodeId,int nodeType,double totalCores,double usedCores,double totalRam,double usedRam) {
		this.nodeId = nodeId;
		this.nodeType = nodeType;
		
		this.totalCores = totalCores;
		this.usedCores = usedCores;
		this.cpuUtilization = usedCores/totalCores;
		
		this.totalRam = totalRam;
		this.usedRam = usedRam;
		this.memoryUtilization = usedRam/totalRam;
	}

	public int getNodeId() {
		return nodeId;
	}

	public int getNodeType() {
		return nodeType;
	}

	public double getTotalCores() {
		return totalCores;
	}

	public double getUsedCores() {
		return usedCores;
	}

	public double getCpuUtilization() {
		return cpuUtilization;
	}

	public double getTotalRam() {
		return totalRam;
	}

	public double getUsedRam() {
		return usedRam;
	}

	public double getMemoryUtilization() {
		return memoryUtilization;
	}
	
}
