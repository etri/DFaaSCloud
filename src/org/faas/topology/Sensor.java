package org.faas.topology;

import org.faas.DFaaSConstants;

public class Sensor implements NodeIF {
	private int id;
	private String functionProfileId;
	private int actuatorId;
	
	private int endDeviceGroupId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFunctionProfileId() {
		return functionProfileId;
	}
	public void setFunctionProfileId(String functionProfileId) {
		this.functionProfileId = functionProfileId;
	}
	public int getActuatorId() {
		return actuatorId;
	}
	public void setActuatorId(int actuatorId) {
		this.actuatorId = actuatorId;
	}
	public int getType() {
		return DFaaSConstants.SENSOR;
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
