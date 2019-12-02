package org.faas.topology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.faas.utils.IntKey;
import org.faas.utils.ReflectionUtil;
import org.faas.utils.Logger;

public class NetworkTopologyHelper {

	private NetworkTopology networkTopology;
	private Map<String,FunctionProfile> functionProfileMap = new HashMap<String,FunctionProfile>();
	
	private Map<Integer,NodeIF> nodeMap = new HashMap<Integer,NodeIF>();
	private Map<IntKey,Link> linkMap = new HashMap<IntKey,Link>();
	
	private static NetworkTopologyHelper me;
	private static long currentSeed;
	
	public static NetworkTopologyHelper create(NetworkTopology networkTopology) {
		me = new NetworkTopologyHelper(networkTopology);
		return me;
	}
	
	public static NetworkTopologyHelper getInstance() {
		return me;
	}
	
	private NetworkTopologyHelper(NetworkTopology networkTopology) {
		this.networkTopology = networkTopology;
		
		List<FunctionProfile> list = FunctionProfileList.getInstance().getFunctionProfileList();
		for (int i=0;i<list.size();i++) {
			FunctionProfile fp = list.get(i);
			functionProfileMap.put(fp.getFunctionProfileId(), fp);
			//System.out.println(fp.getFunctionProfileId());
		}
		
		List<NodeGroup> nodeGroupList = networkTopology.getNodeList();
		for (int i=0;i<nodeGroupList.size();i++) {
			nodeMap.put(nodeGroupList.get(i).getId(), nodeGroupList.get(i));
		}
		List<EndDeviceGroup> endDeviceGroupList = networkTopology.getEndDeviceList();
		for (int i=0;i<endDeviceGroupList.size();i++) {
			nodeMap.put(endDeviceGroupList.get(i).getId(), endDeviceGroupList.get(i));
			
			List<Actuator> al = endDeviceGroupList.get(i).getActuatorList();
			for (int j=0;j<al.size();j++) {
				nodeMap.put(al.get(j).getId(), al.get(j));
			}

			List<Sensor> sl = endDeviceGroupList.get(i).getSensorList();
			for (int j=0;j<sl.size();j++) {
				nodeMap.put(sl.get(j).getId(), sl.get(j));
			}

		}
		
		List<Link> linkList = networkTopology.getLinkList();
		for (int i=0;i<linkList.size();i++) {
			Link link = linkList.get(i);
			linkMap.put(new IntKey(link.getSourceId(), link.getDestId()), link);
		}
	}
	
	public FunctionProfile getFunctionProfile(String networkProfileId) {
		return functionProfileMap.get(networkProfileId);
	}
	
	public NodeIF getNode(int id) {
		return nodeMap.get(id);
	}
	
	public static void setStartSeed(long newSeed) {
		currentSeed = newSeed;
	}
	
	public static long getCurrentSeed() {
		return currentSeed;
	}
	
	public static ContinuousDistribution instantiateDist(DistributionModel distModel) {
		String className = distModel.getClassName();
		
		List<DistributionParameter> parameterList = distModel.getParameterList();
		Object initArgs[] = new Object[parameterList.size()+1];
		initArgs[0] = currentSeed++; //SimulationConfig.getInstance().nextSeed();
		for (int i=0;i<parameterList.size();i++) {
			initArgs[i+1] = parameterList.get(i).getValue();
		}
		
		Logger.info("NetworkTopologyHelper","instantiateDist","NetworkingTopologyHelper.instantiateDist; className="+className+" ");
		StringBuffer sbArgs = new StringBuffer();
		for (int i=0;i<initArgs.length;i++) {
			sbArgs.append(initArgs[i]).append(" ");
		}
		Logger.info("NetworkTopologyHelper","instantiateDist",sbArgs.toString());
		
		return (ContinuousDistribution)ReflectionUtil.createObject(className, initArgs);
	}
	
	public List<Link> getPhysicalLinkList() {
		List<Link> phyLinkList = new ArrayList<Link>();
		
		Map<Integer,EndDeviceGroup> endDeviceGroupMap = new HashMap<Integer,EndDeviceGroup>();
		List<EndDeviceGroup> endDeviceGroupList = networkTopology.getEndDeviceList();
		for (int i=0;i<endDeviceGroupList.size();i++) {
			EndDeviceGroup endDeviceGroup = endDeviceGroupList.get(i);
			endDeviceGroupMap.put(endDeviceGroup.getId(), endDeviceGroup);
		}
		
		List<Link> linkList = networkTopology.getLinkList();
		for (int i=0;i<linkList.size();i++) {
			// CWE-491 add try ... catch
			try {
				Link link = linkList.get(i);
				if (endDeviceGroupMap.containsKey(link.getSourceId())) {
					EndDeviceGroup endDeviceGroup = endDeviceGroupMap.get(link.getSourceId());

					Link newLink = null;

					List<Sensor> sensorList = endDeviceGroup.getSensorList();
					for (int j = 0; j < sensorList.size(); j++) {
						newLink = link.clone();
						newLink.setSourceId(sensorList.get(j).getId());
						phyLinkList.add(newLink);
					}

					List<Actuator> actuatorList = endDeviceGroup.getActuatorList();
					for (int j = 0; j < actuatorList.size(); j++) {
						newLink = link.clone();
						newLink.setSourceId(actuatorList.get(j).getId());
						phyLinkList.add(newLink);
					}

				} else if (endDeviceGroupMap.containsKey(link.getDestId())) {
					EndDeviceGroup endDeviceGroup = endDeviceGroupMap.get(link.getDestId());

					Link newLink = null;

					List<Sensor> sensorList = endDeviceGroup.getSensorList();
					for (int j = 0; j < sensorList.size(); j++) {
						newLink = link.clone();
						newLink.setDestId(sensorList.get(j).getId());
						phyLinkList.add(newLink);
					}

					List<Actuator> actuatorList = endDeviceGroup.getActuatorList();
					for (int j = 0; j < actuatorList.size(); j++) {
						newLink = link.clone();
						newLink.setDestId(actuatorList.get(j).getId());
						phyLinkList.add(newLink);
					}

				} else {
					phyLinkList.add(link);
				}
			} catch(CloneNotSupportedException e) {
				Logger.error("NetworkTopologyHelper:getPhysicalLinkList","Exception: " + e);
			}
		}
		
		return phyLinkList;
	}
	
