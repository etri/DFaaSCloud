package org.faas.application;

import org.cloudbus.cloudsim.core.CloudSim;
import org.faas.AnalyticsEngine;
import org.faas.stats.collector.MonitoringDataCollector;

public class DFaaSFunctionReceiver {

	public void processFunctionResponse(DFaaSFunctionRspMsg rspMsg) {
		DFaaSFunctionInstanceInfo functionInfo = rspMsg.getFunctionInfo();
		
		calcFunctionCompletionTime(functionInfo);
		calcDelayViolation(functionInfo);
		calcReward(functionInfo);
		
		AnalyticsEngine.getInstance().updteFunctionStats(functionInfo);

		functionInfo.getDfaasFunctionScheduler().reportReward(functionInfo); // INP Controller 한테 요청해서 처리 하도록 변경.
		
		MonitoringDataCollector.getInstance().increaseFinishedFunctionCount(functionInfo.getTotalCost());
		
		// Issue #82 increaseFinishedFunctionCount 이후로 이동.
		if (functionInfo.isDelayViolation()) {
			MonitoringDataCollector.getInstance().increaseViolationFunctionCount();
		}
		
	}
	
	private void calcFunctionCompletionTime(DFaaSFunctionInstanceInfo functionInfo) {
		functionInfo.setFunctionCompletionTime(CloudSim.clock());
		
	}
	
	private void calcDelayViolation(DFaaSFunctionInstanceInfo functionInfo) {
		double completeTime = functionInfo.getFunctionCompletionTime();
		double requestTime = functionInfo.getFunctionRequestTime();
		double maxDuration = functionInfo.getMaximumCompletionDuration();
		
		if (completeTime - requestTime > maxDuration) {
			functionInfo.setDelayViolation(true);
		} else {
			functionInfo.setDelayViolation(false);
		}
	}
	
	private void calcReward(DFaaSFunctionInstanceInfo functionInfo) {
		double violationCost = 0;
		if (functionInfo.getFunctionCompletionTime() - functionInfo.getFunctionRequestTime() > functionInfo.getMaximumCompletionDuration()) {
			violationCost = functionInfo.getViolationUnitCost();
		} else {
			violationCost = 0;
		}
		functionInfo.setViolationCost(violationCost);
		functionInfo.setTotalCost(functionInfo.getViolationCost()+functionInfo.getComputingCost()+functionInfo.getInputDataNetworkingCost()+functionInfo.getOutputDataNetworkingCost());
	}
	
}
