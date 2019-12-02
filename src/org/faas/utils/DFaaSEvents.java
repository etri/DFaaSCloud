package org.faas.utils;

public class DFaaSEvents {

	private static final int BASE = 100;
	public static final int EMIT_FUNCTION_REQ = BASE + 1;
	public static final int INP_FUNCTION_REQ_ARRIVAL = BASE + 2;
	public static final int NG_FUNCTION_REQ_ARRIVAL = BASE + 3;
	public static final int FUNCTION_COMPLETE = BASE + 4;
	public static final int ACTUATOR_FUNCTION_RSP_ARRIVAL = BASE + 5;
	public static final int UPDATE_RESOURCE_USAGE = BASE + 6;
	public static final int FUNCTION_TRAFFIC_SEGMENT_END = BASE + 7;

}
