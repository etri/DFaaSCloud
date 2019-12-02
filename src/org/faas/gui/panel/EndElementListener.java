package org.faas.gui.panel;

import org.faas.topology.Actuator;
import org.faas.topology.Sensor;

public interface EndElementListener {
	public void sensorAdded(Sensor sensor);
	public void actuatorAdded(Actuator actuator);
	public void sensorUpdated(Sensor sensor);
	public void actuatorUpdated(Actuator actuator);
}