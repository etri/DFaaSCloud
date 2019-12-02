package org.faas.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.core.SimEntity;
import org.faas.entities.EventSink;
import org.faas.entities.EventSource;
import org.faas.topology.Link;
import org.faas.utils.IntKey;
import org.faas.topology.NodeGroup;
import org.faas.DFaaSConstants;

public class HIINetwork {

	private Map<Integer,HIINode> hiiNodes = new HashMap<Integer,HIINode>();
	private Map<String,HIILink> hiiLinks = new HashMap<String,HIILink>();

	// CWE-495, CWE-496 private -> public
	// id in netowrk topology , id in CloudSim
	public Map<Integer,Integer> idMap = new HashMap<Integer,Integer>();
	
	private static HIINetwork me = new HIINetwork();;
	
	private HIINetwork() {
		
	}
	
	private int convertId(int idInNetworkTopology) {
		return idMap.get(idInNetworkTopology);
	}
	
	public Map<Integer,Integer> getIdMap() {
		return idMap;
	}

	public void setIdMap(Map<Integer,Integer> idMap) {
		this.idMap = idMap;
	}
	
	public int toTopologyId(int entityId) {
		Iterator<Integer> ite = idMap.keySet().iterator();
		while (ite.hasNext()) {
			int idInNetworkTopology = ite.next().intValue();
			int eid = idMap.get(idInNetworkTopology);
			
			if (eid == entityId) {
				return idInNetworkTopology;
			}
		}
		return -1;
	}

	public static HIINetwork create() {
		me = new HIINetwork();
		return me;
	}
	
	public static HIINetwork getInstance() {
		return me;
	}
	
	public HIINode getNode(int id) {
		return hiiNodes.get(id);
	}
	
	public EventSink getActuatorPairOfSensor(int sensorId) {
		int actuatorId = ((EventSource)getNode(sensorId).getNode()).getActuatorId();
		return (EventSink)getNode(actuatorId).getNode();
		
	}
	
	public void addNode(SimEntity node) {
		HIINode hiiNode = new HIINode(node);
		hiiNodes.put(hiiNode.getId(), hiiNode);
		
		idMap.put(hiiNode.getTpId(), hiiNode.getId());
	}
	
	/**
	 * EventSource 및 Actuator의 Network Topology내 sensorId, actuatorId 를 CloudSim의 id로 변경.
	 */
	public void resetPairIds() {
		Iterator<HIINode> ite = hiiNodes.values().iterator();
		
		while (ite.hasNext()) {
			HIINode hiiNode = ite.next();
			Object simEntity = hiiNode.getNode();

			if (simEntity instanceof EventSource) {
				EventSource eventSource = (EventSource)simEntity;
				int actuatorId = convertId(eventSource.getActuatorId());
				eventSource.setActuatorId(actuatorId);
				EventSink eventSink = (EventSink)getNode(actuatorId).getNode();
				eventSink.setSensorId(eventSource.getId());
			}
		}
	}
	
	public void addLink(Link link) {
		int sourceId = link.getSourceId();
		int destId = link.getDestId();
		
		sourceId = idMap.get(sourceId);
		destId = idMap.get(destId);

		HIINode sourceNode = hiiNodes.get(sourceId);
		HIINode destNode = hiiNodes.get(destId);
		
		sourceNode.setParentId(destNode.getId());
		
		// CoreNodeGroup must NOT have north node.
		if (sourceNode.getType() > destNode.getType()) {
		//if (sourceNode.getType() != DFaaSConstants.CORE_NODE_GROUP) {
			sourceNode.setNorthNode(destNode);
		}
		
		HIILink hiiLink = new HIILink();
		hiiLink.setSourceHIINode(sourceNode);
		hiiLink.setDestHIINode(destNode);
		hiiLink.setBw(link.getBw());
		hiiLink.setDelay(link.getDelay());
		hiiLink.setNetworkingUnitCost(link.getNetworkingUnitCost());
		
		hiiLinks.put(getLinkKey(sourceId,destId), hiiLink);
	}
	
	private String getLinkKey(int sourceId,int destId) {
		return sourceId+"."+destId;
	}
	
	public HIILink getLink(int sourceId, int destId) {
		return hiiLinks.get(getLinkKey(sourceId,destId));
	}
	
	public List<HIILink> findPathToTop(int sourceId) {
		List<HIILink> path = new ArrayList<HIILink>();
		scanLinksToTop(path,sourceId);
		return path;
	}

	public List<HIILink> findPath(int sourceId, int destId) {
		List<HIILink> path = new ArrayList<HIILink>();

		HIINode sourceNode = hiiNodes.get(sourceId);

		List<HIILink> path_p2 = null;
		HIINode edgeNode = scanToEdgeNode(sourceNode);
		if(edgeNode != null) {
			path_p2 = getPath(edgeNode.getId(),destId);
		}

		if(path_p2 == null || path_p2.isEmpty()) {
			HIINode coreNode = scanToCoreNode(sourceNode);
			path_p2 = getPath(coreNode.getId(),destId);
			if(!path_p2.isEmpty()) {
				List<HIILink> path_p1 = getPath(sourceId,coreNode.getId());
				path.addAll(path_p1);
				path.addAll(path_p2);
			}
		}
		else {
			List<HIILink> path_p1 = getPath(sourceId,edgeNode.getId());
			path.addAll(path_p1);
			path.addAll(path_p2);
		}

		return path;
	}

