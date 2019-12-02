package org.faas.dfaasfunctionplacementpolicy;

import java.util.List;

import org.faas.application.DFaaSFunctionInstanceInfo;
import org.faas.stats.FunctionStats;
import org.faas.stats.ResourceStates;

// ppt page 13.

public interface FunctionPlacementControlAgentIF {
	public int getFunctionGrade(List<ResourceStates> resourceStatesList, List<FunctionStats> functionStatsList,
			DFaaSFunctionInstanceInfo functionInfo,
			int functionGrade, String functionProfileId, String functionInstanceId,
			int cpuCoreNumber, int memorySize, double violationCost);
	public void reportReward(DFaaSFunctionInstanceInfo functionInfo);
}
