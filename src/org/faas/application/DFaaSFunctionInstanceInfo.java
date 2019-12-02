package org.faas.application;

import java.util.List;

import org.faas.entities.DFaaSFunctionScheduler;
import org.faas.topology.FunctionProfile;
import org.faas.utils.Util;

/**
 * ppt page 12.
 * 
 *
 */
public class DFaaSFunctionInstanceInfo {
	
	private int nodeGroupType; // function instance 가 실행된 node group 의 type
	
	private String functionProfileId;
	private String functionInstanceId;
	private int functionGrade; // function instance 가 실행 요청된 node group type. 
	private int maximumCompletionDuration;
	private double functionRequestTime;
	
	private double functionCompletionTime; // CloudSim.clock()
	private double functionStartingTime;
	private double functionRunningDuration;
	private double functionProcessingDuration;
	
	private boolean delayViolation;// (Yes or No)
	
	private int functionUseCPUCoreSize; // function 실행에 필요한 core 개
	private int functionUseMemorySize; // function 실행에 필요한 memory 크
	private int functionUseProcessingMIsize; // function 실행에 필요한 MIPs
	
	private double inputDataReadingDuration;
	private double outputDataWritingDuration;

	// CWE-495(2), CWE-496(2) : private -> public
	public List<DataLocationAndSize> inputDataList;
	public List<DataLocationAndSize> outputDataList;
	
	private double functionRequestMessageSize;
	private double functionResponseMessageSize;
	
	private int sensorId;
	private int actuatorId;
	
	private double violationUnitCost;
	private long snapShotId; // InfraManage 에 의해 발한 snapShotId
	
	private double totalCost;
	private double violationCost;
	private double computingCost;
	private double inputDataNetworkingCost;
	private double outputDataNetworkingCost;
	
	private DFaaSFunctionScheduler dfaasFunctionScheduler;
	
	
	public DFaaSFunctionInstanceInfo(FunctionProfile functionProfile) {
		this.functionProfileId = functionProfile.getFunctionProfileId();
		this.functionInstanceId = Util.getUUID();//+"-"+System.currentTimeMillis();
		this.functionGrade = functionProfile.getFunctionGrade();
		this.maximumCompletionDuration = functionProfile.getMaximumCompletionDuration();
		
		this.functionUseCPUCoreSize = functionProfile.getComputingResourceRequirements().getNumberOfCpuCores();
		this.functionUseMemorySize = functionProfile.getComputingResourceRequirements().getMemorySize();
		
		this.violationUnitCost = functionProfile.getViolationUnitCost();
	}

	public String getFunctionInstanceId() {
		return functionInstanceId;
	}

	public List<DataLocationAndSize> getInputDataList() {
		return inputDataList;
	}

	public void setInputDataList(List<DataLocationAndSize> inputDataList) {
		this.inputDataList = inputDataList;
	}

	public List<DataLocationAndSize> getOutputDataList() {
		return outputDataList;
	}

	public void setOutputDataList(List<DataLocationAndSize> outputDataList) {
		this.outputDataList = outputDataList;
	}

	public int getSensorId() {
		return sensorId;
	}

	public void setSensorId(int sensorId) {
		this.sensorId = sensorId;
	}

	public int getActuatorId() {
		return actuatorId;
	}

	public void setActuatorId(int actuatorId) {
		this.actuatorId = actuatorId;
	}

	public int getFunctionGrade() {
		return functionGrade;
	}
	

	public double getFunctionCompletionTime() {
		return functionCompletionTime;
	}

	public void setFunctionCompletionTime(double functionCompletionTime) {
		this.functionCompletionTime = functionCompletionTime;
	}

	public double getFunctionStartingTime() {
		return functionStartingTime;
	}

	public void setFunctionStartingTime(double functionStartingTime) {
		this.functionStartingTime = functionStartingTime;
	}

	public int getFunctionUseCPUCoreSize() {
		return functionUseCPUCoreSize;
	}

	public int getFunctionUseMemorySize() {
		return functionUseMemorySize;
	}

	public int getFunctionUseProcessingMIsize() {
		return functionUseProcessingMIsize;
	}

	public String getFunctionProfileId() {
		return functionProfileId;
	}

	public double getFunctionRequestMessageSize() {
		return functionRequestMessageSize;
	}

