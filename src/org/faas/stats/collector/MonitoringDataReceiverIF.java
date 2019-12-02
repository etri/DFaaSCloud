package org.faas.stats.collector;

import java.rmi.RemoteException;
import java.util.List;

import org.faas.stats.ResourceStates;
import org.faas.stats.ResourceUtilization;

public interface MonitoringDataReceiverIF {
	public void initData() throws RemoteException;
	
	public void clock(double clock) throws RemoteException;
	
	public void increaseRequestedFunctionCount(double clock) throws RemoteException;
	
	public void increaseViolationFunctionCount() throws RemoteException;
	
	public void increaseFinishedFunctionCount(double totalCost) throws RemoteException;
	
	public void updateWaitingQueueSum(int nodeGroupId, int grade) throws RemoteException;
	
	public void updateRunningQueueSum(int nodeGroupId, int grade) throws RemoteException;
	
	public void initQueueSum(int nodeGroupId) throws RemoteException;
	
	public void updateFinishedQueueSum(int nodeGroupId, int grade) throws RemoteException;
	
	public void setResourceStates(ResourceStates resourceStates) throws RemoteException;
	
	public void setResourceUtilization(List<ResourceUtilization> resourceUtilizationList) throws RemoteException;
	

}
