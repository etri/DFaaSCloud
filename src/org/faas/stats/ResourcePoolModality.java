package org.faas.stats;

import java.io.Serializable;

public class ResourcePoolModality implements Serializable {
	private double remainingFunctionRunningDuration;
	private int numberOfFunctionRequests;
	private int usingNumberOfCore;
	
	public ResourcePoolModality(double remainingFunctionRunningDuration) {
		this.remainingFunctionRunningDuration = remainingFunctionRunningDuration;
		this.numberOfFunctionRequests = 0;
		this.usingNumberOfCore = 0;

	}

	public double getRemainingFunctionRunningDuration() {
		return remainingFunctionRunningDuration;
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
