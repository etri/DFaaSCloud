package org.faas.stats.collector;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;

import org.faas.network.HIINetwork;
import org.faas.rmi.SimulatorClientIF;
import org.faas.stats.FunctionStats;
import org.faas.stats.MonitoringData;
import org.faas.stats.ResourceStates;
import org.faas.stats.ResourceUtilization;
import org.faas.utils.Logger;

public class MonitoringDataCollector {
	private static MonitoringDataCollector me;
	
	private MonitoringDataReceiverIF receiver;
	
	private MonitoringDataCollector(MonitoringDataReceiverIF receiver) {
		this.receiver = receiver;
	}
	
	public static MonitoringDataCollector getInstance() {
		return me;
	}
	
	public static void useEmpty() {
		me = new MonitoringDataCollector(null);
	}

	public static void useLocal() {
		me = new MonitoringDataCollector(new MonitoringData());
	}
	
	public static void useRemote(MonitoringDataReceiverIF receiver) {
		me = new MonitoringDataCollector(receiver);
	}
	
	public void init() {
		if (receiver == null) return;
		try {
			receiver.initData();
		} catch (Exception e) {
			// CWE-209 add code
			Logger.error("MonitoringDataCollector:init","Exception: " + e);
			//e.printStackTrace();
		}
	}
	
	public void clock(double clock) {
		if (receiver == null) return;
		try {
			receiver.clock(clock);
		} catch (Exception e) {
			// CWE-209 add code
			Logger.error("MonitoringDataCollector:clock","Exception: " + e);
			//e.printStackTrace();
		}
	}
	
	public void increaseRequestedFunctionCount(double clock) {
		if (receiver == null) return;
		try {
			receiver.increaseRequestedFunctionCount(clock);
		} catch (Exception e) {
			// CWE-209 add code
			Logger.error("MonitoringDataCollector:increaseRequestedFunctionCount","Exception: " + e);
			//e.printStackTrace();
		}
	}
	
	public void increaseViolationFunctionCount() {
		if (receiver == null) return;
		try {
			receiver.increaseViolationFunctionCount();
		} catch (Exception e) {
			// CWE-209 add code
			Logger.error("MonitoringDataCollector:increaseViolationFunctionCount","Exception: " + e);
			//e.printStackTrace();
		}
	}
	
	public void increaseFinishedFunctionCount(double totalCost) {
		if (receiver == null) return;
		try {
			receiver.increaseFinishedFunctionCount(totalCost);
		} catch (Exception e) {
			// CWE-209 add code
			Logger.error("MonitoringDataCollector:increaseFinishedFunctionCount","Exception: " + e);
			//e.printStackTrace();
		}
	}
	
	public void updateWaitingQueueSum(int nodeGroupId, int grade) {
		if (receiver == null) return;
		try {
			receiver.updateWaitingQueueSum(nodeGroupId, grade);
		} catch (Exception e) {
			// CWE-209 add code
			Logger.error("MonitoringDataCollector:updateWaitingQueueSum","Exception: " + e);
			//e.printStackTrace();
		}
	}
	
	public void updateRunningQueueSum(int nodeGroupId, int grade) {
		if (receiver == null) return;
		try {
			receiver.updateRunningQueueSum(nodeGroupId, grade);
		} catch (Exception e) {
			// CWE-209 add code
			Logger.error("MonitoringDataCollector:updateRunningQueueSum","Exception: " + e);
			//e.printStackTrace();
		}
	}
	
	public void initQueueSum(int nodeGroupId) {
		if (receiver == null) return;
		try {
			receiver.initQueueSum(nodeGroupId);
		} catch (Exception e) {
			// CWE-209 add code
			Logger.error("MonitoringDataCollector:initQueueSum","Exception: " + e);
			//e.printStackTrace();
		}
	}
	
	public void updateFinishedQueueSum(int nodeGroupId, int grade) {
		if (receiver == null) return;
		try {
			receiver.updateFinishedQueueSum(nodeGroupId, grade);
		} catch (Exception e) {
			// CWE-209 add code
			Logger.error("MonitoringDataCollector:updateFinishedQueueSum","Exception: " + e);
			//e.printStackTrace();
		}
	}
	
	public void setResourceStates(ResourceStates resourceStates) {
		if (receiver == null) return;
		try {
			receiver.setResourceStates(resourceStates);
		} catch (Exception e) {
			// CWE-209 add code
			Logger.error("MonitoringDataCollector:setResourceStates","Exception: " + e);
			//e.printStackTrace();
		}
	}
	
	public void setResourceUtilization(List<ResourceUtilization> resourceUtilizationList) {
		if (receiver == null) return;
		try {
			receiver.setResourceUtilization(resourceUtilizationList);
		} catch (Exception e) {
			// CWE-209 add code
			Logger.error("MonitoringDataCollector:setResourceUtilization","Exception: " + e);
			//e.printStackTrace();
		}
	}
	
}
