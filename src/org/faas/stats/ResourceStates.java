package org.faas.stats;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.faas.entities.NodeGroup;

public class ResourceStates implements Serializable {

	private long snapShotId;
	private double snapShotTime;

	// CWE-495 private -> public
	public Map<Integer,ResourceStatsData> resourcePoolModalityMap = new HashMap<Integer,ResourceStatsData>();

	private static long snapShotIdSeq = 0;
	
	private static long getSnapShotSeq() {
		return ++snapShotIdSeq;
	}
	
	public ResourceStates(double snapShotTime) {
		this.snapShotTime = snapShotTime;
		this.snapShotId = getSnapShotSeq();
	}
	
	public long getSnapShotId() {
		return snapShotId;
	}
	
	public double getSnapShotTime() {
		return snapShotTime;
	}
	
	public Map<Integer,ResourceStatsData> getResourcePoolModalityMap() {
		return resourcePoolModalityMap;
	}
	
	public void prepareModalityForNodeGroup(NodeGroup nodeGroup) {
		ResourceStatsData resourceStatData;
		// TODO U:
//		resourceStatData = resourcePoolModalityMap.get(nodeGroupId);
//		if (resourceStatData == null) {
			resourceStatData = new ResourceStatsData(nodeGroup.getId(),nodeGroup.getType(),nodeGroup.getTotalCpu(),nodeGroup.getTotalMemory());
			resourcePoolModalityMap.put(nodeGroup.getId(), resourceStatData);
//		}
		
	}

//	public void prepareModalityForNodeGroup(int nodeGroupId,int nodeGroupType) {
//		ResourceStatsData resourceStatData;
//		// TODO U:
////		resourceStatData = resourcePoolModalityMap.get(nodeGroupId);
////		if (resourceStatData == null) {
//			resourceStatData = new ResourceStatsData(nodeGroupId,nodeGroupType);
//			resourcePoolModalityMap.put(nodeGroupId, resourceStatData);
////		}
//		
//	}

	public void addResourcePoolModality(int nodeGroupId, double remainingFunctionRunningDuration, int functionUseCpuCoreSize) {
		ResourceStatsData resourceStatData = resourcePoolModalityMap.get(nodeGroupId);
		resourceStatData.addResourcePoolModality(remainingFunctionRunningDuration, functionUseCpuCoreSize);
	}
	
	public void addQueueModality(int nodeGroupId, double expectedFunctionRunningDuration, int functionUseCpuCoreSize) {
		ResourceStatsData resourceStatData = resourcePoolModalityMap.get(nodeGroupId);
		resourceStatData.addQueueModality(expectedFunctionRunningDuration, functionUseCpuCoreSize);
	}
	
	public String toPrintString() {
		StringBuffer sb = new StringBuffer();
		
		Iterator<ResourceStatsData> ite = resourcePoolModalityMap.values().iterator();
		
		while (ite.hasNext()) {
			ResourceStatsData data = ite.next();
			if (data.getQueueModalityMap().size()>0 || data.getResourcePoolModalityMap().size()>0) {
				sb.append("\n"+this.getClass().getName()+".toPrintString:"+"ResourceStates").append("\n");
				sb.append("snapShotId="+snapShotId).append("\n");
				sb.append("snapShotTime="+snapShotTime).append("\n");
				
				break;
			}
		}
		
		ite = resourcePoolModalityMap.values().iterator();
		
		while (ite.hasNext()) {
			ResourceStatsData data = ite.next();
			sb.append(data.toPrintString());
		}
		
		return sb.toString();
	}
}
