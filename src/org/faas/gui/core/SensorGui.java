package org.faas.gui.core;

import java.io.Serializable;

import org.faas.utils.distribution.DeterministicDistribution;
import org.faas.utils.distribution.Distribution;
import org.faas.utils.distribution.NormalDistribution;
import org.faas.utils.distribution.UniformDistribution;

public class SensorGui extends Node implements Serializable{

	private static final long serialVersionUID = 4087896123649020073L;

	private String name;
	private String sensorType;
	
	private Distribution distribution;
	
	public SensorGui(String name, String type, Distribution distribution){
		super(name, "SENSOR");
		setName(name);
		setSensorType(type);
		setDistribution(distribution);
	}

	public int getDistributionType(){
		return distribution.getDistributionType();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Distribution getDistribution() {
		return distribution;
	}

	public void setDistribution(Distribution distribution) {
		this.distribution = distribution;
	}
	
	@Override
	public String toString() {
		
		if(distribution instanceof NormalDistribution)
			return "EventSource [dist=1 mean=" + ((NormalDistribution)distribution).getMean() + " stdDev=" + ((NormalDistribution)distribution).getStdDev() + "]";
		else if(distribution instanceof UniformDistribution)
			return "EventSource [dist=2 min=" + ((UniformDistribution)distribution).getMin() + " max=" + ((UniformDistribution)distribution).getMax() + "]";
		else if(distribution instanceof DeterministicDistribution)
			return "EventSource [dist=3 value=" + ((DeterministicDistribution)distribution).getValue() + "]";
		else
			return "";
	}

	public String getSensorType() {
		return sensorType;
	}

	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}
	
}
