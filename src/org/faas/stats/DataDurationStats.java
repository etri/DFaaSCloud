package org.faas.stats;

import java.io.Serializable;

import org.faas.DFaaSConstants;

public class DataDurationStats implements Serializable {
	  private StatValue durationOfEDData = new StatValue();
	  private StatValue durationOfFogData = new StatValue();
	  private StatValue durationOfEdgeData = new StatValue();
	  private StatValue durationOfCoreData = new StatValue();
	  
	  public void updateStats(int deviceType,double duration) {
		  if (deviceType == DFaaSConstants.END_EDVICE_GROUP || deviceType == DFaaSConstants.SENSOR || deviceType == DFaaSConstants.ACTUATOR) {
			  durationOfEDData.addValue(duration);
		  } else if (deviceType == DFaaSConstants.CORE_NODE_GROUP) {
			  durationOfCoreData.addValue(duration);
		  } else if (deviceType == DFaaSConstants.EDGE_NODE_GROUP) {
			  durationOfEdgeData.addValue(duration);
		  } else if (deviceType == DFaaSConstants.FOG_NODE_GROUP) {
			  durationOfFogData.addValue(duration);
		  } else {
			  System.err.println(this.getClass().getName()+".updateStats can NOT happen. deviceType="+deviceType);
		  }
	  }

	public StatValue getDurationOfEDData() {
		return durationOfEDData;
	}

	public StatValue getDurationOfFogData() {
		return durationOfFogData;
	}

	public StatValue getDurationOfEdgeData() {
		return durationOfEdgeData;
	}

	public StatValue getDurationOfCoreData() {
		return durationOfCoreData;
	}
	  
}
