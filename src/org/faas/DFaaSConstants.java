package org.faas;

public class DFaaSConstants {

	public static final int CORE_NODE_GROUP = 1;
	public static final int EDGE_NODE_GROUP = 2;
	public static final int FOG_NODE_GROUP = 3;
	public static final int END_EDVICE_GROUP = 4;
	public static final int SENSOR = 5;
	public static final int ACTUATOR = 6;
	
	private static final String NODE_NAMES[] = { ""
			, "CORE" 
			, "EDGE"
			, "FOG"
			, "END_DEVICE"
			, "SENSOR"
			, "ACTUATOR" };
	
	public static String getEntityName(int i) {
		return NODE_NAMES[i];
	}
}
