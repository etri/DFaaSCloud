package org.faas.topology;

import java.util.List;

public class ComputingResourceRequirements {

	private int numberOfCpuCores;
	private DistributionModel requiredMipsModel;
	private int memorySize;
	
	public int getNumberOfCpuCores() {
		return numberOfCpuCores;
	}
	public void setNumberOfCpuCores(int numberOfCpuCores) {
		this.numberOfCpuCores = numberOfCpuCores;
	}
	public DistributionModel getRequiredMipsModel() {
		return requiredMipsModel;
	}
	public void setRequiredMipsModel(DistributionModel requiredMipsModel) {
		this.requiredMipsModel = requiredMipsModel;
	}
	public int getMemorySize() {
		return memorySize;
	}
	public void setMemorySize(int memorySize) {
		this.memorySize = memorySize;
	}
	
	
}
