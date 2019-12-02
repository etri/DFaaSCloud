package org.faas.stats.collector;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import org.faas.rmi.SimulatorClientIF;
import org.faas.rmi.SimulatorServerIF;
import org.faas.stats.FunctionStats;
import org.faas.stats.ResourceStates;
import org.faas.stats.ResourceUtilization;

public class MonitoringDataReceiverTest implements SimulatorServerIF {

	@Override
	public void initData() throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void clock(double clock) {
	}

	@Override
	public void increaseRequestedFunctionCount(double clock) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	public void increaseViolationFunctionCount() throws RemoteException {
		
	}

	@Override
	public void increaseFinishedFunctionCount(double totalCost) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateWaitingQueueSum(int nodeGroupId, int grade) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateRunningQueueSum(int nodeGroupId, int grade) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initQueueSum(int nodeGroupId) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateFinishedQueueSum(int nodeGroupId, int grade) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setResourceStates(ResourceStates resourceStates) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setResourceUtilization(List<ResourceUtilization> resourceUtilizationList) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reportAnalyticsEngineResult(List<FunctionStats> functionStatsList) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	public void registerSimulator(SimulatorClientIF simulator) throws RemoteException {
		
	}

	public void unregisterSimulator(SimulatorClientIF simulator) throws RemoteException {
		
	}

	@Override
	public void setIdMap(Map<Integer,Integer> idMap) throws RemoteException {
		
	}

}
