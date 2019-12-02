package org.faas.utils.distribution;

public class DeterministicDistribution extends Distribution {

	private double value;

	public DeterministicDistribution(long seed, double value) {
		super();
		setValue(value);
	}
	
	@Override
	public double sample() {
		return value;
	}
	
	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public int getDistributionType() {
		return Distribution.DETERMINISTIC;
	}
}
