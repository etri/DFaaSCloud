package org.faas.topology.test;

import org.faas.DFaaSConstants;
import org.faas.topology.*;
import org.faas.utils.JsonUtil;

import java.io.File;

public class NetworkTopologyTest {

	public static NetworkTopology createEmptyNetworkTopology() {
		NetworkTopology topology = new NetworkTopology();
		
		FunctionProfile fp = FunctionProfileList.getInstance().getFunctionProfileList().get(0);
		
		topology.addFunctionProfile(fp);
				
		NodeGroup coreNG = new NodeGroup();
		coreNG.setId(NetworkTopology.getEntityId());
		coreNG.setType(DFaaSConstants.CORE_NODE_GROUP);
		coreNG.setMips(2800);
		coreNG.setRam(4000);
		coreNG.setRatePerMips(0.0);

		NodeGroup edgeNG = new NodeGroup();
		edgeNG.setId(NetworkTopology.getEntityId());
		edgeNG.setType(DFaaSConstants.EDGE_NODE_GROUP);
		edgeNG.setMips(2800);
		edgeNG.setRam(4000);
		edgeNG.setRatePerMips(0.0);

		NodeGroup edgeNG2 = new NodeGroup();
		edgeNG2.setId(NetworkTopology.getEntityId());
		edgeNG2.setType(DFaaSConstants.EDGE_NODE_GROUP);
		edgeNG2.setMips(2800);
		edgeNG2.setRam(4000);
		edgeNG2.setRatePerMips(0.0);
		
		NodeGroup fogNG = new NodeGroup();
		fogNG.setId(NetworkTopology.getEntityId());
		fogNG.setType(DFaaSConstants.FOG_NODE_GROUP);
		fogNG.setMips(2800);
		fogNG.setRam(4000);
		fogNG.setRatePerMips(0.0);

		EndDeviceGroup endDeviceGroup = new EndDeviceGroup();
		endDeviceGroup.setId(NetworkTopology.getEntityId());
		
		Sensor sensor = new Sensor();
		sensor.setId(NetworkTopology.getEntityId());
		sensor.setFunctionProfileId(fp.getFunctionProfileId());
		
		Actuator actuator = new Actuator();
		actuator.setId(NetworkTopology.getEntityId());
		//actuator.setFunctionProfileId(fp.getFunctionProfileId());

		actuator.setSensorId(sensor.getId());
		sensor.setActuatorId(actuator.getId());

		endDeviceGroup.addSensor(sensor);
		endDeviceGroup.addActuator(actuator);
		
		topology.addNodeGroup(coreNG);
		topology.addNodeGroup(edgeNG);
		//topology.addNodeGroup(edgeNG2);
		topology.addNodeGroup(fogNG);
		topology.addEndDeviceGroup(endDeviceGroup);
		
		Link link;
		
		// End Device Group <> FOG
		link = new Link();
		link.setSourceId(endDeviceGroup.getId());
		link.setDestId(fogNG.getId());
		link.setBw(2.2);
		link.setDelay(1.0);
		link.setNetworkingUnitCost(1.5);
		topology.addLink(link);
		
		link = new Link();
		link.setSourceId(fogNG.getId());
		link.setDestId(endDeviceGroup.getId());
		link.setBw(3.2);
		link.setDelay(1.0);
		link.setNetworkingUnitCost(1.7);
		topology.addLink(link);
		
		// FOG <> EDGE

		link = new Link();
		link.setSourceId(fogNG.getId());
		link.setDestId(edgeNG.getId());
		link.setBw(5.2);
		link.setDelay(1.0);
		link.setNetworkingUnitCost(1.8);
		topology.addLink(link);
		
		link = new Link();
		link.setSourceId(edgeNG.getId());
		link.setDestId(fogNG.getId());
		link.setBw(6.2);
		link.setDelay(1.0);
		link.setNetworkingUnitCost(3.3);
		topology.addLink(link);

		// EDGE <> CORE

		link = new Link();
		link.setSourceId(edgeNG.getId());
		link.setDestId(coreNG.getId());
		link.setBw(9.2);
		link.setDelay(1.0);
		link.setNetworkingUnitCost(2.5);
		topology.addLink(link);
		
		link = new Link();
		link.setSourceId(coreNG.getId());
		link.setDestId(edgeNG.getId());
		link.setBw(10.2);
		link.setDelay(1.3);
		link.setNetworkingUnitCost(1.7);
		topology.addLink(link);

		return topology;
	}
	
