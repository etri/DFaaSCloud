package org.faas.rmi;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import org.faas.gui.DFaaSGui;
import org.faas.gui.panel.AnalyticsEngineStatPanel;
import org.faas.network.HIINetwork;
import org.faas.rmi.DFaaSSimulator;
import org.faas.rmi.SimulatorClientIF;
import org.faas.stats.FunctionStats;
import org.faas.stats.MonitoringData;
import org.faas.stats.ResourceStates;
import org.faas.stats.ResourceUtilization;

public class SimulatorServer implements SimulatorServerIF {
	
	@Override
	public void clock(double clock) throws RemoteException  {
		MonitoringData.getCurrentData().clock(clock);
	}
	
	@Override
	public void increaseRequestedFunctionCount(double clock) throws RemoteException {
		MonitoringData.getCurrentData().increaseRequestedFunctionCount(clock);
	}

	@Override
	public void increaseViolationFunctionCount() throws RemoteException {
		MonitoringData.getCurrentData().increaseViolationFunctionCount();
	}

	@Override
	public void increaseFinishedFunctionCount(double totalCost) {
		MonitoringData.getCurrentData().increaseFinishedFunctionCount(totalCost);
	}

	@Override
	public void initData() {
		MonitoringData.getCurrentData().initData();
	}

	@Override
	public void registerSimulator(SimulatorClientIF simulator) {
		DFaaSSimulator.createRemote(simulator);
	}
	
	@Override
	public void unregisterSimulator(SimulatorClientIF simulator) throws RemoteException {
		DFaaSGui.getMe().isRunning = false;
	}
	
	@Override
	public void setIdMap(Map<Integer, Integer> idMap) {
		HIINetwork.getInstance().setIdMap(idMap);
	}

	@Override
	public void reportAnalyticsEngineResult(List<FunctionStats> functionStatsList) {
		AnalyticsEngineStatPanel.getInstance().show(functionStatsList);
	}

	//

	@Override
	public void updateWaitingQueueSum(int nodeGroupId, int grade) {
		MonitoringData.getCurrentData().updateWaitingQueueSum(nodeGroupId, grade);
	}

	@Override
	public void updateRunningQueueSum(int nodeGroupId, int grade) {
		MonitoringData.getCurrentData().updateRunningQueueSum(nodeGroupId, grade);
	}

	@Override
	public void initQueueSum(int nodeGroupId) {
		MonitoringData.getCurrentData().initQueueSum(nodeGroupId);
	}

	@Override
	public void updateFinishedQueueSum(int nodeGroupId, int grade) {
		MonitoringData.getCurrentData().updateFinishedQueueSum(nodeGroupId, grade);
	}

	@Override
	public void setResourceStates(ResourceStates resourceStates) {
		MonitoringData.getCurrentData().setResourceStates(resourceStates);
	}

	@Override
	public void setResourceUtilization(List<ResourceUtilization> resourceUtilizationList) {
		MonitoringData.getCurrentData().setResourceUtilization(resourceUtilizationList);
	}

}
