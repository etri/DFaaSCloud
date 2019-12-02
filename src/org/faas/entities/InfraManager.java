package org.faas.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.faas.AnalyticsEngine;
import org.faas.SimulationConfig;
import org.faas.application.DFaaSFunctionInstanceInfo;
import org.faas.db.DatabaseLogger;
import org.faas.stats.ResourceUtilization;
import org.faas.stats.collector.MonitoringDataCollector;
import org.faas.topology.FunctionProfile;
import org.faas.topology.NetworkTopologyHelper;
import org.faas.stats.FunctionStats;
import org.faas.stats.MonitoringData;
import org.faas.stats.ResourceStates;
import org.faas.utils.DFaaSEvents;
import org.faas.utils.Logger;

public class InfraManager extends SimEntity {

	private static InfraManager me;

	private double delay = SimulationConfig.getInstance().getInfraManagerMonitoringInterval();
	//private ResourceStates resourceStates;
	// CWE-495: private -> public
	public List<ResourceStates> resourceStatesList = new ArrayList<ResourceStates>();
	
	private List<NodeGroup> nodeGroupList = new ArrayList<NodeGroup>();
	
	public static void createInstance(double delay) {
		me = new InfraManager(delay);
	}
	
	public static InfraManager getInstance() {
		return me;
	}
	
	private InfraManager(double delay) {
		super("InfraManager");
		
		this.delay = delay;
		
		List<SimEntity> entities = CloudSim.getEntityList();
		for (int i=0;i<entities.size();i++) {
			SimEntity entity = entities.get(i);
			if (entity instanceof NodeGroup) {
				nodeGroupList.add((NodeGroup)entity);
			}
		}
	}
	
	@Override
	public void startEntity() {
		send(getId(), delay, DFaaSEvents.UPDATE_RESOURCE_USAGE);
	}

	@Override
	public void processEvent(SimEvent ev) {
		Logger.debug(this,"processEvent",getName(),ev.toString());
		
		switch(ev.getTag()){
			case DFaaSEvents.UPDATE_RESOURCE_USAGE:
				processUpdateResourceUsage(ev);
				send(getId(), delay, DFaaSEvents.UPDATE_RESOURCE_USAGE);
				MonitoringDataCollector.getInstance().clock(CloudSim.clock());
				break;
			// CWE-478: add default code
			default:
				break;
		}
	}
	
	public void saveReward(DFaaSFunctionInstanceInfo instanceInfo) {
    	if (SimulationConfig.getInstance().getDbLogging()) {
    		DatabaseLogger.getInstance().insert(instanceInfo); // InfraManager 
    	}

	}
	
