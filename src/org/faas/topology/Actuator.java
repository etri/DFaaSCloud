package org.faas.topology;

import org.faas.DFaaSConstants;

public class Actuator implements NodeIF {
	private int id;
	private int sensorId;
//	private String functionProfileId;
	
	private int endDeviceGroupId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSensorId() {
		return sensorId;
	}
	public void setSensorId(int sensorId) {
		this.sensorId = sensorId;
	}
//	public String getFunctionProfileId() {
//		return functionProfileId;
//	}
//	public void setFunctionProfileId(String functionProfileId) {
//		this.functionProfileId = functionProfileId;
//	}
	public int getType() {
		return DFaaSConstants.ACTUATOR;
	}

	public void setType(int type) {
	}
	
	public String toString() {
		return id+"";
	}
	public int getEndDeviceGroupId() {
		return endDeviceGroupId;
	}
	public void setEndDeviceGroupId(int endDeviceGroupId) {
		this.endDeviceGroupId = endDeviceGroupId;
	}
	
}
