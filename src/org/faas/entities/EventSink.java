package org.faas.entities;

import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.faas.application.DFaaSFunctionReceiver;
import org.faas.application.DFaaSFunctionRspMsg;
import org.faas.utils.DFaaSEvents;
import org.faas.utils.Logger;

public class EventSink extends SimEntity {

	private int sensorId;
	
	private int tpId; // id on NetworkTopology
	
	private int parentId;
	
	private DFaaSFunctionReceiver receiver;
	
	public EventSink(int id, int sensorId, DFaaSFunctionReceiver receiver) {
		super("EventSink-"+id);
		
		tpId = id;
		this.sensorId = sensorId;
		this.receiver = receiver;
	}
	
	public int getTpId() {
		return tpId;
	}
	
	@Override
	public void startEntity() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processEvent(SimEvent ev) {
		Logger.debug(this,"processEvent",getName(),ev.toString());
		
		switch(ev.getTag()){
			case DFaaSEvents.ACTUATOR_FUNCTION_RSP_ARRIVAL:
				processActuatorFunctionRspArrival(ev);
				break;
			// CWE-478: add default code
			default:
				break;
		}
	}
	
	private void processActuatorFunctionRspArrival(SimEvent ev) {
		receiver.processFunctionResponse((DFaaSFunctionRspMsg)ev.getData());
	}

	@Override
	public void shutdownEntity() {
		// TODO Auto-generated method stub
		
	}
	
	public void setSensorId(int sensorId) {
		this.sensorId = sensorId;
	}
	
	public int getSensorId() {
		return sensorId;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	
}
