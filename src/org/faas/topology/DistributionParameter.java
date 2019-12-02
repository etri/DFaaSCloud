package org.faas.topology;

public class DistributionParameter {

	private String name;
	private Object value;
	
	public DistributionParameter() {}
	
	public DistributionParameter(String name,Object value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public Object getValue() {
		return value;
	}
}
