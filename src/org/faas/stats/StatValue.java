package org.faas.stats;

import java.io.Serializable;
import java.util.Formatter;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class StatValue implements Serializable {

	private long count;
	private double sum;
	
	private double mean = 0;
	private double variance = 0;
	private double median = 0;
	private double min = 0; 
	private double max = 0; // Issue #72
	
	private String measure;
	
	private SummaryStatistics stats = new SummaryStatistics() ;
	
	public StatValue() {
		this("ms");
	}
	
	public StatValue(String measure) {
		this.measure = measure;
	}
	
	// Issue #72
	public boolean isValid() {
		return count == 0 ? false:true;
	}
	
	public void addValue(double value) {
		
		stats.addValue(value);
		
		count++;
		mean = stats.getMean();
		//variance = stats.getVariance();
		variance = stats.getPopulationVariance();
		min = stats.getMin();
		max = stats.getMax();
		
		median = stats.getGeometricMean(); //TODO 
	}

	public long getCount() {
		return count;
	}

	public double getMean() {
		return mean;
	}

	public double getVariance() {
		return variance;
	}

	public double getMedian() {
		return median;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}
	
	public String getMeasure() {
		return measure;
	}

	public void setMeasure(String measure) {
		this.measure = measure;
	}

	public String toPrintString() {
		StringBuilder sbuf = new StringBuilder();
		Formatter fmt = new Formatter(sbuf);
		fmt.format("count=%10d, min=%10.3f, max=%10.3f, mean=%10.3f, median=%10.3f, variance=%10.3f"
				, count, min, max, mean, median, variance);
		
		return sbuf.toString();
	}
	
	public static void main(String []argv) {
		StatValue value = new StatValue();
		
		value.addValue(0);
		value.addValue(0);
		value.addValue(0);
		value.addValue(59);
		value.addValue(59);
		value.addValue(59);

		System.out.println(value.toPrintString());
	}
}
