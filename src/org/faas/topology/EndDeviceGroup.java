package org.faas.topology;

import java.util.ArrayList;
import java.util.List;

import org.faas.DFaaSConstants;

public class EndDeviceGroup implements NodeIF {

	private int id;
	// CWE-495(2), CWE-496(2) private -> public
	public List<Sensor> sensorList = new ArrayList<Sensor>();
	public List<Actuator> actuatorList = new ArrayList<Actuator>();
	private int type = DFaaSConstants.END_EDVICE_GROUP;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public List<Sensor> getSensorList() {
		return sensorList;
	}

	public void setSensorList(List<Sensor> sensorList) {
		this.sensorList = sensorList;
	}

	public List<Actuator> getActuatorList() {
		return actuatorList;
	}

	public void setActuatorList(List<Actuator> actuatorList) {
		this.actuatorList = actuatorList;
	}

	public void addSensor(Sensor sensor) {
		sensorList.add(sensor);
		sensor.setEndDeviceGroupId(id);
	}
	
	public void addActuator(Actuator actuator) {
		actuatorList.add(actuator);
		actuator.setEndDeviceGroupId(id);
	}
	
	public boolean deleteActuator(Actuator actuator) {
		Sensor pairedSensor = getSensor(actuator.getSensorId());
		if (pairedSensor != null) {
			pairedSensor.setActuatorId(0); // clear paired actuator
		}
		
		return actuatorList.remove(actuator);
	}
	
	public boolean deleteSensor(Sensor sensor) {
		Actuator pairedActuator = getActuator(sensor.getActuatorId());
		if (pairedActuator != null) {
			pairedActuator.setSensorId(0); // clear paired sensor
		}
		
		return sensorList.remove(sensor);
	}
	
	public Actuator getActuator(int id) {
		Actuator actuator = null;
		
		for (int i=0;i<actuatorList.size();i++) {
			if (id == actuatorList.get(i).getId()) {
				actuator = actuatorList.get(i);
				break;
			}
		}
		
		return actuator;
	}
	
	public Sensor getSensor(int id) {
		Sensor sensor = null;
		
		for (int i=0;i<sensorList.size();i++) {
			if (id == sensorList.get(i).getId()) {
				sensor = sensorList.get(i);
				break;
			}
		}
		
		return sensor;
	}
}
