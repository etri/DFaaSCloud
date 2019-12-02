package org.faas.utils;

import java.util.HashMap;
import java.util.Map;

public class DFaaSUtils {
	private static int ENTITY_ID = 1;

	public static int generateEntityId(){
		return ENTITY_ID++;
	}

	//public static int MAX = 10000000;
	public static final int MAX = 10000000;
}
