package org.faas.application;

import org.faas.entities.EventSink;

public class DFaaSFunctionRspMsg {
	private String functionProfileId;
	private String functionInstanceId;
	private double messageSize;
	private DFaaSFunctionInstanceInfo functionInfo;
	private EventSink eventSink;
	
	public DFaaSFunctionRspMsg(String functionProfileId, double messageSize,DFaaSFunctionInstanceInfo functionInfo) {
		
		this.functionProfileId = functionProfileId;
		this.functionInstanceId = functionInfo.getFunctionInstanceId();
		this.messageSize = messageSize;
		this.functionInfo = functionInfo;
	}

	public String getFunctionProfileId() {
		return functionProfileId;
	}

	public String getFunctionInstanceId() {
		return functionInstanceId;
	}

	public double getMessageSize() {
		return messageSize;
	}
	
	public DFaaSFunctionInstanceInfo getFunctionInfo() {
		return functionInfo;
	}

	public EventSink getEventSink() {
		return eventSink;
	}

	public void setEventSink(EventSink eventSink) {
		this.eventSink = eventSink;
	}

//	public FunctionPlacementControlAgentIF getFunctionPlacementAgent() {
//		return functionPlacementAgent;
//	}
	
	
}
