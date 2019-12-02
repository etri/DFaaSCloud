package org.faas.stats;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.faas.SimulationConfig;

public class ResourceStatsData implements Serializable {

	private int nodeGroupId;
	private int nodeGroupType;
	private int totalCpu; // NodeGroup 의 total cpu number.
	private int totalRam; // NodeGroup 의 total memory size.
	
	private int resourceModalitySize = SimulationConfig.getInstance().getResourceModalitySize();
	// CWE-495(2) private -> public
	public Map<Double,ResourcePoolModality> resourcePoolModalityMap = new HashMap<Double,ResourcePoolModality>();
	public Map<Double,QueueModality> queueModalityMap = new HashMap<Double,QueueModality>();

	private LimitGenerator resourcePoolModalityLimitGenerator = new LimitGenerator(SimulationConfig.getInstance().getResourceModalitySize(),SimulationConfig.getInstance().getResourceModalityGap());
	private LimitGenerator queueModalityLimitGenerator = new LimitGenerator(SimulationConfig.getInstance().getResourceModalitySize(),SimulationConfig.getInstance().getResourceModalityGap());
	
	public static final double UNKNOWN_LEVEL = Double.MAX_VALUE;

	public ResourceStatsData(int nodeGroupId,int nodeGroupType,int totalCpu,int totalRam) {
		this.nodeGroupId = nodeGroupId;
		this.nodeGroupType = nodeGroupType;
		this.totalCpu = totalCpu;
		this.totalRam = totalRam;
	}
	
	public void addResourcePoolModality(double remainingFunctionRunningDuration,int functionUseCpuCoreSize) {

		ResourcePoolModality data = prepareResourcePoolModality(remainingFunctionRunningDuration);

		data.increaseNumberOfFuctionRequests();
		data.increaseCpuCore(functionUseCpuCoreSize);

	}
	
	public void addQueueModality(double expectedFunctionRunningDuration,int functionUseCpuCoreSize) {

		QueueModality data = prepareQueueModality(expectedFunctionRunningDuration);

		data.increaseNumberOfFuctionRequests();
		data.increaseCpuCore(functionUseCpuCoreSize);
	}
	
	private ResourcePoolModality prepareResourcePoolModality(double value) {
		ResourcePoolModality data = null;
		if (value == UNKNOWN_LEVEL) {
			data = resourcePoolModalityMap.get(UNKNOWN_LEVEL);
			if (data == null) {
				resourcePoolModalityMap.put(UNKNOWN_LEVEL,new ResourcePoolModality(UNKNOWN_LEVEL));
			}
		} else {
			double key = resourcePoolModalityLimitGenerator.getLimit(value);
			
			data = resourcePoolModalityMap.get(key);
			if (data == null) {
				data = new ResourcePoolModality(key);
				resourcePoolModalityMap.put(key,data);
			}
		}
		
		return data;
	}
	