	private NetworkTopologyTest() {

		NetworkTopology topology = new NetworkTopology();
		
		FunctionProfile fp = FunctionProfileTest.packFunctionProfile();
		
		topology.addFunctionProfile(fp);
				
		NodeGroup coreNG = new NodeGroup();
		coreNG.setId(NetworkTopology.getEntityId());
		coreNG.setType(DFaaSConstants.CORE_NODE_GROUP);
		coreNG.setMips(2800);
		coreNG.setRam(4000);
		coreNG.setRatePerMips(0.0);

		NodeGroup edgeNG = new NodeGroup();
		edgeNG.setId(NetworkTopology.getEntityId());
		edgeNG.setType(DFaaSConstants.EDGE_NODE_GROUP);
		edgeNG.setMips(2800);
		edgeNG.setRam(4000);
		edgeNG.setRatePerMips(0.0);

		NodeGroup edgeNG2 = new NodeGroup();
		edgeNG2.setId(NetworkTopology.getEntityId());
		edgeNG2.setType(DFaaSConstants.EDGE_NODE_GROUP);
		edgeNG2.setMips(2800);
		edgeNG2.setRam(4000);
		edgeNG2.setRatePerMips(0.0);
		
		NodeGroup fogNG = new NodeGroup();
		fogNG.setId(NetworkTopology.getEntityId());
		fogNG.setType(DFaaSConstants.FOG_NODE_GROUP);
		fogNG.setMips(2800);
		fogNG.setRam(4000);
		fogNG.setRatePerMips(0.0);

		EndDeviceGroup endDeviceGroup = new EndDeviceGroup();
		endDeviceGroup.setId(NetworkTopology.getEntityId());
		
		Sensor sensor = new Sensor();
		sensor.setId(NetworkTopology.getEntityId());
		sensor.setFunctionProfileId(fp.getFunctionProfileId());
		
		Actuator actuator = new Actuator();
		actuator.setId(NetworkTopology.getEntityId());
		//actuator.setFunctionProfileId(fp.getFunctionProfileId());

		actuator.setSensorId(sensor.getId());
		sensor.setActuatorId(actuator.getId());

		endDeviceGroup.addSensor(sensor);
		endDeviceGroup.addActuator(actuator);
		
		topology.addNodeGroup(coreNG);
		topology.addNodeGroup(edgeNG);
		//topology.addNodeGroup(edgeNG2);
		topology.addNodeGroup(fogNG);
		topology.addEndDeviceGroup(endDeviceGroup);
		
		Link link;
		
		// End Device Group <> FOG
		link = new Link();
		link.setSourceId(endDeviceGroup.getId());
		link.setDestId(fogNG.getId());
		link.setBw(2.2);
		link.setDelay(1.0);
		link.setNetworkingUnitCost(1.5);
		topology.addLink(link);
		
		link = new Link();
		link.setSourceId(fogNG.getId());
		link.setDestId(endDeviceGroup.getId());
		link.setBw(3.2);
		link.setDelay(1.0);
		link.setNetworkingUnitCost(1.7);
		topology.addLink(link);
		
		// FOG <> EDGE

		link = new Link();
		link.setSourceId(fogNG.getId());
		link.setDestId(edgeNG.getId());
		link.setBw(5.2);
		link.setDelay(1.0);
		link.setNetworkingUnitCost(1.8);
		topology.addLink(link);
		
		link = new Link();
		link.setSourceId(edgeNG.getId());
		link.setDestId(fogNG.getId());
		link.setBw(6.2);
		link.setDelay(1.0);
		link.setNetworkingUnitCost(3.3);
		topology.addLink(link);

		// EDGE <> CORE

		link = new Link();
		link.setSourceId(edgeNG.getId());
		link.setDestId(coreNG.getId());
		link.setBw(9.2);
		link.setDelay(1.0);
		link.setNetworkingUnitCost(2.5);
		topology.addLink(link);
		
		link = new Link();
		link.setSourceId(coreNG.getId());
		link.setDestId(edgeNG.getId());
		link.setBw(10.2);
		link.setDelay(1.3);
		link.setNetworkingUnitCost(1.7);
		topology.addLink(link);

		String jsonFile = "json/network_topology";
		JsonUtil.write(topology, jsonFile);
		
		NetworkTopology topology2 = NetworkTopology.load(new File(jsonFile));
		
		System.out.println(JsonUtil.toPrettyJsonString(topology2));
	}
	
	public static void main(String argv[]) {
		new NetworkTopologyTest();
	}
}
