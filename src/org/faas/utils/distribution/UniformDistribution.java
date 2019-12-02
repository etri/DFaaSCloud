package org.faas.utils.distribution;

import org.faas.utils.Logger;
import org.faas.utils.distribution.Distribution;

import java.util.Random;

public class UniformDistribution extends Distribution {

	/** The num gen. */
	private final Random numGen;

	/** The min. */
	private final double mag, min, max;

	/**
	 * Creates new uniform distribution.
	 *
	 * @param min minimum value
	 * @param max maximum value
	 */
	public UniformDistribution(double min, double max) {
		if (min >= max) {
			throw new IllegalArgumentException("Maximum must be greater than the minimum.");
		}
		numGen = new Random();
		mag = max - min;
		this.min = min;
		this.max = max;
	}

	/**
	 * Creates new uniform distribution.
	 *
	 * @param min minimum value
	 * @param max maximum value
	 * @param seed simulation seed to be used
	 */
	// 	public UniformDistribution(double min, double max, long seed) { ORG
	public UniformDistribution(long seed, double min, double max) {
		if (min >= max) {
			throw new IllegalArgumentException("Maximum must be greater than the minimum.");
		}

		numGen = new Random(seed);
		mag = max - min;
		this.min = min;
		this.max = max;
	}

	/**
	 * Generate a new random number.
	 *
	 * @return the next random number in the sequence
	 */
	@Override
	public double sample() {
		return (numGen.nextDouble() * (mag)) + min;
	}

	/**
	 * Generates a new random number based on the number generator and values provided as
	 * parameters.
	 *
	 * @param rd the random number generator
	 * @param min the minimum value
	 * @param max the maximum value
	 * @return the next random number in the sequence
	 */
	public static double sample(Random rd, double min, double max) {
		// CWE-248 try ... catch added
		try {
			if (min >= max) {
				throw new IllegalArgumentException("Maximum must be greater than the minimum.");
			}
		} catch(IllegalArgumentException e) {
			Logger.error("UniformDistribution:","IllegalArgumentException: " + e);
		}
		return (rd.nextDouble() * (max - min)) + min;
	}

	/**
	 * Set the random number generator's seed.
	 *
	 * @param seed the new seed for the generator
	 */
	public void setSeed(long seed) {
		numGen.setSeed(seed);
	}

	@Override
	public int getDistributionType() {
		return Distribution.UNIFORM;
	}

	public double getMin() { return this.min; }
	public double getMax() { return this.max; }
}
