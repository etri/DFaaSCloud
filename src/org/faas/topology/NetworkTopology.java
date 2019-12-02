package org.faas.topology;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.faas.DFaaSConstants;
import org.faas.utils.JsonUtil;

public class NetworkTopology {
	// CWE-496 private -> public
	public List<FunctionProfile> functionProfileList = new ArrayList<FunctionProfile>();
	// CWE-495(3), CWE-496(3) private -> public
	public List<Link> linkList = new ArrayList<Link>();
	public List<NodeGroup> nodeList = new ArrayList<NodeGroup>();
	public List<EndDeviceGroup> endDeviceList = new ArrayList<EndDeviceGroup>();
	
//	public List<FunctionProfile> getFunctionProfileList() {
//		return functionProfileList;
//	}
	
	@Deprecated
	public void setFunctionProfileList(List<FunctionProfile> functionProfileList) {
		this.functionProfileList = functionProfileList;
	}
	public List<Link> getLinkList() {
		return linkList;
	}
	public void setLinkList(List<Link> linkList) {
		this.linkList = linkList;
	}
	public List<NodeGroup> getNodeList() {
		return nodeList;
	}
	public void setNodeList(List<NodeGroup> nodeList) {
		this.nodeList = nodeList;
	}
	
	public List<EndDeviceGroup> getEndDeviceList() {
		return endDeviceList;
	}
	public void setEndDeviceList(List<EndDeviceGroup> endDeviceList) {
		this.endDeviceList = endDeviceList;
	}
	
	public boolean deleteLink(Link link) {
		return linkList.remove(link);
	}
	
	public boolean addLink(Link link) {
		if (contains(link)) {
			return false;
		}
		linkList.add(link);
		return true;
	}
	
	private boolean contains(Link link) {
		
		for (int i=0;i<linkList.size();i++) {
			if (linkList.get(i).equals(link)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void deleteNodeGroup(NodeGroup node) {
		nodeList.remove(node);
	}
	
	public void addNodeGroup(NodeGroup node) {
		nodeList.add(node);
	}

	public void deleteEndDeviceGroup(EndDeviceGroup node) {
		endDeviceList.remove(node);
	}
	
	public void addEndDeviceGroup(EndDeviceGroup node) {
		endDeviceList.add(node);
	}
	
	public void addFunctionProfile(FunctionProfile fp) {
		functionProfileList.add(fp);
	}
	
	public boolean isFunctionProfileInUse(String functionProfileId) {
		for (int i=0;i<endDeviceList.size();i++) {
			EndDeviceGroup endDeviceGroup = endDeviceList.get(i);
			for (int j=0;j<endDeviceGroup.getSensorList().size();j++) {
				Sensor s = endDeviceGroup.getSensorList().get(j);
				if (s.getFunctionProfileId().equals(functionProfileId)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private String validateResult;
	
	public boolean validate() {
		StringBuffer sb = new StringBuffer();
		
		int invalidCount=0;
		
		for (int i=0;i<this.nodeList.size();i++) {
			
			NodeGroup nodeGroup = nodeList.get(i);
			if (nodeGroup.getType() == DFaaSConstants.FOG_NODE_GROUP) {
				if (nodeGroup.getFunctionPlacementAgentInfo() == null) {
					sb.append("[FogNodeGroup #").append(nodeGroup.getId()).append("].")
					.append("Function Placement Agent Info. NOT exist.").append("\n");
				}
			}
			
		}
		
		for (int i=0;i<endDeviceList.size();i++) {
			EndDeviceGroup endDeviceGroup = endDeviceList.get(i);
			for (int j=0;j<endDeviceGroup.getSensorList().size();j++) {
				Sensor s = endDeviceGroup.getSensorList().get(j);
				if (s.getActuatorId() == 0) {
					invalidCount++;
					sb.append("[EndDeviceGroup #").append(s.getEndDeviceGroupId()).append("].")
					.append("EventSource #").append(s.getId()).append(" has NO paired EventSink.").append("\n");
				}
				
				String functionProfileId = s.getFunctionProfileId();
				if (functionProfileId == null || NetworkTopologyHelper.getInstance().getFunctionProfile(functionProfileId) == null) {
					invalidCount++;
					sb.append("[EndDeviceGroup #").append(s.getEndDeviceGroupId()).append("].")
					.append("EventSource #").append(s.getId()).append(" NO function profile is set.").append("\n");
				}
			}
			for (int j=0;j<endDeviceGroup.getActuatorList().size();j++) {
				Actuator a = endDeviceGroup.getActuatorList().get(j);
				if (a.getSensorId() == 0) {
					invalidCount++;
					sb.append("[EndDeviceGroup #").append(a.getEndDeviceGroupId()).append("].")
					.append("EventSink #").append(a.getId()).append(" has NO paired EventSource.").append("\n");
				}
			}
		}
		
		validateResult = sb.toString();
		return invalidCount==0?true:false;
	}
	
	public String getValidateResult() {
		return validateResult;
	}
	
	public static NetworkTopology load(File file) {
		NetworkTopology nt = (NetworkTopology)JsonUtil.read(NetworkTopology.class, file);
		
		// update entityId
		for (int i=0;i<nt.nodeList.size();i++) {
			int id = nt.nodeList.get(i).getId();
			if (id > NetworkTopology.entityId) {
				NetworkTopology.entityId = id;
			}
		}
		for (int i=0;i<nt.endDeviceList.size();i++) {
			EndDeviceGroup g = nt.endDeviceList.get(i);
			for (int j=0;j<g.getActuatorList().size();j++) {
				int id = g.getActuatorList().get(j).getId();
				if (id > NetworkTopology.entityId) {
					NetworkTopology.entityId = id;
				}
			}
			for (int j=0;j<g.getSensorList().size();j++) {
				int id = g.getSensorList().get(j).getId();
				if (id > NetworkTopology.entityId) {
					NetworkTopology.entityId = id;
				}
			}
		}
		return nt;
	}
	
	private static int entityId = 1;
	
	public static int getEntityId() {
		return ++entityId;
	}
}
