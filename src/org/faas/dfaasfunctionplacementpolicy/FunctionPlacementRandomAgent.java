package org.faas.dfaasfunctionplacementpolicy;

import java.util.List;

import org.faas.DFaaSConstants;
import org.faas.application.DFaaSFunctionInstanceInfo;
import org.faas.stats.FunctionStats;
import org.faas.stats.ResourceStates;
import org.faas.utils.distribution.LocationDistribution;

public class FunctionPlacementRandomAgent extends FunctionPlacementAgent {
	
	private LocationDistribution locationDist;
	
	public FunctionPlacementRandomAgent() {
		locationDist = new LocationDistribution();
        locationDist.addNumber(DFaaSConstants.CORE_NODE_GROUP, 0.33);
        locationDist.addNumber(DFaaSConstants.EDGE_NODE_GROUP, 0.33);
        locationDist.addNumber(DFaaSConstants.FOG_NODE_GROUP, 0.33);

	}
	
	public void reportReward(DFaaSFunctionInstanceInfo functionInfo) {
		//System.out.println(this.getClass().getName()+".reportReward:");
	}
	public int getFunctionGrade(List<ResourceStates> resourceStatesList, List<FunctionStats> functionStatsList
			, DFaaSFunctionInstanceInfo functionInfo
			, int functionGrade
			, String functionProfileId, String functionInstanceId, int cpuCoreNumber, int memorySize, double violationCost) {
		
		int location = (int)locationDist.sample();
		//System.out.println(this.getClass().getName()+".getFunctionGrade: location="+location);

		return location;
	
	}
}
