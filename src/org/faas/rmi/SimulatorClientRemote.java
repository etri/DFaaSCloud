package org.faas.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import org.cloudbus.cloudsim.core.CloudSim;
import org.faas.AnalyticsEngine;
import org.faas.stats.FunctionStats;

public class SimulatorClientRemote extends UnicastRemoteObject implements SimulatorClientIF {

	public SimulatorClientRemote() throws RemoteException {
		super();
	}
	
	@Override
	public void start() throws RemoteException {
		CloudSim.startSimulation();
	}

	@Override
	public void stop() throws RemoteException {
		CloudSim.terminateSimulation();
	}

	public void shutDown() throws RemoteException {
		System.out.println(this.getClass().getName()+".shutDown()");
		System.exit(0);
	}
	
	public List<FunctionStats> getFunctionStats() throws RemoteException {
		if (AnalyticsEngine.getInstance() == null) return null;
		
		return AnalyticsEngine.getInstance().getFunctionStats();
	}
}
