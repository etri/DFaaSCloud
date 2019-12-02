package org.faas.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.faas.stats.FunctionStats;

public interface SimulatorClientIF extends Remote {

	public void start() throws RemoteException;
	public void stop() throws RemoteException;
	public void shutDown() throws RemoteException;
	public List<FunctionStats> getFunctionStats() throws RemoteException;
	
}
