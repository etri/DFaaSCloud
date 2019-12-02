package org.faas.rmi;

import java.util.List;

import org.faas.stats.FunctionStats;
import org.faas.utils.Logger;

public class DFaaSSimulator {

	private static SimulatorClientIF simulator;
	
	public static void createLocal() {
		simulator = new SimulatorClientLocal();
	}
	
	public static void createRemote(SimulatorClientIF simulator) {
		DFaaSSimulator.simulator = simulator;
	}
	
	public static void start() {
		if (simulator == null) return;

		try {
			simulator.start();
		} catch (Exception e) {
			// CWE-209 add code
			Logger.error("DFaaSSimulator:start","Exception: " + e);
			//e.printStackTrace();
		}
	}
	
	public static void stop() {
		if (simulator == null) return;
		
		try {
			simulator.stop();
		} catch (Exception e) {
			// CWE-209 add code
			Logger.error("DFaaSSimulator:stop","Exception: " + e);
			//e.printStackTrace();
		}
	}
	
	public static void shutDown() {
		if (simulator == null) return;

		try {
			simulator.shutDown();
		} catch (Exception e) {
			// CWE-390 add code
			Logger.error("DFaaSSimulator:shutDown","Exception: " + e);
			//e.printStackTrace();
		}
	}
	
	public static List<FunctionStats> getFunctionStats() {
		if (simulator == null) return null;
		try {
			return simulator.getFunctionStats();
		} catch (Exception e) {
			// CWE-390 add code
			Logger.error("DFaaSSimulator:getFunctionStats","Exception: " + e);
			//e.printStackTrace();
		}
		return null;
	}

}