	private HIINode scanToEdgeNode(HIINode sourceNode) {
		HIINode northNode = sourceNode.getNorthNode();
		if (northNode != null) {
			if (northNode.getType() == DFaaSConstants.EDGE_NODE_GROUP)
				return northNode;
			return scanToEdgeNode(northNode);
		}
		return null;
	}

	private HIINode scanToCoreNode(HIINode sourceNode) {
		HIINode northNode = sourceNode.getNorthNode();
		if (northNode != null) {
			if (northNode.getType() == DFaaSConstants.CORE_NODE_GROUP)
				return northNode;
			return scanToCoreNode(northNode);
		}
		return null;
	}

	private void scanLinksToTop(List<HIILink> path,int sourceId) {
		HIINode sourceNode = hiiNodes.get(sourceId);

		HIINode northNode = sourceNode.getNorthNode();
		if (northNode == null) {
			
		} else {
			int northNodeId = northNode.getId();
			path.add(hiiLinks.get(getLinkKey(sourceNode.getId(),northNode.getId())));
			
			scanLinksToTop(path,northNodeId);
		}
	}
	
	public List<SimEntity> findNodesToTop(int sourceId) {
		List<SimEntity> path = new ArrayList<SimEntity>();
		scanNodesTop(path,sourceId);
		return path;
	}
	
	private void scanNodesTop(List<SimEntity> path,int sourceId) {
		HIINode sourceNode = hiiNodes.get(sourceId);

		HIINode northNode = sourceNode.getNorthNode();
		if (northNode == null) {
			path.add(sourceNode.getNode());
		} else {
			int northNodeId = northNode.getId();
			path.add(sourceNode.getNode());
			
			scanNodesTop(path,northNodeId);
		}
	}
	
	public List<HIILink> findLinks(int sourceId, int destId) {
		
		if (sourceId == destId) {
			return new ArrayList<HIILink>();
		}
		
		List<HIILink> path = new ArrayList<HIILink>();
		
		scanLinks(path,sourceId,destId);
		if (path.isEmpty()) {
			scanLinks(path,destId,sourceId);
			
			List<HIILink> path2 = new ArrayList<HIILink>();
			for (int i=path.size()-1;i>=0;i--) {
				HIILink l = path.get(i);
				HIILink l2 = hiiLinks.get(getLinkKey(l.getDestHIINode().getId(), l.getSourceHIINode().getId()));
				path2.add(l2);
			}
			path = path2;
		}
		
		return path;
	}
	
	private void scanLinks(List<HIILink> path,int sourceId, int destId) {
		HIINode sourceNode = hiiNodes.get(sourceId);

		if (sourceNode == null) {
			System.out.println(this.getClass().getName()+".scanLinks : TODO");
		}
		
		HIINode northNode = sourceNode.getNorthNode();
		if (northNode == null) {
			path.clear();
		} else {
			int northNodeId = northNode.getId();
			path.add(hiiLinks.get(getLinkKey(sourceNode.getId(),northNode.getId())));
			
			if (destId != northNodeId) {
				scanLinks(path,northNodeId,destId);
			}
		}
	}
	
	public double calcNetworkDelay(int size, int sourceId, int destId) {
	
		if (sourceId == destId) {
			return 0;
		}
		
		List<HIILink> linkList = getPath(sourceId, destId);
		
		double delay = 0;
		
		for (int i=0;i<linkList.size();i++) {
			HIILink link2 = linkList.get(i);
			
			double linkDelay = (double)size/link2.getBw();
			delay = delay + linkDelay;
		}
		
		return delay;
	}
	
	public double calcNetworkDelayToNorth(int size, int sourceId, int destId) {
		
		if (sourceId == destId) {
			return 0;
		}
		
		HIILink link = getLink(sourceId, destId);
		
		List<HIILink> linkList = new ArrayList<HIILink>();
		if (link == null) {
			linkList.addAll(findLinks(sourceId, destId));
		} else {
			linkList.add(link);
		}
		
		double delay = 0;
		
		if (linkList.size()==1) {
			HIILink link0 = linkList.get(0);
			delay = (double)size/link0.getBw();
			delay += link0.getDelay(); // TODO propagation delay + hop delay
		} else if (linkList.size()==2) {
			HIILink link0 = linkList.get(0);
			HIILink link1 = linkList.get(1);
			
			double link0Delay = (double)size/link0.getBw();
			double link1Delay = (double)size/link1.getBw();
			
			delay = Math.max(link0Delay, link1Delay) + link0.getDelay() + link1.getDelay();
			
		} else {
			System.err.println(this.getClass().getName()+".calcNetworkDelayToNorth: can NOT happen.");
		}
		
		return delay;
	}

