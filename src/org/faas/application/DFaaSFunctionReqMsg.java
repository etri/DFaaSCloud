package org.faas.application;

import org.cloudbus.cloudsim.core.SimEvent;
import org.faas.entities.EventSource;

public class DFaaSFunctionReqMsg {
	private String functionProfileId;
	private String functionInstanceId;
	private double messageSize;
	private DFaaSFunctionInstanceInfo functionInfo;
	private DfaaSFunctionInstance functionInstance;
	private EventSource eventSource; //
	
	private SimEvent simEvent;
	
	public DFaaSFunctionReqMsg(String functionProfileId, double messageSize,DFaaSFunctionInstanceInfo functionInfo) {
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

	public DfaaSFunctionInstance getFunctionInstance() {
		return functionInstance;
	}

	public void setFunctionInstance(DfaaSFunctionInstance functionInstance) {
		this.functionInstance = functionInstance;
	}

	public SimEvent getSimEvent() {
		return simEvent;
	}

	public void setSimEvent(SimEvent simEvent) {
		this.simEvent = simEvent;
	}

	public EventSource getEventSource() {
		return eventSource;
	}

	public void setEventSource(EventSource eventSource) {
		this.eventSource = eventSource;
	}
	
}