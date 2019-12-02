package org.faas.stats;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.faas.gui.panel.MonitoringPanel;
import org.faas.stats.collector.MonitoringDataReceiverIF;
public class MonitoringData implements MonitoringDataReceiverIF {

	private double simulationTime;
	private long requestedFunctionCount;
	private long finishedFunctionCount;
	private long violationFunctionCount;
	private double totalCost;

	// CWE-495 private -> public
	// node group id , NodeGroupQueueSum
	public Map<Integer,NodeGroupQueueSum> nodeGroupQueueSumMap = new HashMap<Integer,NodeGroupQueueSum>();
	private Map<Integer,QueueResourcePoolStats> resourceStatsMap = new HashMap<Integer,QueueResourcePoolStats>();

	private List<ResourceUtilization> resourceUtilizationList = new ArrayList<ResourceUtilization>(); 
	// CWE-500 public -> private
	private static MonitoringData currentData;
	// CWE-495 apply recommend
	private static List<MonitoringData> list = new ArrayList<MonitoringData>();
	
	private static int size = 2;

	public static MonitoringData getCurrentData() { return currentData; }

	public double getSimulationTime() {
		return simulationTime;
	}

	public long getRequestedFunctionCount() {
		return requestedFunctionCount;
	}

	public long getFinishedFunctionCount() {
		return finishedFunctionCount;
	}

	public long getViolationFunctionCount() {
		return violationFunctionCount;
	}
	
	public double getTotalCost() {
		return totalCost; 
	}

	public Map<Integer, NodeGroupQueueSum> getNodeGroupQueueSumMap() {
		return nodeGroupQueueSumMap;
	}
	
	public synchronized Map<Integer,QueueResourcePoolStats> getResourceStatsMap() {
		Map<Integer,QueueResourcePoolStats> newMap = new HashMap<Integer,QueueResourcePoolStats>();
		newMap.putAll(resourceStatsMap);
		return newMap;
	}

	public List<ResourceUtilization> getResourceUtilizationList() {
		return new ArrayList<ResourceUtilization>(resourceUtilizationList);
	}
	
//	public static void clear() {
//		currentData = null;
//	}
	
	public static void refresh() {
		if (monitoringPanel != null) {
//			monitoringPanel.showClock();
//			monitoringPanel.requestUpdate();
		}
	}
	
	public static void refresh2() {
		if (currentData == null) return;
		
		if (monitoringPanel != null) {
			monitoringPanel.showClock(currentData.simulationTime);
			monitoringPanel.requestUpdate();
		}
	}
	
	private static MonitoringPanel monitoringPanel;

	@Override
	public void clock(double clock) {
		if (currentData == null) {
			currentData = new MonitoringData();
		}
		currentData.simulationTime = clock;
		
	}
	
	@Override
	public void increaseRequestedFunctionCount(double clock) {
		if (currentData == null) {
			currentData = new MonitoringData();
		}
		// CWE-580 else {} code removed
		currentData.simulationTime = clock;
		currentData.requestedFunctionCount++;
		
//		if (monitoringPanel != null) {
//			monitoringPanel.showClock();
//		}
	}
	
	@Override
	public void increaseFinishedFunctionCount(double totalCost) {
		currentData.finishedFunctionCount++;
		currentData.totalCost += totalCost;
		
		list.add(currentData);
//		if (monitoringPanel != null) {
//			monitoringPanel.requestUpdate();
//		}
		
		if (list.size()>size) list.remove(0);
		
	}

	// CWE-580, CWE-491 clone() code removed

	@Override
	public void increaseViolationFunctionCount() {
		currentData.violationFunctionCount++;
	}
	
	@Override
	public void initData() {
		currentData = new MonitoringData();
	}
	
	@Override
	public void updateWaitingQueueSum(int nodeGroupId, int grade) {
		NodeGroupQueueSum sum = currentData.nodeGroupQueueSumMap.get(nodeGroupId);
		sum.updateWaitingQueueSum(grade);
	}
	
	@Override
	public void updateRunningQueueSum(int nodeGroupId, int grade) {
		NodeGroupQueueSum sum = currentData.nodeGroupQueueSumMap.get(nodeGroupId);
		sum.updateRunningQueueSum(grade);
	}
	
	@Override
	public void initQueueSum(int nodeGroupId) {
		NodeGroupQueueSum oldSum = currentData.nodeGroupQueueSumMap.get(nodeGroupId);

		if (oldSum != null) {
			oldSum.initRunningQueueSumData();
			oldSum.initWaitingQueueSumData();
		} else {
			currentData.nodeGroupQueueSumMap.put(nodeGroupId, new NodeGroupQueueSum(nodeGroupId));
		}
	}
	
	@Override
	public void updateFinishedQueueSum(int nodeGroupId, int grade) {
		NodeGroupQueueSum sum = currentData.nodeGroupQueueSumMap.get(nodeGroupId);
		if (sum == null) {
			sum = new NodeGroupQueueSum(nodeGroupId);
			currentData.nodeGroupQueueSumMap.put(nodeGroupId,sum);
		}
		sum.updateFinishedQueueSum(grade);
	}
	