	public double getFunctionResponseMessageSize() {
		return functionResponseMessageSize;
	}

	public void setFunctionRequestMessageSize(double functionRequestMessageSize) {
		this.functionRequestMessageSize = functionRequestMessageSize;
	}

	public void setFunctionResponseMessageSize(double functionResponseMessageSize) {
		this.functionResponseMessageSize = functionResponseMessageSize;
	}

	public int getMaximumCompletionDuration() {
		return maximumCompletionDuration;
	}

	public void setMaximumCompletionDuration(int maximumCompletionDuration) {
		this.maximumCompletionDuration = maximumCompletionDuration;
	}

	public double getFunctionRequestTime() {
		return functionRequestTime;
	}

	public void setFunctionRequestTime(double functionRequestTime) {
		this.functionRequestTime = functionRequestTime;
	}

	public double getFunctionRunningDuration() {
		return functionRunningDuration;
	}

	public void setFunctionRunningDuration(double functionRunningDuration) {
		this.functionRunningDuration = functionRunningDuration;
	}

	public double getFunctionprocessingDuration() {
		return functionProcessingDuration;
	}

	public void setFunctionProcessingDuration(double functionprocessingDuration) {
		this.functionProcessingDuration = functionprocessingDuration;
	}

	public double getInputDataReadingDuration() {
		return inputDataReadingDuration;
	}

	public void setInputDataReadingDuration(double inputDataReadingDuration) {
		this.inputDataReadingDuration = inputDataReadingDuration;
	}

	public double getOutputDataWritingDuration() {
		return outputDataWritingDuration;
	}

	public void setOutputDataWritingDuration(double outputDataWritingDuration) {
		this.outputDataWritingDuration = outputDataWritingDuration;
	}

	public boolean isDelayViolation() {
		return delayViolation;
	}

	public void setDelayViolation(boolean delayViolation) {
		this.delayViolation = delayViolation;
	}

	public double getViolationUnitCost() {
		return violationUnitCost;
	}

	public void setViolationUnitCost(int violationUnitCost) {
		this.violationUnitCost = violationUnitCost;
	}

	public long getSnapShotId() {
		return snapShotId;
	}

	public void setSnapShotId(long snapShotId) {
		this.snapShotId = snapShotId;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public double getViolationCost() {
		return violationCost;
	}

	public void setViolationCost(double violationCost) {
		this.violationCost = violationCost;
	}

	public double getComputingCost() {
		return computingCost;
	}

	public void setComputingCost(double computingCost) {
		this.computingCost = computingCost;
	}

	public double getInputDataNetworkingCost() {
		return inputDataNetworkingCost;
	}

	public void setInputDataNetworkingCost(double inputDataNetworkingCost) {
		this.inputDataNetworkingCost = inputDataNetworkingCost;
	}

	public double getOutputDataNetworkingCost() {
		return outputDataNetworkingCost;
	}

	public void setOutputDataNetworkingCost(double outputDataNetworkingCost) {
		this.outputDataNetworkingCost = outputDataNetworkingCost;
	}

	public void setFunctionProfileId(String functionProfileId) {
		this.functionProfileId = functionProfileId;
	}

	public void setFunctionInstanceId(String functionInstanceId) {
		this.functionInstanceId = functionInstanceId;
	}

	public void setFunctionGrade(int functionGrade) {
		this.functionGrade = functionGrade;
	}

	public void setFunctionUseCPUCoreSize(int functionUseCPUCoreSize) {
		this.functionUseCPUCoreSize = functionUseCPUCoreSize;
	}

	public void setFunctionUseMemorySize(int functionUseMemorySize) {
		this.functionUseMemorySize = functionUseMemorySize;
	}

	public void setFunctionUseProcessingMIsize(int functionUseProcessingMIsize) {
		this.functionUseProcessingMIsize = functionUseProcessingMIsize;
	}

	public int getNodeGroupType() {
		return nodeGroupType;
	}

	public void setNodeGroupType(int nodeGroupType) {
		this.nodeGroupType = nodeGroupType;
	}

	public DFaaSFunctionScheduler getDfaasFunctionScheduler() {
		return dfaasFunctionScheduler;
	}

	public void setDfaasFunctionScheduler(DFaaSFunctionScheduler dfaasFunctionScheduler) {
		this.dfaasFunctionScheduler = dfaasFunctionScheduler;
	}

}
