package org.faas.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import org.faas.rmi.SimulatorClientIF;
import org.faas.stats.FunctionStats;
import org.faas.stats.ResourceStates;
import org.faas.stats.ResourceUtilization;
import org.faas.stats.collector.MonitoringDataReceiverIF;

public interface SimulatorServerIF extends Remote, MonitoringDataReceiverIF {
	
	public void reportAnalyticsEngineResult(List<FunctionStats> functionStatsList) throws RemoteException;
	
	public void registerSimulator(SimulatorClientIF simulator) throws RemoteException;
	public void unregisterSimulator(SimulatorClientIF simulator) throws RemoteException;
	
	public void setIdMap(Map<Integer,Integer> idMap) throws RemoteException;
}