	private void processUpdateResourceUsage(SimEvent ev) {
		
		ResourceStates resourceStates = new ResourceStates(CloudSim.clock());

		List<ResourceUtilization> resourceUtilizationList = new ArrayList<ResourceUtilization>();
		
		for (int i=0;i<nodeGroupList.size();i++) {
			NodeGroup nodeGroup = nodeGroupList.get(i);
			int nodeGroupId = nodeGroup.getId();
			int nodeGroupType = nodeGroup.getType();
			
			// prepare resource states for each node group
			resourceStates.prepareModalityForNodeGroup(nodeGroup);
			
			// resource utilization
			resourceUtilizationList.add(new ResourceUtilization(nodeGroup));
			
			
			//Resource pool modality
			//해당 nodeGroup에서 실행 중인 DFaaSFunctionInstance들의 remaining function running duration 정보와 사용하고 있는 리소스 정보(CPU core 수)를 테이블로 관리 
			//Remaining function running duration = function running duration mean - (current simulation time - function starting time)
			
			MonitoringDataCollector.getInstance().initQueueSum(nodeGroupId);
			
			// update queue modality
			List<DFaaSFunctionInstanceInfo> functionInstancesInWaitingQueue = nodeGroup.getFunctionInstancesInWaitingQueue(); // queue modality
			for (int j=0;j<functionInstancesInWaitingQueue.size();j++) {
				DFaaSFunctionInstanceInfo functionInfo = functionInstancesInWaitingQueue.get(j);
				
				// TODO U: how to calc expectedFunctionRunningDuration? mean or average?
				int type = functionInfo.getNodeGroupType();
				String functionProfileId = functionInfo.getFunctionProfileId();
				FunctionStats functionStats = AnalyticsEngine.getInstance().getFunctionStat(functionProfileId,type);
				
				if (functionStats == null) continue; // issue #40
				
				double expectedFunctionRunningDuration = functionStats.getFunctionRunningDurationStat().getMean();

				resourceStates.addQueueModality(nodeGroupId, expectedFunctionRunningDuration, functionInfo.getFunctionUseCPUCoreSize());

				MonitoringDataCollector.getInstance().updateWaitingQueueSum(nodeGroupId, functionInfo.getFunctionGrade());
			}
			
			List<DFaaSFunctionInstanceInfo> functionInstancesInRunningQueue = nodeGroup.getFunctionInstancesInRunningQueue();
			
			// update resource pool modality
			double currentSimulationTime = CloudSim.clock();
			for (int j=0;j<functionInstancesInRunningQueue.size();j++) {
				DFaaSFunctionInstanceInfo functionInfo = functionInstancesInRunningQueue.get(j);
				
				int type = functionInfo.getNodeGroupType();
				String functionProfileId = functionInfo.getFunctionProfileId();
				FunctionStats functionStats = AnalyticsEngine.getInstance().getFunctionStat(functionProfileId,type);
				
				if (functionStats == null) continue;
				
				double functionRunningDurationMean = functionStats.getFunctionRunningDurationStat().getMean();
				
				double functionStartingTime = functionInfo.getFunctionStartingTime();
				double expectedRemainingFunctionRunningDuration = functionRunningDurationMean - (currentSimulationTime - functionStartingTime);

				int functionUseCpuCoreSize = functionInfo.getFunctionUseCPUCoreSize();
				
				if (functionStats == null) {
//					resourceStates.addResourcePoolModality(nodeGroupId, ResourceStatsData.UNKNOWN_LEVEL, functionUseCpuCoreSize);
//					resourceStates.updateQueueUsageState(nodeGroupId, ResourceStatsData.UNKNOWN_LEVEL, functionUseCpuCoreSize);
				} else {

					resourceStates.addResourcePoolModality(nodeGroupId, expectedRemainingFunctionRunningDuration, functionUseCpuCoreSize);
				}
				
				functionInfo.setSnapShotId(resourceStates.getSnapShotId());
				
				MonitoringDataCollector.getInstance().updateRunningQueueSum(nodeGroupId, functionInfo.getFunctionGrade());
			}
		}
		
		MonitoringDataCollector.getInstance().setResourceStates(resourceStates);
		MonitoringDataCollector.getInstance().setResourceUtilization(resourceUtilizationList);
		
		// Issue #72 3.4 가장 최근 항목만 유지.
		resourceStatesList.clear();
		resourceStatesList.add(resourceStates);
//		resourceStatesList.add(resourceStates);
//		if (resourceStatesList.size()>10) {
//			resourceStatesList.remove(0);
//		}
		String log = resourceStates.toPrintString();
		if (log.length()>0) {
			Logger.info("InfraManager", "processUpdateResourceUsage", log);
		}
		
		MonitoringData.refresh();

    	if (SimulationConfig.getInstance().getDbLogging()) {
		
    		// logging functionStats
			List<FunctionStats> functionStatsList = AnalyticsEngine.getInstance().getFunctionStats();
			Map<String,List<FunctionStats>> map = new HashMap<String,List<FunctionStats>>();
			
			for (int i=0;i<functionStatsList.size();i++) {
				String functionProfileId = functionStatsList.get(i).getFunctionProfileId();
				map.put(functionProfileId,new ArrayList<FunctionStats>());
			}
			for (int i=0;i<functionStatsList.size();i++) {
				FunctionStats functionStats = functionStatsList.get(i);
				String functionProfileId = functionStats.getFunctionProfileId();
				List<FunctionStats> list = map.get(functionProfileId);
				list.add(functionStats);
			}

			Iterator<String> ite = map.keySet().iterator();
			while (ite.hasNext()) {
				String profileId = ite.next();
				FunctionProfile functionProfile = NetworkTopologyHelper.getInstance().getFunctionProfile(profileId);
				DatabaseLogger.getInstance().insert(map.get(profileId),functionProfile); // InfraManager 
			}
			
			// logging resourceStatesList
			DatabaseLogger.getInstance().insert(resourceStatesList);
    	}

	}

	@Override
	public void shutdownEntity() {
		// TODO Auto-generated method stub
		
	}
	
	public List<ResourceStates> getResourceStates() {
		return resourceStatesList;
	}
	
	
}
