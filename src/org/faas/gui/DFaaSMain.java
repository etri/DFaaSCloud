package org.faas.gui;

import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.core.CloudSim;
import org.faas.AnalyticsEngine;
import org.faas.SimulationConfig;
import org.faas.SimulationManager;
import org.faas.rmi.SimulatorClientIF;
import org.faas.rmi.SimulatorClientRemote;
import org.faas.rmi.SimulatorServerIF;
import org.faas.stats.FunctionStats;
import org.faas.stats.collector.MonitoringDataCollector;
import org.faas.topology.NetworkTopology;
import org.faas.topology.NetworkTopologyHelper;
import org.faas.utils.Logger;

public class DFaaSMain {
	// CWE-500 : public -> private, add getAttachGui()
	private static boolean attachGui = false;
	// CWE-500 : public -> private, add getServer()
	private static SimulatorServerIF server;

	public static boolean getAttachGui() { return attachGui; }
	public static SimulatorServerIF getServer() { return server; }
	public static void main(String args[]) {
		
		java.text.NumberFormat byteFormat = java.text.NumberFormat.getNumberInstance();
		
		System.gc();
		long heapSize = Runtime.getRuntime().totalMemory();
		
		System.out.println(DFaaSMain.class.getName()+" executed.");
		if (args.length == 0) {
			System.out.println("Usage: java -jar dfaassim.jar [configuration_path]");
			System.out.println("Warning: The argument configuration_path is missing. './config' is used in default.");
		} else {
			//SimulationConfig.getInstance().setConfigPath(args[0]);
			SimulationManager.setConfigPath(args[0]);
		}
		
		for (int i=0;i<args.length;i++) {
			if (args[i].contains("ATTACH_GUI")) {
				attachGui = true;
				break;
			}
		}
		
		SimulatorClientIF rc = null;
		
		if (attachGui) {
			
			try {
				Registry registry = LocateRegistry.getRegistry("localhost",50053);
				server = (SimulatorServerIF) registry.lookup("server");
				MonitoringDataCollector.useRemote(server);
			} catch (Exception e) {
				Logger.error("DFaaSMain:main","Exception: " + e);
				// CWE-209
				//e.printStackTrace();
			}

			
			try {
				rc = new SimulatorClientRemote();
				server.registerSimulator(rc);
			} catch (Exception e) {
				Logger.error("DFaaSMain:main","Exception: " + e);
				// CWE-209
				//e.printStackTrace();
			}

		} else {
			MonitoringDataCollector.useEmpty();
		}
		
		String networkTopologyFile = SimulationManager.getConfigPath()+"/network_topology.json";
		
		int emitCount = -1; // if -1 infinite.
		
		//Log.enable(); // cloudSim logger
		Logger.enable(); // fogSim logger
		Logger.disable();
		Logger.setLogLevel(Logger.INFO);
		
		int num_user = 1; // number of cloud users
		Calendar calendar = Calendar.getInstance();
		boolean trace_flag = false; // mean trace events

		if (SimulationConfig.getInstance().getSimulationRunningDuration() <=0) {
			SimulationManager.setSimulationDuration(Double.MAX_VALUE);
		} else {
			SimulationManager.setSimulationDuration(SimulationConfig.getInstance().getSimulationRunningDuration());
		}
		
		CloudSim.init(num_user, calendar, trace_flag);
		
		NetworkTopology networkTopology = NetworkTopology.load(new File(networkTopologyFile));
		//networkTopology.setFunctionProfileList(FunctionProfileList.getInstance().getFunctionProfileList());

		System.out.println("DFaaSMain, main, configPath = "+SimulationManager.getConfigPath());
		System.out.println("DFaaSMain, main, CurrentSeed = "+NetworkTopologyHelper.getCurrentSeed());
		System.out.println("DFaaSMain, main, InpControllerDecisionTime = "+SimulationConfig.getInstance().getInpControllerDecisionTime());
		System.out.println("DFaaSMain, main, SimulationRunningDuration = "+SimulationConfig.getInstance().getSimulationRunningDuration());
		
		MonitoringDataCollector.getInstance().init();
		
		SimulationManager.startSimulation(networkTopology);
		
		if (attachGui) {
			try {
				server.reportAnalyticsEngineResult(AnalyticsEngine.getInstance().getFunctionStats());
			} catch (Exception e) {
				Logger.error("DFaaSMain:main","Exception: " + e);
				// CWE-209
				//e.printStackTrace();
			}
		}
		
		System.out.println(DFaaSMain.class.getName()+".main: exits.");
		
		if (attachGui) {
			
			try {
				server.unregisterSimulator(rc);
			} catch (Exception e) {
				Logger.error("DFaaSMain:main","Exception: " + e);
				// CWE-209
				//e.printStackTrace();
			}

		}
		
		System.out.println("start: Heap Size = " + byteFormat.format(heapSize) + " bytes");

		System.gc();
		heapSize = Runtime.getRuntime().totalMemory();
		System.out.println("  end: Heap Size = " + byteFormat.format(heapSize) + " bytes");

		System.exit(0);
	}
	
	
	public static void reportAnalyticsEngineResult(List<FunctionStats> functionStatsList) {
		try {
			server.reportAnalyticsEngineResult(functionStatsList);
		} catch (Exception e) {
			Logger.error("DFaaSMain:reportAnalyticsEngineResult","Exception: " + e);
			// CWE-209
			//e.printStackTrace();
		}
	}
	
	public static void registerSimulator(SimulatorClientIF simulator) {
		try {
			server.registerSimulator(simulator);
		} catch (Exception e) {
			Logger.error("DFaaSMain:registerSimulator","Exception: " + e);
			// CWE-209
			//e.printStackTrace();
		}
	}
	
	public static void setIdMap(Map<Integer,Integer> idMap) {
		try {
			server.setIdMap(idMap);
		} catch (Exception e) {
			Logger.error("DFaaSMain:setIdMap","Exception: " + e);
			// CWE-209
			//e.printStackTrace();
		}
	}
}