package org.faas.entities;

import java.util.List;

import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.faas.AnalyticsEngine;
import org.faas.DFaaSConstants;
import org.faas.dfaasfunctionplacementpolicy.FunctionPlacementControlAgentIF;
import org.faas.application.DFaaSFunctionInstanceInfo;
import org.faas.application.DFaaSFunctionReqMsg;
import org.faas.application.DataLocationAndSize;
import org.faas.network.HIINetwork;
import org.faas.stats.FunctionStats;
import org.faas.stats.ResourceStates;
import org.faas.utils.DFaaSEvents;
import org.faas.utils.Logger;

public class DFaaSFunctionScheduler extends SimEntity {

	private static double decisionTime = 1;
	
	private int coreNodeGroupId;
	private int edgeNodeGroupId;
	private int fogNodeGroupId;
	
	FunctionPlacementControlAgentIF functionPlacementAgent;

	public static double getDecisionTime() {
		return decisionTime;
	}
	
	public static void setDecisionTime(double time) {
		decisionTime = time;
	}
	
	public void setFunctionPlacementAgent(FunctionPlacementControlAgentIF functionPlacementAgent) {
		this.functionPlacementAgent = functionPlacementAgent;
	}
	
	public DFaaSFunctionScheduler(int id, int coreNodeGroupId, int edgeNodeGroupId, int fogNodeGroupId) {
		super("DFaaSFunctionScheduler-"+id);
		
		super.setId(id);
		
		this.coreNodeGroupId = coreNodeGroupId;
		this.edgeNodeGroupId = edgeNodeGroupId;
		this.fogNodeGroupId = fogNodeGroupId;
	}
	
	@Override
	public void startEntity() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processEvent(SimEvent ev) {
		
		Logger.debug(this,"processEvent",getName(),ev.toString());
		
		switch(ev.getTag()){
			case DFaaSEvents.INP_FUNCTION_REQ_ARRIVAL:
				processInpFunctionReqArrival(ev);
				break;
			// CWE-478: add default code
			default:
				break;
		}		
	}

	@Override
	public void shutdownEntity() {
		// TODO Auto-generated method stub
		
	}
	
	public void reportReward(DFaaSFunctionInstanceInfo instanceInfo) {
		functionPlacementAgent.reportReward(instanceInfo);
		InfraManager.getInstance().saveReward(instanceInfo);
	}

	private int convertTypeToId(int type) {
		return convertTypeToId(type,null);
	}
	private int convertTypeToId(int type,DFaaSFunctionReqMsg reqMsg) {
		int id = -1;
		if (type == DFaaSConstants.CORE_NODE_GROUP) {
			id = coreNodeGroupId;
		} else if (type == DFaaSConstants.EDGE_NODE_GROUP) {
			id = edgeNodeGroupId;
		} else if (type == DFaaSConstants.FOG_NODE_GROUP) {
			id = fogNodeGroupId;
		} else if (type == DFaaSConstants.END_EDVICE_GROUP) {
			if (reqMsg != null) {
				id = reqMsg.getEventSource().getActuatorId();
			} else {
				System.out.println(this.getClass().getName()+".convertTypeToId this can NOT happend.");
			}
		} else {
			System.out.println(this.getClass().getName()+".convertTypeToId this can NOT happend : unknown group type"+type);
		}
		return id;
	}
	
	private void processInpFunctionReqArrival(SimEvent ev) {
		DFaaSFunctionReqMsg reqMsg = (DFaaSFunctionReqMsg)ev.getData();
		DFaaSFunctionInstanceInfo functionInfo = reqMsg.getFunctionInfo();
		functionInfo.setDfaasFunctionScheduler(this);
		
		// function stats
		List<FunctionStats> functionStatsList = AnalyticsEngine.getInstance().getFunctionStats();
		
		// resource states
		List<ResourceStates> resourceStatesList = InfraManager.getInstance().getResourceStates();
		
		//int grade = FunctionPlacementControlAgentFactory.getAgent().getFunctionGrade(
		int grade = functionPlacementAgent.getFunctionGrade(
				resourceStatesList
				, functionStatsList
				, functionInfo
				, functionInfo.getFunctionGrade()
				, functionInfo.getFunctionProfileId()
				, functionInfo.getFunctionInstanceId()
				, functionInfo.getFunctionUseCPUCoreSize()
				, functionInfo.getFunctionUseMemorySize()
				, functionInfo.getViolationUnitCost());
	
		functionInfo.setNodeGroupType(grade);
		
		int destNodeGroupId = convertTypeToId(grade);
		
		List<DataLocationAndSize> inputDataList = functionInfo.getInputDataList();
		for (int i=0;i<inputDataList.size();i++) {
			DataLocationAndSize dataLocAndSize = inputDataList.get(i);
			dataLocAndSize.setNodeGroupId(convertTypeToId(dataLocAndSize.getType(),reqMsg));
		}
		
		List<DataLocationAndSize> outputDataList = functionInfo.getOutputDataList();
		for (int i=0;i<outputDataList.size();i++) {
			DataLocationAndSize dataLocAndSize = outputDataList.get(i);
			dataLocAndSize.setNodeGroupId(convertTypeToId(dataLocAndSize.getType(),reqMsg));
		}
		
		double delay = calcNetworkDelay(destNodeGroupId,reqMsg);
		
		// send event
		send(destNodeGroupId, delay, DFaaSEvents.NG_FUNCTION_REQ_ARRIVAL,reqMsg);

	}
	
	private double calcNetworkDelay(int destNodeGroupId,DFaaSFunctionReqMsg reqMsg) {
		int sourceId = this.fogNodeGroupId;
		
		double delay = HIINetwork.getInstance().calcNetworkDelay2(reqMsg.getMessageSize(), sourceId, destNodeGroupId);
		
		delay = delay + DFaaSFunctionScheduler.getDecisionTime();
		
		return delay;
	}
	
}
