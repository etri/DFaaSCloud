package org.faas.stats;

import java.io.Serializable;

public class QueueModality implements Serializable {
	private double expectedFunctionRunningDuration;
	private int numberOfFunctionRequests;
	private int usingNumberOfCore;
	
	public QueueModality(double expectedFunctionRunningDuration) {
		this.expectedFunctionRunningDuration = expectedFunctionRunningDuration;
		this.numberOfFunctionRequests = 0;
		this.usingNumberOfCore = 0;

	}

	public double getExpectedFunctionRunningDuration() {
		return expectedFunctionRunningDuration;
	}

	public int getUsingNumberOfCore() {
		return usingNumberOfCore;
	}
	public int getNumberOfFunctionRequests() {
		return numberOfFunctionRequests;
	}
	public void increaseCpuCore(int value) {
		usingNumberOfCore += value;
	}
	public void increaseNumberOfFuctionRequests() {
		numberOfFunctionRequests++;
	}
}