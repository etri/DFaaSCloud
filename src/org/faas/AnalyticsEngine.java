package org.faas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.faas.application.DFaaSFunctionInstanceInfo;
import org.faas.stats.FunctionStats;
import org.faas.utils.Logger;

public class AnalyticsEngine {

	private static AnalyticsEngine me;
	
	private Map<StatKey,FunctionStats> functionStatMap = new HashMap<StatKey,FunctionStats>();
	
	public static void init() {
		me = new AnalyticsEngine();
	}
	
	public static AnalyticsEngine getInstance() {
		return me;
	}
	
	private FunctionStats prepareFunctionStats(int runNodeGroupType,String functionProfileId) {
		StatKey key = new StatKey(functionProfileId,runNodeGroupType);
		FunctionStats stats = functionStatMap.get(key);
		if (stats == null) {
			stats = new FunctionStats(runNodeGroupType,functionProfileId);
			functionStatMap.put(key,stats);
		}

		return stats;
	}
	
	/**
	 * 
	 * @param info
	 * @param runNodeGroupType NodeGroup's id function actually executed on
	 */
	public void updteFunctionStats(DFaaSFunctionInstanceInfo functionInfo) {
		// ppt page 21,22
		int runNodeGroupType = functionInfo.getNodeGroupType();
		String functionProfileId = functionInfo.getFunctionProfileId();
		
		FunctionStats stats = prepareFunctionStats(runNodeGroupType, functionProfileId);
		
		stats.updateStats(functionInfo);
		
	}
	
	public void updateInputDataReadingDurationStats(DFaaSFunctionInstanceInfo functionInfo,int locationNodeGroupType,double readingDuration) {
		int runNodeGroupType = functionInfo.getNodeGroupType();
		String functionProfileId = functionInfo.getFunctionProfileId();

		FunctionStats stats = prepareFunctionStats(runNodeGroupType, functionProfileId);

		stats.updateInputDataDurationStats(functionInfo, locationNodeGroupType, readingDuration);
	}
	
	public void updateOutputDataReadingDurationStats(DFaaSFunctionInstanceInfo functionInfo,int locationNodeGroupType,double writingDuration) {
		int runNodeGroupType = functionInfo.getNodeGroupType();
		String functionProfileId = functionInfo.getFunctionProfileId();

		FunctionStats stats = prepareFunctionStats(runNodeGroupType, functionProfileId);

		stats.updateOutputDataDurationStats(functionInfo, locationNodeGroupType, writingDuration);
	}
	
	public void print() {
		boolean isEnabled = Logger.isEnabled();
		Logger.enable();

		System.out.println(this.getClass().getName()+".print:");
		List<FunctionStats> list = getFunctionStats();
		for (int i=0;i<list.size();i++) {
			String log = list.get(i).toPrintString();
			if (log.length()>0) {
				Logger.info(this.getClass().getName(), "print", log);
			}
		}

		if (isEnabled) {
			Logger.enable();
		} else {
			Logger.disable();
		}
	}
	
	public FunctionStats getFunctionStat(String functionProfileId, int nodeGroupId) {
		StatKey key = new StatKey(functionProfileId,nodeGroupId);
		
		return functionStatMap.get(key);
	}
	
	public List<FunctionStats> getFunctionStats() {
		return new ArrayList(functionStatMap.values());
	}
	
	static class StatKey {
		String functionProfileId;
		int functionExecNodeType;
		StatKey(String functionProfileId, int functionRunNodeGroupType) {
			this.functionProfileId = functionProfileId;
			this.functionExecNodeType = functionRunNodeGroupType;
					
		}
		
		@Override
	    public int hashCode() {
	        //System.out.println("hashCode");
	        final int prime = 31;
	        int result = 1;
	        result = prime * result + functionExecNodeType;
	        result = prime * result + ((functionProfileId == null) ? 0 : functionProfileId.hashCode());
	        return result;
	    }

	    @Override
	    public boolean equals(Object obj) {
	        //System.out.println("equals");
			if (obj == null)
				return false;
	        if (this.hashCode() == obj.hashCode())
	            return true;

	        if (getClass() != obj.getClass())
	            return false;
	        StatKey other = (StatKey) obj;
	        if (functionExecNodeType != other.functionExecNodeType)
	            return false;
	        if (functionProfileId == null) {
	            if (other.functionProfileId != null)
	                return false;
	        } else if (!functionProfileId.equals(other.functionProfileId))
	            return false;
	        return true;
	    }
	}
	
	public static void main(String []argv) {
		StatKey key = new StatKey("id",2);
		
		Map<StatKey,String> map = new HashMap<StatKey,String>();
		
		
		map.put(key, "A");
		map.put(new StatKey("id",2), "A");
		map.put(new StatKey("id",3), "A");
		
		System.out.println(map.size());
	}
}