	@Override
	public void setResourceStates(ResourceStates resourceStates) {
		Iterator<ResourceStatsData> ite = resourceStates.getResourcePoolModalityMap().values().iterator();
		while (ite.hasNext()) {
			ResourceStatsData resourceStatsData = ite.next();
			setResourceStatsData(resourceStatsData.getNodeGroupId(),resourceStatsData.getNodeGroupType(),resourceStatsData);
		}
	}
	
	@Override
	public void setResourceUtilization(List<ResourceUtilization> resourceUtilizationList) {
//		currentData.resourceUtilizationList.clear();
//		currentData.resourceUtilizationList.addAll(resourceUtilizationList);
		currentData.resourceUtilizationList = resourceUtilizationList;
	}

	public static void setMonitoringPanel(MonitoringPanel monitoringPanel) {
		MonitoringData.monitoringPanel = monitoringPanel;
	}
	
	private void setResourceStatsData(int nodeGroupId, int nodeGroupType, ResourceStatsData resourceStatsData) {
		
		// TODO U: clone needed?
		currentData.resourceStatsMapPut(nodeGroupId, nodeGroupType, resourceStatsData);
	}
	
	private synchronized void resourceStatsMapPut(int nodeGroupId, int nodeGroupType, ResourceStatsData resourceStatsData) {
		resourceStatsMap.put(nodeGroupId, new QueueResourcePoolStats(nodeGroupId,nodeGroupType,resourceStatsData));
	}


	public static void clear() {
		currentData = new MonitoringData();
		list.clear();
	}

	public static List<MonitoringData> getList() {
		List<MonitoringData> newList = new ArrayList<MonitoringData>();
		for(int i = 0; i < list.size(); i++) {
			newList.add(list.get(i));
		}
		return newList;
	}
	
	public static class QueueResourcePoolStats {
		private int nodeGroupId;
		private int nodeGroupType;
		// CWE-495(2) private -> public
		public Map<Double,ResourcePoolModality> resourcePoolModalityMap;
		public Map<Double,QueueModality> queueModalityMap;
		
		QueueResourcePoolStats(int nodeGroupId,int nodeGroupType,ResourceStatsData resourceStatsData) {
			this.nodeGroupType = nodeGroupType;
			this.nodeGroupId = nodeGroupId;
			
			resourcePoolModalityMap = resourceStatsData.getResourcePoolModalityMap();
			queueModalityMap = resourceStatsData.getQueueModalityMap();
		}

		public int getNodeGroupId() {
			return nodeGroupId;
		}

		public int getNodeGroupType() {
			return nodeGroupType;
		}

		public Map<Double, ResourcePoolModality> getResourcePoolModalityMap() {
			return resourcePoolModalityMap;
		}

		public Map<Double, QueueModality> getQueueModalityMap() {
			return queueModalityMap;
		}

	}
	
	public static class NodeGroupQueueSum {
		private int nodeGroupId;
		private NodeGroupQueueSumData waitingQueueSumData;
		private NodeGroupQueueSumData runningQueueSumData;
		private NodeGroupQueueSumData finishedQueueSumData;
		
		NodeGroupQueueSum(int nodeGroupId) {
			this.nodeGroupId = nodeGroupId;
		}
		
		private void updateWaitingQueueSum(int grade) {
			if (waitingQueueSumData == null) {
				waitingQueueSumData = new NodeGroupQueueSumData();
			}
			waitingQueueSumData.increaseCount(grade);
		}
		
		private void updateRunningQueueSum(int grade) {
			if (runningQueueSumData == null) {
				runningQueueSumData = new NodeGroupQueueSumData();
			}
			runningQueueSumData.increaseCount(grade);
		}
		
		private void updateFinishedQueueSum(int grade) {
			if (finishedQueueSumData == null) {
				finishedQueueSumData = new NodeGroupQueueSumData();
			}
			finishedQueueSumData.increaseCount(grade);
		}
		
		public int getNodeGroupId() {
			return nodeGroupId;
		}
		
		public void initWaitingQueueSumData() {
			waitingQueueSumData = new NodeGroupQueueSumData();
		}
		
		public NodeGroupQueueSumData getWaitingQueueSumData() {
			return waitingQueueSumData;
		}

		public void initRunningQueueSumData() {
			runningQueueSumData = new NodeGroupQueueSumData();
		}
		public NodeGroupQueueSumData getRunningQueueSumData() {
			return runningQueueSumData;
		}

		public NodeGroupQueueSumData getFinishedQueueSumData() {
			return finishedQueueSumData;
		}

		// CWE-580, CWE-491 clone() code removed
	}
	
	public static class NodeGroupQueueSumData {
		private int nodeGroupId;
		// grade , count
		private  Map<Integer,Integer> gradeCountMap = new HashMap<Integer,Integer>();
		
		public int getNodeGroupId() {
			return nodeGroupId;
		}
		
		public Iterator<Integer> getGrades() {
			return gradeCountMap.keySet().iterator();
		}
		
		public int getCount(int grade) {
			return gradeCountMap.get(grade);
		}
		
		public void increaseCount(int grade) {
			int count = 0;
			if (gradeCountMap.get(grade) == null) {
				count = 1;
			} else {
				count = gradeCountMap.get(grade).intValue()+1;
			}
			gradeCountMap.put(grade, count);
		}

		// CWE-580, CWE-491 clone() code removed
	}
}
