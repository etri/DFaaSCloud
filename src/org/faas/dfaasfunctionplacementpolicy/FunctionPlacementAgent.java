package org.faas.dfaasfunctionplacementpolicy;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.faas.application.DFaaSFunctionInstanceInfo;
import org.faas.stats.FunctionStats;
import org.faas.stats.ResourceStates;
import org.faas.topology.FunctionPlacementAgentInfo;
import org.faas.utils.ReflectionUtil;

public abstract class FunctionPlacementAgent implements FunctionPlacementControlAgentIF {

	private FunctionPlacementControlAgentIF agent;

	// CWE-495: apply recommend
	private static List<Class> agentList;
	
	public static List<Class> getAgentList() {
		List<Class> newAgentList = new ArrayList<Class>();
		if (agentList == null) {
			agentList = new ArrayList<Class>();
			
			ServiceLoader sl = ServiceLoader.load(FunctionPlacementControlAgentIF.class);
			Iterator ite = sl.iterator();
			
			while (ite.hasNext()) {
				Object o = ite.next();
				agentList.add(o.getClass());
				newAgentList.add(o.getClass());
			}
		} else {
			for(int i = 0; i < agentList.size(); i++) {
				newAgentList.add(agentList.get(i));
			}
		}

		return newAgentList;
	}
	
	public static FunctionPlacementControlAgentIF create(FunctionPlacementAgentInfo agentInfo) {
		//dfaasfunctionplacementpolicy = (FunctionPlacementControlAgentIF)ReflectionUtil.createObject(agentInfo.getClassName(), agentInfo.getParameterValues().toArray());
		return (FunctionPlacementControlAgentIF)ReflectionUtil.createObject(agentInfo.getClassName(), agentInfo.getParameterValues().toArray());
	}
	
	public abstract int getFunctionGrade(List<ResourceStates> resourceStatesList, List<FunctionStats> functionStatsList,
			DFaaSFunctionInstanceInfo functionInfo,
			int functionGrade, String functionProfileId, String functionInstanceId,
			int cpuCoreNumber, int memorySize, double violationCost);
	
	public abstract void reportReward(DFaaSFunctionInstanceInfo functionInfo);
	
}
