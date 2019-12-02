package org.faas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.sdn.overbooking.BwProvisionerOverbooking;
import org.cloudbus.cloudsim.sdn.overbooking.PeProvisionerOverbooking;
import org.faas.dfaasfunctionplacementpolicy.FunctionPlacementControlAgentIF;
import org.faas.dfaasfunctionplacementpolicy.FunctionPlacementAgent;
import org.faas.application.DFaaSFunctionReceiver;
import org.faas.application.DFaaSFunctionRequester;
import org.faas.db.DatabaseLogger;
import org.faas.entities.*;
import org.faas.entities.EventSource;
import org.faas.gui.DFaaSMain;
import org.faas.network.HIINetwork;
import org.faas.policy.FunctionAllocationPolicy;
import org.faas.topology.NetworkTopology;
import org.faas.topology.NetworkTopologyHelper;
import org.faas.utils.FogLinearPowerModel;
import org.faas.utils.DFaaSUtils;
import org.faas.utils.Logger;

import org.faas.scheduler.FunctionInstanceScheduler;

public class SimulationManager {

	private static HIINetwork network;

	// CWE-490 public -> private :(3)
	private static double infraManagerDelay = 500;
	private static double simulationDuration = 3000;
	private static String configPath = "config";

	public static double getInfraManagerDelay() { return infraManagerDelay; }
	public static double setInfraManagerDelay(double val) {
		infraManagerDelay = val;
		return infraManagerDelay;
	}
	public static double getSimulationDuration() { return simulationDuration; }
	public static double setSimulationDuration(double val) {
		simulationDuration = val;
		return simulationDuration;
	}
	public static String getConfigPath() { return configPath; }
	public static String setConfigPath(String path) {
		configPath = path;
		return configPath;
	}

	public static void startSimulation(NetworkTopology networkTopology) {

    	if (SimulationConfig.getInstance().getDbLogging()) {
			DatabaseLogger.getInstance().open();
			DatabaseLogger.getInstance().insertMaster(UUID.randomUUID().toString());
    	}
    	
		// init network.
		network = HIINetwork.create();
		
		SimulationManager.infraManagerDelay = SimulationConfig.getInstance().getInfraManagerMonitoringInterval();
		if (SimulationConfig.getInstance().isInfiniteRunning()) {
			SimulationManager.simulationDuration = Double.MAX_VALUE;
		} else {
			SimulationManager.simulationDuration = SimulationConfig.getInstance().getSimulationRunningDuration();
		}
		
		NetworkTopologyHelper.setStartSeed(SimulationConfig.getInstance().getInitialSeed());
		AnalyticsEngine.init();

		NetworkTopologyHelper networkTopologyHelper = NetworkTopologyHelper.create(networkTopology);
		
		
		
		List<org.faas.topology.NodeGroup> nodeGroupList = networkTopology.getNodeList();
		Map<Integer,org.faas.topology.NodeGroup> tpIdMap = new HashMap<Integer,org.faas.topology.NodeGroup>();
		for (int i=0;i<nodeGroupList.size();i++) {
			org.faas.topology.NodeGroup node = nodeGroupList.get(i);
			tpIdMap.put(node.getId(), node);
			try {
				NodeGroup nodeGroup = createNodeGroup(node);
				network.addNode(nodeGroup);
			} catch (Exception e) {
				Logger.error("startSimulation","Exception: createNodeGroup()" + e);
				// CWE-209
				//e.printStackTrace();
			}
		}
		
		List<org.faas.topology.EndDeviceGroup> endDeviceList = networkTopology.getEndDeviceList();
		List<EventSource> realEventSourceList = new ArrayList<EventSource>();
		for (int i=0;i<endDeviceList.size();i++) {
			org.faas.topology.EndDeviceGroup endDeviceGroup = endDeviceList.get(i);
			
			List<org.faas.topology.Sensor> sensorList = endDeviceGroup.getSensorList();
			for (int j=0;j<sensorList.size();j++) {
				org.faas.topology.Sensor sensor = sensorList.get(j);
				DFaaSFunctionRequester requester = new DFaaSFunctionRequester(networkTopologyHelper.getFunctionProfile(sensor.getFunctionProfileId()));
				EventSource realEventSource = new EventSource(sensor.getId(),sensor.getActuatorId(), requester);
				network.addNode(realEventSource);
				realEventSourceList.add(realEventSource);
			}
			List<org.faas.topology.Actuator> actuatorList = endDeviceGroup.getActuatorList();
			for (int j=0;j<actuatorList.size();j++) {
				org.faas.topology.Actuator actuator = actuatorList.get(j);
				DFaaSFunctionReceiver receiver = new DFaaSFunctionReceiver();
				EventSink realEventSink = new EventSink(actuator.getId(),actuator.getSensorId(),receiver);
				network.addNode(realEventSink);
			}
		}
		
		List<org.faas.topology.Link> linkList = networkTopologyHelper.getPhysicalLinkList();
		for (int i=0;i<linkList.size();i++) {
			network.addLink(linkList.get(i));
		}
		
		network.resetPairIds();
		
		// instantiate DFaaSFunctionScheduler
		for (int i = 0; i< realEventSourceList.size(); i++) {
			EventSource realEventSource = realEventSourceList.get(i);
			List<SimEntity> path = network.findNodesToTop(realEventSource.getId());
			
			EventSource eventSource = null;
			NodeGroup coreNodeGroup = null;
			NodeGroup edgeNodeGroup = null;
			NodeGroup fogNodeGroup = null;
			for (int j=0;j<path.size();j++) {
				SimEntity entity = path.get(j);
				if (entity instanceof EventSource) {
					eventSource = (EventSource)entity;
				} else 
				if (entity instanceof NodeGroup) {
					NodeGroup ng = (NodeGroup)entity;
					if (ng.getType() == DFaaSConstants.CORE_NODE_GROUP) {
						coreNodeGroup = ng;
					} else if (ng.getType() == DFaaSConstants.EDGE_NODE_GROUP) {
						edgeNodeGroup = ng;
					} else if (ng.getType() == DFaaSConstants.FOG_NODE_GROUP) {
						fogNodeGroup = ng;
					}
				}
			}
			DFaaSFunctionScheduler dfaasFunctionScheduler = fogNodeGroup.getINPController();
			if (dfaasFunctionScheduler == null) {
				dfaasFunctionScheduler = new DFaaSFunctionScheduler(CloudSim.getEntityList().size()
						,coreNodeGroup.getId(),edgeNodeGroup.getId(),fogNodeGroup.getId());
				
				fogNodeGroup.setINPController(dfaasFunctionScheduler);
				
				org.faas.topology.NodeGroup node = tpIdMap.get(fogNodeGroup.getTpId());
				FunctionPlacementControlAgentIF functionPlacementAgent = FunctionPlacementAgent.create(node.getFunctionPlacementAgentInfo());
				dfaasFunctionScheduler.setFunctionPlacementAgent(functionPlacementAgent);
				
			}
			
			eventSource.setControllerId(dfaasFunctionScheduler.getId());
		}
		
		InfraManager.createInstance(infraManagerDelay);
		
		// [ test codes 
		List<SimEntity> l = CloudSim.getEntityList();
		for (int i=0;i<l.size();i++) {
			Logger.info("SimulationManager","startSimulation",l.get(i).getId()+" "+l.get(i).getName());
		}
		// ]

		if (DFaaSMain.getAttachGui()) {
			try {
				DFaaSMain.getServer().setIdMap(HIINetwork.getInstance().getIdMap());
			} catch (Exception e) {
				Logger.error("startSimulation","Exception: " + e);
				// CWE-209
				//e.printStackTrace();
			}
		}
		
		CloudSim.terminateSimulation((simulationDuration*1000));
		
		CloudSim.startSimulation();
		
		//CloudSim.stopSimulation();
		
		AnalyticsEngine.getInstance().print();
	}
	
