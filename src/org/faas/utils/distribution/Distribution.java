package org.faas.utils.distribution;

import org.cloudbus.cloudsim.distributions.ContinuousDistribution;

import java.util.Random;

public abstract class Distribution implements ContinuousDistribution {
	// CWE-500(6) add final
	public static final int NORMAL = 1;
	public static final int DETERMINISTIC = 2;
	public static final int UNIFORM = 3;
	public static final int LOCATION = 4;
	public static final int TRACEBASEDTRAFFIC = 5;
	public static final int ZIPFWITHBASE = 6;

	public abstract int getDistributionType();
}
