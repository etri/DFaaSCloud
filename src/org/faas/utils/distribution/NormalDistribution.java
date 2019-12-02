package org.faas.utils.distribution;

import java.util.Random;

import org.cloudbus.cloudsim.distributions.ContinuousDistribution;

public class NormalDistribution extends Distribution {

	private double mean;
	private double stdDev;
	
	private final Random numGen;
	
	public NormalDistribution(long seed, double mean, double stdDev) {
		setMean(mean);
		setStdDev(stdDev);
		numGen = new Random(seed);
	}
	
	public double getMean() {
		return mean;
	}

	public void setMean(double mean) {
		this.mean = mean;
	}

	public double getStdDev() {
		return stdDev;
	}

	public void setStdDev(double stdDev) {
		this.stdDev = stdDev;
	}

	@Override
	public double sample() {
		return numGen.nextGaussian()*stdDev + mean;
	}

	@Override
	public int getDistributionType() {
		return Distribution.LOCATION;
	}

}
