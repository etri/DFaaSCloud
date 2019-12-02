package org.faas.stats;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.faas.DFaaSConstants;
import org.faas.application.DataLocationAndSize;

/**
 * data size stats
 * 
 * @author javajune
 *
 */
public class DataStats implements Serializable {

	// CWE-495 private -> public
	// node type, stat value
	public Map<Integer,StatValue> dataStatMap = new HashMap<Integer,StatValue>();
	
	public void addValue(List<DataLocationAndSize> list) {
		for (int i=0;i<list.size();i++) {
			DataLocationAndSize data = list.get(i);
			addValue(data);
		}
	}
	
	private void addValue(DataLocationAndSize data) {
		int type = data.getType();
		
		StatValue statValue = dataStatMap.get(type);
		if (statValue == null) {
			statValue = new StatValue("mb");
			dataStatMap.put(type, statValue);
		}
		
		statValue.addValue(data.getSize());
	}
	
	public Map<Integer,StatValue> getDataStatMap() {
		return dataStatMap;
	}
	
	public StatValue getEdDataStat() {
		return dataStatMap.get(DFaaSConstants.END_EDVICE_GROUP);
	}
	
	public StatValue getCoreDataStat() {
		return dataStatMap.get(DFaaSConstants.CORE_NODE_GROUP);
	}
	
	public StatValue getEdgeDataStat() {
		return dataStatMap.get(DFaaSConstants.EDGE_NODE_GROUP);
	}
	
	public StatValue getFogDataStat() {
		return dataStatMap.get(DFaaSConstants.FOG_NODE_GROUP);
	}
	
	public List<StatValue> getDataSizeStats() {
		return new ArrayList<StatValue>(dataStatMap.values());
	}
	
	public String toPrintString() {
		
		StringBuffer sb = new StringBuffer();

		Iterator<Integer> ite =  dataStatMap.keySet().iterator();
		while (ite.hasNext()) {
			int key = ite.next();
			StatValue statValue = dataStatMap.get(key);
			sb.append("type = ").append(key).append("(").append(DFaaSConstants.getEntityName(key)).append("), ")
				.append(statValue.toPrintString()).append("\n");
		}
		
		return sb.toString();
	}
}
