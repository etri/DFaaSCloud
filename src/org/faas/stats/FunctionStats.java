package org.faas.stats;

import java.io.Serializable;

import org.faas.DFaaSConstants;
import org.faas.application.DFaaSFunctionInstanceInfo;

public class FunctionStats implements Serializable {
	private int nodeGroupType;
	private String functionProfileId;
	
	private StatValue functionRunningDurationStat = new StatValue();
	private StatValue processingDurationStats = new StatValue();
	private StatValue completionDurationStats = new StatValue();
	private StatValue reqMessageSizeStat = new StatValue("mb");
	private StatValue rspMessageSizeStat = new StatValue("mb");

	// input data size stats
	private DataStats inputDataSizeStats = new DataStats();
	
	// output data size stats
	private DataStats outputDataSizeStats = new DataStats();
	
	private DataDurationStats inputDataDurationStats = new DataDurationStats();
	private DataDurationStats outputDataDurationStats = new DataDurationStats();
	
	public FunctionStats(int nodeGroupType, String functionProfileId) {
		this.nodeGroupType = nodeGroupType;
		this.functionProfileId = functionProfileId;
	}
	
	public void updateStats(DFaaSFunctionInstanceInfo functionInfo) {
		functionProfileId = functionInfo.getFunctionProfileId();
		
		functionRunningDurationStat.addValue(functionInfo.getFunctionRunningDuration());
		reqMessageSizeStat.addValue(functionInfo.getFunctionRequestMessageSize());
		rspMessageSizeStat.addValue(functionInfo.getFunctionResponseMessageSize());
		
		processingDurationStats.addValue(functionInfo.getFunctionprocessingDuration());
		completionDurationStats.addValue(functionInfo.getFunctionCompletionTime()-functionInfo.getFunctionRequestTime()); // TODO what's function completion duration?
		
		inputDataSizeStats.addValue(functionInfo.getInputDataList());
		outputDataSizeStats.addValue(functionInfo.getOutputDataList());
	}
	
	public void updateInputDataDurationStats(DFaaSFunctionInstanceInfo functionInfo, int nodeGroupType, double duration) {
		inputDataDurationStats.updateStats(nodeGroupType, duration);
	}

	public void updateOutputDataDurationStats(DFaaSFunctionInstanceInfo functionInfo, int nodeGroupType, double duration) {
		outputDataDurationStats.updateStats(nodeGroupType, duration);
	}

	public StatValue getFunctionRunningDurationStat() {
		return functionRunningDurationStat;
	}
	
	public String toPrintString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getClass().getName()+".toPrintString:");
		sb.append(nodeGroupType+" ("+DFaaSConstants.getEntityName(nodeGroupType)+")").append("\n");
		
		sb.append(printStatValue("functionRunningDurationStat",functionRunningDurationStat)).append("\n");
		sb.append(printStatValue("processingDurationStats",processingDurationStats)).append("\n");
		sb.append(printStatValue("completionDurationStats",completionDurationStats)).append("\n");
		
		sb.append(printStatValue("reqMessageSizeStat",reqMessageSizeStat)).append("\n");
		sb.append(printStatValue("rspMessageSizeStat",rspMessageSizeStat)).append("\n");
		
		sb.append("\n");
		
		sb.append(printDataStats("inputDataStats",inputDataSizeStats)).append("\n");
		sb.append(printDataStats("outputDataStats",outputDataSizeStats)).append("\n");
		
		return sb.toString();
	}
	
	private String printStatValue(String name,StatValue statValue) {
		
		//String s = String.format("%30s %d%7s %s %s"
		String s = String.format("%30s %s %s"
				,name
				//,nodeGroupType
				//," ("+DFaaSConstants.NODE_NAMES[nodeGroupType]+")"
				,functionProfileId
				,statValue.toPrintString());

		return s;
	}
	
	private String printDataStats(String name,DataStats dataStats) {
		return name+"\n"+dataStats.toPrintString();
	}
	
	public int getNodeGroupType() {
		return this.nodeGroupType;
	}
	
	public String getFunctionProfileId() {
		return functionProfileId;
	}

	public StatValue getReqMessageSizeStat() {
		return reqMessageSizeStat;
	}

	public StatValue getRspMessageSizeStat() {
		return rspMessageSizeStat;
	}

	public DataStats getInputDataSizeStats() {
		return inputDataSizeStats;
	}

	public DataStats getOutputDataSizeStats() {
		return outputDataSizeStats;
	}

	public DataDurationStats getInputDataDurationStats() {
		return inputDataDurationStats;
	}

	public DataDurationStats getOutputDataDurationStats() {
		return outputDataDurationStats;
	}

	public StatValue getProcessingDurationStats() {
		return processingDurationStats;
	}

	public StatValue getCompletionDurationStats() {
		return completionDurationStats;
	}
	
}