	public void deleteLlink(Link link) {
		if (networkTopology.deleteLink(link)) {
			Link link2 =linkMap.get(new IntKey(link.getDestId(),link.getSourceId()));
			networkTopology.deleteLink(link2);

			linkMap.remove(new IntKey(link.getSourceId(),link.getDestId()));
			linkMap.remove(new IntKey(link2.getSourceId(),link2.getDestId()));
		}
	}
	
	public int countType(int type) {
		Iterator<NodeIF> ite = nodeMap.values().iterator();
		int count = 0;
		
		while(ite.hasNext()) {
			if (ite.next().getType() == type) count++;
		}
		
		return count;
	}
	
	public void deleteNode(int id) {
		if (nodeMap.containsKey(id)) {
			NodeIF node = nodeMap.get(id);
			if (node instanceof NodeGroup) {
				if (countType(node.getType())==1) return;
				networkTopology.deleteNodeGroup((NodeGroup)node);
			} if (node instanceof EndDeviceGroup) {
				if (countType(node.getType())==1) return;
				networkTopology.deleteEndDeviceGroup((EndDeviceGroup)node);
			} if (node instanceof Sensor) {
				List<EndDeviceGroup> list = networkTopology.getEndDeviceList();
				for (int i=0;i<list.size();i++) {
					if (list.get(i).deleteSensor((Sensor)node)) {
						break;
					}
				}
			} if (node instanceof Actuator) {
				List<EndDeviceGroup> list = networkTopology.getEndDeviceList();
				for (int i=0;i<list.size();i++) {
					if (list.get(i).deleteActuator((Actuator)node)) {
						break;
					}
				}
			}
			nodeMap.remove(id);
			
			List<IntKey> linkList = findLinkKeys(id);
			for (int i=0;i<linkList.size();i++) {
				IntKey key = linkList.get(i);
				networkTopology.deleteLink(linkMap.get(key));
				linkMap.remove(key);
			}
		}
	}
	
	public List<Link> findLinks(int nodeId) {
		List<Link> list = new ArrayList<Link>();
		
		Iterator<IntKey> ite = linkMap.keySet().iterator();
		while (ite.hasNext()) {
			IntKey key = ite.next();
			if (key.getKey1() == nodeId || key.getKey2() == nodeId) {
				list.add(linkMap.get(key));
			} 
		}
		
		return list;
	}
	
	public List<IntKey> findLinkKeys(int nodeId) {
		List<IntKey> list = new ArrayList<IntKey>();
		
		Iterator<IntKey> ite = linkMap.keySet().iterator();
		while (ite.hasNext()) {
			IntKey key = ite.next();
			if (key.getKey1() == nodeId || key.getKey2() == nodeId) {
				list.add(key);
			}
		}
		
		return list;
	}

	
	public List<Sensor> getUnpairedSensorList() {
		List<Sensor> sensors = new ArrayList<Sensor>();
		Iterator<EndDeviceGroup> nodeIte = networkTopology.getEndDeviceList().iterator();
		while (nodeIte.hasNext()) {
			EndDeviceGroup endGroup = nodeIte.next();

			List<Sensor> sensorList = endGroup.getSensorList();
			for (int i=0;i<sensorList.size();i++) {
				int pairedActuatorId = sensorList.get(i).getActuatorId();
				if (pairedActuatorId == 0) {
					sensors.add(sensorList.get(i));
				}
			}
			
		}

		return sensors;
	}
	
	public List<Actuator> getUnpairedActuatorList() {
		List<Actuator> actuators = new ArrayList<Actuator>();
		Iterator<EndDeviceGroup> nodeIte = networkTopology.getEndDeviceList().iterator();
		while (nodeIte.hasNext()) {
			EndDeviceGroup endGroup = nodeIte.next();

			List<Actuator> actuatorsList = endGroup.getActuatorList();
			for (int i=0;i<actuatorsList.size();i++) {
				int pairedSensorId = actuatorsList.get(i).getSensorId();
				if (pairedSensorId == 0) {
					actuators.add(actuatorsList.get(i));
				}
			}

		}

		return actuators;
	}
}
