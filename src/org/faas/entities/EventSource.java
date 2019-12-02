package org.faas.entities;

import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.faas.application.DFaaSFunctionReqMsg;
import org.faas.application.DFaaSFunctionRequester;
import org.faas.network.HIINetwork;
import org.faas.utils.DFaaSEvents;
import org.faas.utils.Logger;

public class EventSource extends SimEntity {

	private int controllerId;
	private int actuatorId;

	private DFaaSFunctionRequester requester;
	
	private int parentId;
	
	private int tpId; // id on NetworkTopology
	
	private int emitCount = -1;
	private int emittedCount = 0;
	
	public EventSource(int id, int actuatorId, DFaaSFunctionRequester requester) {
		super("EventSource-"+id);
		
		this.requester = requester;
		tpId = id;
		
		this.actuatorId = actuatorId;
	}
	
	public int getTpId() {
		return tpId;
	}

	public void setEmitCount(int count) {
		this.emitCount = count;
	}

	@Override
	public void startEntity() {
		send(getId(), requester.getDelayOfReqMsg(), DFaaSEvents.EMIT_FUNCTION_REQ);

		if (requester.getFunctionProfile().getFunctionRequestArrivalProcessModels().size()>1) {
			transmitTrafficSegment();
		}
	}

	@Override
	public void processEvent(SimEvent ev) {

		Logger.debug(this,"processEvent",getName(),ev.toString());
		
		switch(ev.getTag()){
			case DFaaSEvents.EMIT_FUNCTION_REQ:
				transmitInpFunctionArrivalReq();

				if (emitCount == -1) {
					send(getId(), requester.getDelayOfReqMsg(), DFaaSEvents.EMIT_FUNCTION_REQ);
				} else {
					if (emitCount > emittedCount) {
						send(getId(), requester.getDelayOfReqMsg(), DFaaSEvents.EMIT_FUNCTION_REQ);
						emittedCount++;
					}
				}
				break;
			case DFaaSEvents.FUNCTION_TRAFFIC_SEGMENT_END:
				transmitTrafficSegment();
				send(getId(), requester.getDelayOfReqMsg(), DFaaSEvents.EMIT_FUNCTION_REQ); // Issue #66
				break;
			// CWE-478: add default code
			default:
				break;
		}
	}

	@Override
	public void shutdownEntity() {
		// TODO Auto-generated method stub
		
	}
	
	private void transmitInpFunctionArrivalReq() {
		DFaaSFunctionReqMsg reqMsg = requester.getReqMsg();
		reqMsg.getFunctionInfo().setSensorId(super.getId());
		reqMsg.getFunctionInfo().setActuatorId(actuatorId);
		reqMsg.setEventSource(this);
		
		double delay = calcNetworkDelay(reqMsg);
		
		send(controllerId, delay, DFaaSEvents.INP_FUNCTION_REQ_ARRIVAL,reqMsg);
	}
	
	private void transmitTrafficSegment() {
		double delay = requester.getCurrentTrafficSegmentLength();
		send(super.getId(), delay, DFaaSEvents.FUNCTION_TRAFFIC_SEGMENT_END);
	}
	//int count = 0;
	
	/**
	 * 
	 * @param reqMsg
	 * @return milli-seconds
	 */
	private double calcNetworkDelay(DFaaSFunctionReqMsg reqMsg) {
		// refers ppt page 9.
		double messageSize = reqMsg.getMessageSize();
		double delay = HIINetwork.getInstance().calcNetworkDelay2(messageSize,getId(), parentId);
		
		return delay;
	}

	public void setControllerId(int controllerId) {
		this.controllerId = controllerId;
	}
	
	public void setActuatorId(int actuatorId) {
		this.actuatorId = actuatorId;
	}
	
	public int getActuatorId() {
		return actuatorId;
	}
	
	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

}
