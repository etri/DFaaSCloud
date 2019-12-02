package org.faas.rmi;

import java.rmi.RemoteException;
import java.util.List;

import org.cloudbus.cloudsim.core.CloudSim;
import org.faas.AnalyticsEngine;
import org.faas.stats.FunctionStats;

public class SimulatorClientLocal implements SimulatorClientIF {

	@Override
	public void start() {
		CloudSim.startSimulation();
	}

	@Override
	public void stop() {
		CloudSim.terminateSimulation();
	}

	public void shutDown() throws RemoteException {
		// do nothing
	}
	public List<FunctionStats> getFunctionStats() throws RemoteException {
		return AnalyticsEngine.getInstance().getFunctionStats();
	}

}