	private QueueModality prepareQueueModality(double value) {
		QueueModality data = null;
		if (value == UNKNOWN_LEVEL) {
			data = queueModalityMap.get(UNKNOWN_LEVEL);
			if (data == null) {
				queueModalityMap.put(UNKNOWN_LEVEL,new QueueModality(UNKNOWN_LEVEL));
			}
		} else {
			double key = queueModalityLimitGenerator.getLimit(value);
			
			data = queueModalityMap.get(key);
			if (data == null) {
				data = new QueueModality(key);
				queueModalityMap.put(key,data);
			}
		}		
		return data;
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
	
	public int getTotalMemory() {
		return totalRam;
	}
	
	public int getTotalCpu() {
		return totalCpu;
	}
	
	public String toPrintString() {
		
		StringBuffer sb = new StringBuffer();
		
//		if (queueModality.size()>0 || resourcePoolModalityMap.size()>0) {
//			sb.append(this.getClass().getName()+".print:"+"nodeGroupId="+nodeGroupId).append("\n");
//		}
//		
//		if (queueModality.size()>0) {
//			sb.append("Queue Modality: size="+queueModality.size()+" , function profile ids = "+queueModality).append("\n");
//			
//		}
		
		if (resourcePoolModalityMap.size()>0) {
			Iterator<ResourcePoolModality> ite = resourcePoolModalityMap.values().iterator();
			
			sb.append("Resource Pool Modality: size="+resourcePoolModalityMap.size()).append("\n");
			while (ite.hasNext()) {
				ResourcePoolModality data = ite.next();
				
				sb.append("Level="+data.getRemainingFunctionRunningDuration()+" , NumberOfCore="+data.getUsingNumberOfCore()).append("\n");
			}
		}
		
		return sb.toString();
	}
	
	public static class FunctionWaitingState implements Serializable {

		private double expectedFunctionRunningDuration;
		private int functionNumber;
		
		public FunctionWaitingState(double expectedFunctionRunningDuration) {
			this.expectedFunctionRunningDuration = expectedFunctionRunningDuration;
		}
		
		public void addFunctionNumber(int functionNumber) {
			this.functionNumber += functionNumber;
		}
		
		public int getFunctionNumber() {
			return this.functionNumber;
		}
		
		public String getExpectedFunctionRunningDurationStr() {
			return (int)expectedFunctionRunningDuration+"";
		}
	}
	
	public static class QueueUsageState implements Serializable {

		private double expectedFunctionRunningDuration;
		private int cpuCoreNumber;
		
		public QueueUsageState(double expectedFunctionRunningDuration) {
			this.expectedFunctionRunningDuration = expectedFunctionRunningDuration;
		}
		
		public void addCpuCoreNumber(int cpuCoreNumber) {
			this.cpuCoreNumber += cpuCoreNumber;
		}
		
		public int getCpuCoreNumber() {
			return this.cpuCoreNumber;
		}
		
		public String getExpectedFunctionRunningDurationStr() {
			return (int)expectedFunctionRunningDuration+"";
		}
	}

	public static class FunctionExecutionState implements Serializable {

		private double expectedRemainingFunctionRunningDuration;
		private int functionNumber;

		public FunctionExecutionState(double expectedRemainingFunctionRunningDuration) {
			this.expectedRemainingFunctionRunningDuration = expectedRemainingFunctionRunningDuration;
		}
		
		public void addFunctionNumber(int functionNumber) {
			this.functionNumber += functionNumber;
		}
		
		public int getFunctionNumber() {
			return this.functionNumber;
		}
		
		public String getExpectedRemainingFunctionRunningDurationStr() {
			return (int)expectedRemainingFunctionRunningDuration+"";
		}
		
	}
	
	public static class CpuCoreUsageState implements Serializable {

		private double expectedRemainingFunctionRunningDuration;
		private int cpuCoreNumber;
		
		public CpuCoreUsageState(double expectedRemainingFunctionRunningDuration) {
			this.expectedRemainingFunctionRunningDuration = expectedRemainingFunctionRunningDuration;
		}
		
		public void addCpuCoreNumber(int cpuCoreNumber) {
			this.cpuCoreNumber += cpuCoreNumber;
		}
		
		public int getCpuCoreNumber() {
			return this.cpuCoreNumber;
		}
		
		public String getExpectedRemainingFunctionRunningDurationStr() {
			return (int)expectedRemainingFunctionRunningDuration+"";
		}
	}
	
	public static class MemoryUsageState implements Serializable {

		private double expectedRemainingFunctionRunningDuration;
		private int memoryBlocks;
		
		public MemoryUsageState(double expectedRemainingFunctionRunningDuration) {
			this.expectedRemainingFunctionRunningDuration = expectedRemainingFunctionRunningDuration;
		}
		
		public void addMemoryBlocks(int memoryBlocks) {
			this.memoryBlocks += memoryBlocks;
		}
		
		public int getMemoryBlocks() {
			return this.memoryBlocks;
		}
		
		public String getExpectedRemainingFunctionRunningDurationStr() {
			return (int)expectedRemainingFunctionRunningDuration+"";
		}
	}
	
	public static class LimitGenerator implements Serializable {
		double modalityGap;
		int modalitySize;
		
		List<Double> limits = new ArrayList<Double>();
		
		public LimitGenerator(int modalitySize, double modalityGap) {
			this.modalityGap = modalityGap;
			this.modalitySize = modalitySize;
		
			double gap = modalityGap;
			for (int i=0;i<modalitySize;i++) {
				limits.add(gap);
				gap += modalityGap;
			}
			//System.out.println(limits);
		}
		
		public double getLimit(double value) {

			if (value> 1000) {
				//(new Exception()).printStackTrace();
			}
			double limit = -1;
			for (int i=0;i<limits.size();i++) {
				double bottom = limits.get(i);
				
				if (i==0) {
					if (value < bottom) {
						limit = bottom;
						break;
					} else {
						double top = limits.get(i+1);
						if (value < top) {
							limit = top;
							break;
						}
					}
				} else if (i == limits.size()-1) {
					limit = bottom;
					break;
				} else {
					double top = limits.get(i+1);
					if (value>=bottom && value<top) {
						limit = top;
						break;
					}
				}
				
			}
			//System.out.println(this.getClass().getName()+" : value="+value+" , limit = "+limit);
			return limit;
			
		}
		
	}
	
}