	private static NodeGroup createNodeGroup(org.faas.topology.NodeGroup node) throws Exception {
		
		//node
		
		long mips = node.getMips();
		int ram = node.getRam();
		double ratePerMips = node.getRatePerMips(); 
		double busyPower = 107.339; // obsolete
		double idlePower = 83.4333; // obsolete
		long upBw = 0; // obsolete
		long downBw = 0; // obsolete
		//int level, 

		List<Pe> peList = new ArrayList<Pe>();

		// 3. Create PEs and add these into a list.
		int cores = node.getNumberOfCpuCores();
		
		//peList.add(new Pe(0, new PeProvisionerOverbooking(mips))); // need to store Pe id and MIPS Rating
		for (int i=0;i<cores;i++) {
			peList.add(new Pe(i, new PeProvisionerOverbooking(mips))); // need to store Pe id and MIPS Rating
		}

		int hostId = DFaaSUtils.generateEntityId();
		long storage = 1000000000; // host storage, obsolete
		int bw = 1000000000; // obsolete
		
		PowerHost host = new PowerHost(
				hostId,
				new RamProvisionerSimple(ram),
				new BwProvisionerOverbooking(bw), // TODO 
				storage,
				peList,
				new FunctionInstanceScheduler(peList),
				new FogLinearPowerModel(busyPower, idlePower)
			);

		List<Host> hostList = new ArrayList<Host>();
		hostList.add(host);
		
		
		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this
										// resource
		double costPerBw = 0.0; // the cost of using bw in this resource

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
				arch, os, vmm, hostList, time_zone, cost, costPerMem,
				costPerStorage, costPerBw);
		
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN
		// devices by now
		
		NodeGroup nodeGroup = new NodeGroup(node.getId(), node.getType(), node.getComputingUnitCost() , node.getMips(), node.getQueueSize(),
				characteristics, new FunctionAllocationPolicy(hostList, node.getMips()), storageList, 10, upBw, downBw, 0, ratePerMips);
		
		return nodeGroup;
		
	}
	
}