	/**
	 * ppt page 25.
	 * 
	 * @param size
	 * @param sourceId
	 * @param destId
	 * @return milli-seconds
	 */
	public double calcNetworkDelay2(double size, int sourceId, int destId) {
		if (sourceId == destId) {
			return 0;
		}

		List<HIILink> linkList = getPath(sourceId, destId);

		if(linkList.size() <= 0) {
			linkList = findPath(sourceId, destId);
		}
		double maxDelay = 0;
		double delay = 0;
		for(int i=0; i < linkList.size(); i++) {
			HIILink link = linkList.get(i);
			delay += link.getDelay();
			double linkDelay = ((double)(size*8)/link.getBw())*1000;
			maxDelay = Math.max(linkDelay,maxDelay);
		}

		delay += maxDelay;

		return delay;
	}
	
	private List<HIILink> getPath(int sourceId, int destId) {
		IntKey pathKey = new IntKey(sourceId, destId);
		List<HIILink> linkList = pathCache.get(pathKey);
		
		if (linkList == null) {
			HIILink link = getLink(sourceId, destId);
			
			linkList = new ArrayList<HIILink>();
			if (link == null) {
				linkList.addAll(findLinks(sourceId, destId));
			} else {
				linkList.add(link);
			}
			pathCache.put(pathKey, linkList);
		}

		return linkList;
	}
	
	public double calcNetworkCost(int sourceId, int destId) {
		
		IntKey key = new IntKey(sourceId,destId);
		
		if (sourceId == destId) {
			return 0;
		}
		
		Object existCost = networkingUnitCostTable.get(key);
		if (existCost != null) {
			return ((Double)existCost).doubleValue();
		}
		
		HIILink link = getLink(sourceId, destId);
		
		List<HIILink> linkList = new ArrayList<HIILink>();
		if (link == null) {
			linkList.addAll(findLinks(sourceId, destId));
		} else {
			linkList.add(link);
		}
		
		double cost = 0;
		
		for (int i=0;i<linkList.size();i++) {
			HIILink link2 = linkList.get(i);
			
			double unitCost = link2.getNetworkingUnitCost();
			cost += unitCost;
		}
		
		networkingUnitCostTable.put(key, cost);
		
		return cost;
	}
	
	public double calcNetworkCost(double dataSize,int sourceId, int destId) {
		
		IntKey key = new IntKey(sourceId,destId);
		
		if (sourceId == destId) {
			return 0;
		}
		
		HIILink link = getLink(sourceId, destId);
		
		List<HIILink> linkList = new ArrayList<HIILink>();
		if (link == null) {
			linkList.addAll(findLinks(sourceId, destId));
		} else {
			linkList.add(link);
		}
		
		double cost = 0;
		
		for (int i=0;i<linkList.size();i++) {
			HIILink link2 = linkList.get(i);
			double unitCost = 0;
			unitCost = link2.getNetworkingUnitCost() * dataSize;
			cost += unitCost;
		}
		
		networkingUnitCostTable.put(key, cost);
		
		return cost;
	}
	
	// obsolete
	public double calcInputDataNetworkCost(int dataSize,int sourceId, int destId) {
		
		IntKey key = new IntKey(sourceId,destId);
		
		if (sourceId == destId) {
			return 0;
		}
		
		HIILink link = getLink(sourceId, destId);
		
		List<HIILink> linkList = new ArrayList<HIILink>();
		if (link == null) {
			linkList.addAll(findLinks(sourceId, destId));
		} else {
			linkList.add(link);
		}
		
		double cost = 0;
		
		for (int i=0;i<linkList.size();i++) {
			HIILink link2 = linkList.get(i);
			double unitCost = 0;
			unitCost = link2.getNetworkingUnitCost() * dataSize;
			cost += unitCost;
		}
		
		networkingUnitCostTable.put(key, cost);
		
		return cost;
	}
	
	
	// obsolete
	public double calcOutputDataNetworkCost(int dataSize,int sourceId, int destId) {
		
		IntKey key = new IntKey(sourceId,destId);
		
		if (sourceId == destId) {
			return 0;
		}
		
		HIILink link = getLink(sourceId, destId);
		
		List<HIILink> linkList = new ArrayList<HIILink>();
		if (link == null) {
			linkList.addAll(findLinks(sourceId, destId));
		} else {
			linkList.add(link);
		}
		
		double cost = 0;
		
		for (int i=0;i<linkList.size();i++) {
			HIILink link2 = linkList.get(i);
			double unitCost = 0;
			unitCost = link2.getNetworkingUnitCost() * dataSize;
			cost += unitCost;
		}
		
		networkingUnitCostTable.put(key, cost);
		
		return cost;
	}
	
	private Map<IntKey,Double> networkingUnitCostTable = new HashMap<IntKey,Double>();
	private Map<IntKey,List<HIILink>> pathCache = new HashMap<IntKey,List<HIILink>>();
}
