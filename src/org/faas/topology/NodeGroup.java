package org.faas.topology;

public class NodeGroup implements NodeIF {

	private int id;
	private int type;

	private int mips;
	private int ram;
	private double ratePerMips;
	
	private double computingUnitCost;

	private int numberOfCpuCores=5;
	
	private int queueSize;
	
	private FunctionPlacementAgentInfo functionPlacementAgentInfo;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getMips() {
		return mips;
	}

	public void setMips(int mips) {
		this.mips = mips;
	}

	public int getRam() {
		return ram;
	}

	public void setRam(int ram) {
		this.ram = ram;
	}

	public double getRatePerMips() {
		return ratePerMips;
	}

	public void setRatePerMips(double ratePerMips) {
		this.ratePerMips = ratePerMips;
	}

	public double getComputingUnitCost() {
		return computingUnitCost;
	}

	public void setComputingUnitCost(double computingUnitCost) {
		this.computingUnitCost = computingUnitCost;
	}

	public int getNumberOfCpuCores() {
		return numberOfCpuCores;
	}

	public void setNumberOfCpuCores(int numberOfCpuCores) {
		this.numberOfCpuCores = numberOfCpuCores;
	}

	public FunctionPlacementAgentInfo getFunctionPlacementAgentInfo() {
		return functionPlacementAgentInfo;
	}

	public void setFunctionPlacementAgentInfo(FunctionPlacementAgentInfo functionPlacementAgentInfo) {
		this.functionPlacementAgentInfo = functionPlacementAgentInfo;
	}

	public int getQueueSize() {
		return queueSize;
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}
	
}
